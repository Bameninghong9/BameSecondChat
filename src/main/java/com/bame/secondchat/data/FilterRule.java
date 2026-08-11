package com.bame.secondchat.data;

public interface FilterRule {
    /**
     * Überprüft, ob die Nachricht auf diese Regel zutrifft.
     * @param message Der Text der Chat-Nachricht (plain text)
     * @return true, wenn die Regel zutrifft, ansonsten false.
     */
    boolean matches(String message);
    
    /**
     * Gibt den Typ der Regel zurück, wird für die JSON-Serialisierung verwendet.
     */
    String getType();
    
    /**
     * Gibt den Wert der Regel zurück.
     */
    String getValue();
}
