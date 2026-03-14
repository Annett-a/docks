package com.example.documents.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardInfo {
    private String userId;
    private String cardProductId;
    private String plasticName;
    private String expDate;
    private String contractName;
    private String cardName;
    private String openDocument;
    private String closeDocument;
    private Boolean closeFlag;
}