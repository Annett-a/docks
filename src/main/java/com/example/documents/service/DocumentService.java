package com.example.documents.service;

import com.example.documents.model.Document;
import com.example.documents.model.DocumentType;
import com.example.documents.repo.DocumentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository repo;

    public DocumentService(DocumentRepository repo) {
        this.repo = repo;
    }

    public void validate(DocumentType type, UUID userId, String userFio, String cardNumber) {
        if (type == null) {
            throw new IllegalArgumentException("docType is required");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (userFio == null || userFio.trim().isEmpty()) {
            throw new IllegalArgumentException("userFio is required");
        }
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            throw new IllegalArgumentException("cardNumber must be 16 digits");
        }
    }

    public Document create(DocumentType type, UUID userId, String userFio, String cardNumber) {
        validate(type, userId, userFio, cardNumber);

        Document doc = new Document();
        doc.setId(UUID.randomUUID());
        doc.setDocType(type);
        doc.setCreatedDate(Instant.now());
        doc.setUserId(userId);
        doc.setUserFio(userFio.trim());
        doc.setCardNumber(cardNumber);

        return repo.save(doc);
    }

    public Optional<Document> get(UUID id) {
        Objects.requireNonNull(id, "id");
        return repo.findById(id);
    }
}