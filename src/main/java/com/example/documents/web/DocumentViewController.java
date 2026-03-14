package com.example.documents.web;

import com.example.documents.model.Document;
import com.example.documents.service.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class DocumentViewController {

    private final DocumentService service;

    @GetMapping("/documents/{id}")
    public String viewDocument(@PathVariable("id") String id, Model model, HttpServletResponse response) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<Document> opt = service.get(uuid);

            if (opt.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                model.addAttribute("errorTitle", "Документ не найден");
                model.addAttribute("errorText", "В базе нет документа с указанным ID.");
                model.addAttribute("missingId", id);
                return "not-found";
            }

            Document doc = opt.get();
            model.addAttribute("doc", doc);
            model.addAttribute("createdAt", Date.from(doc.getCreatedDate()));
            return "document";

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("errorTitle", "Некорректный идентификатор");
            model.addAttribute("errorText", "ID должен быть в формате UUID.");
            model.addAttribute("missingId", id);
            return "not-found";
        }
    }
}