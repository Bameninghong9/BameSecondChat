package com.bame.secondchat.data;

public class CapturesBlockRule implements FilterRule {
    private final String value;
    private final String lowerValue;

    public CapturesBlockRule(String value) {
        this.value = value;
        this.lowerValue = value != null ? value.toLowerCase() : "";
    }

    @Override
    public boolean matches(String message) {
        if (message == null || value == null) return false;
        return message.toLowerCase().startsWith(lowerValue);
    }

    @Override
    public String getType() {
        return "captures_block";
    }

    @Override
    public String getValue() {
        return value;
    }
}
