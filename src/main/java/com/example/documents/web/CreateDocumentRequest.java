package com.example.documents.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateDocumentRequest {
    private String docType;
    private String userId;
    private String userFio;
    private String cardNumber;
}