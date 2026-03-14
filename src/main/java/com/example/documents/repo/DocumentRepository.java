package com.example.documents.repo;

import com.example.documents.model.Document;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {
    Document save(Document d);
    Optional<Document> findById(UUID id);
}
