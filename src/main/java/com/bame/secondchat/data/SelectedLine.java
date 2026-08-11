package com.bame.secondchat.data;

import java.util.Objects;

public class SelectedLine {
    private final ChatMessage message;
    private final int lineIndex;

    public SelectedLine(ChatMessage message, int lineIndex) {
        this.message = message;
        this.lineIndex = lineIndex;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectedLine that = (SelectedLine) o;
        return lineIndex == that.lineIndex && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, lineIndex);
    }
}
