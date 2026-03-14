package com.example.documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private UUID id;
    private DocumentType docType;
    private Instant createdDate;
    private UUID userId;
    private String userFio;
    private String cardNumber;
}