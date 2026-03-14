package com.example.documents.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardInfoResponse {
    private String message;
    private CardInfo data;
}