package com.bame.secondchat.data;

public class ContainsRule implements FilterRule {
    private final String value;
    private final String lowerValue;

    public ContainsRule(String value) {
        this.value = value;
        this.lowerValue = value != null ? value.toLowerCase() : "";
    }

    @Override
    public boolean matches(String message) {
        if (message == null || value == null) return false;
        return message.toLowerCase().contains(lowerValue);
    }

    @Override
    public String getType() {
        return "contains";
    }

    @Override
    public String getValue() {
        return value;
    }
}
