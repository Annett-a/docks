package com.example.documents.web;

import com.example.documents.model.Document;
import com.example.documents.model.DocumentType;
import com.example.documents.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentApiController {

    private final DocumentService service;
    private final CardsClient cardsClient;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        final UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return error(HttpStatus.BAD_REQUEST, "INVALID_ID", "Invalid UUID format",
                    Map.of("id", id));
        }

        Optional<Document> found = service.get(uuid);
        if (found.isEmpty()) {
            return error(HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found",
                    Map.of("id", uuid.toString()));
        }

        return ResponseEntity.ok(found.get());
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> create(@RequestBody CreateDocumentRequest dto, HttpServletRequest request) {
        final DocumentType type;
        try {
            type = DocumentType.valueOf(dto.getDocType());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, "INVALID_DOC_TYPE", "Unknown docType",
                    Map.of("docType", String.valueOf(dto.getDocType())));
        }

        final UUID userId;
        try {
            userId = UUID.fromString(dto.getUserId());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, "INVALID_USER_ID", "Invalid UUID format for userId",
                    Map.of("userId", String.valueOf(dto.getUserId())));
        }

        try {
            service.validate(type, userId, dto.getUserFio(), dto.getCardNumber());
        } catch (IllegalArgumentException ex) {
            return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Input validation error",
                    Map.of("details", ex.getMessage()));
        }

        Document created = service.create(type, userId, dto.getUserFio(), dto.getCardNumber());
        String location = request.getContextPath() + "/api/documents/" + created.getId();

        return ResponseEntity.created(URI.create(location)).body(created);
    }

    @GetMapping(value = "/open/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOpenByPath(@PathVariable("userId") String userId, HttpServletRequest request) {
        return createFromCards(userId, DocumentType.CARD_OPENED, request);
    }

    @GetMapping(value = "/close/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCloseByPath(@PathVariable("userId") String userId, HttpServletRequest request) {
        return createFromCards(userId, DocumentType.CARD_CLOSED, request);
    }

    @GetMapping(value = "/open-by-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOpenByQuery(
            @RequestParam(name = "userId", required = false) String userId,
            HttpServletRequest request
    ) {
        return createFromCards(userId, DocumentType.CARD_OPENED, request);
    }

    @GetMapping(value = "/close-by-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCloseByQuery(
            @RequestParam(name = "userId", required = false) String userId,
            HttpServletRequest request
    ) {
        return createFromCards(userId, DocumentType.CARD_CLOSED, request);
    }

    @GetMapping(value = "/user/open/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOpenDocumentByCard(@PathVariable("cardId") String cardId) {
        return getDocumentByCard(cardId, true);
    }

    @GetMapping(value = "/user/close/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCloseDocumentByCard(@PathVariable("cardId") String cardId) {
        return getDocumentByCard(cardId, false);
    }

    private ResponseEntity<?> createFromCards(String userIdParam, DocumentType type, HttpServletRequest request) {
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            return error(HttpStatus.BAD_REQUEST, "MISSING_USER_ID",
                    "User id is required (either as query parameter userId or in URL path)", null);
        }

        final UUID userId;
        try {
            userId = UUID.fromString(userIdParam.trim());
        } catch (IllegalArgumentException e) {
            return error(HttpStatus.BAD_REQUEST, "INVALID_USER_ID",
                    "Invalid UUID format for userId", Map.of("userId", userIdParam));
        }

        final CardInfo cardInfo;
        try {
            cardInfo = cardsClient.fetchCardInfo(userId.toString());
        } catch (IOException e) {
            return error(HttpStatus.BAD_GATEWAY, "CARDS_API_ERROR",
                    "Failed to call cards service", null);
        }

        if (cardInfo == null) {
            return error(HttpStatus.NOT_FOUND, "CARD_NOT_FOUND",
                    "Card not found for given userId", Map.of("userId", userId.toString()));
        }

        String cardNumber = cardInfo.getPlasticName();
        String userFio = (cardInfo.getCardName() != null && !cardInfo.getCardName().trim().isEmpty())
                ? cardInfo.getCardName()
                : "Unknown user";

        try {
            Document created = service.create(type, userId, userFio, cardNumber);
            String location = request.getContextPath() + "/api/documents/" + created.getId();
            return ResponseEntity.created(URI.create(location)).body(created);
        } catch (IllegalArgumentException ex) {
            return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
                    "Input validation error", Map.of("details", ex.getMessage()));
        }
    }

    private ResponseEntity<?> getDocumentByCard(String cardId, boolean isOpen) {
        if (cardId == null || cardId.trim().isEmpty()) {
            return error(HttpStatus.BAD_REQUEST, "MISSING_CARD_ID",
                    "Card id is required in URL path", null);
        }

        final CardInfo cardInfo;
        try {
            cardInfo = cardsClient.fetchCardInfo(cardId);
        } catch (IOException e) {
            return error(HttpStatus.BAD_GATEWAY, "CARDS_API_ERROR",
                    "Failed to call cards service", null);
        }

        if (cardInfo == null) {
            return error(HttpStatus.NOT_FOUND, "CARD_NOT_FOUND",
                    "Card not found for given cardId", Map.of("cardId", cardId));
        }

        String documentIdStr = isOpen ? cardInfo.getOpenDocument() : cardInfo.getCloseDocument();

        if (documentIdStr == null || documentIdStr.trim().isEmpty()) {
            String code = isOpen ? "OPEN_DOCUMENT_NOT_FOUND" : "CLOSE_DOCUMENT_NOT_FOUND";
            String message = isOpen
                    ? "Open document id not found for given cardId"
                    : "Close document id not found for given cardId";

            return error(HttpStatus.NOT_FOUND, code, message, Map.of("cardId", cardId));
        }

        final UUID documentId;
        try {
            documentId = UUID.fromString(documentIdStr.trim());
        } catch (IllegalArgumentException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "INVALID_DOCUMENT_ID",
                    "Card service returned invalid document id for this card",
                    Map.of("cardId", cardId, "documentId", documentIdStr));
        }

        Optional<Document> found = service.get(documentId);
        if (found.isEmpty()) {
            return error(HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND",
                    "Document not found", Map.of("documentId", documentId.toString()));
        }

        return ResponseEntity.ok(found.get());
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status,
            String code,
            String message,
            Map<String, ?> extra
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", status.value());
        payload.put("error", code);
        payload.put("message", message);

        if (extra != null) {
            payload.putAll(extra);
        }

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload);
    }
}