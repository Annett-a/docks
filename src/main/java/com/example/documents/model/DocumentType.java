package com.example.documents.model;

public enum DocumentType {
    CARD_OPENED,
    CARD_CLOSED,
    TRANSFER_RECEIPT;

    public static boolean isValid(String s) {
        try { valueOf(s); return true; }
        catch (Exception e) { return false; }
    }
}
