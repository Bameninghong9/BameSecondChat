package com.bame.secondchat.data;

import java.util.ArrayList;
import java.util.List;

public class ChatTab {
    private String name;
    private List<FilterRule> rules;
    private boolean hideFromAll;
    
    // Layout infos
    private int x;
    private int y;
    private int width;
    private int height;
    
    // Runtime data, will not be serialized to JSON
    private transient List<ChatMessage> messages = new ArrayList<>();
    
    // Optional: Keep track of scroll offset per tab
    private transient double scrollOffset = 0;
    
    // Unread message count
    private transient int unreadCount = 0;
    
    // Selected lines for screenshot feature
    private transient java.util.Set<SelectedLine> selectedLines = new java.util.HashSet<>();

    public ChatTab(String name, boolean hideFromAll) {
        this.name = name;
        this.rules = new ArrayList<>();
        this.hideFromAll = hideFromAll;
        this.x = 4;
        this.y = 4;
        this.width = 300;
        this.height = 150;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterRule> getRules() {
        if (this.rules == null) {
            this.rules = new ArrayList<>();
        }
        return rules;
    }

    public void addRule(FilterRule rule) {
        getRules().add(rule);
    }
    
    public void removeRule(FilterRule rule) {
        getRules().remove(rule);
    }

    public boolean isHideFromAll() {
        return hideFromAll;
    }

    public void setHideFromAll(boolean hideFromAll) {
        this.hideFromAll = hideFromAll;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        if (width <= 0) {
            width = 300;
        }
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        if (height <= 0) {
            height = 150;
        }
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<ChatMessage> getMessages() {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        return this.messages;
    }

    public void addMessage(ChatMessage message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        
        int stackLimit = com.bame.secondchat.config.GlobalConfig.getInstance().stackMessages;
        if (stackLimit > 0 && !this.messages.isEmpty()) {
            ChatMessage lastMessage = this.messages.get(this.messages.size() - 1);
            if (lastMessage.getMessage().getString().equals(message.getMessage().getString())) {
                if (lastMessage.getStackCount() < stackLimit) {
                    lastMessage.incrementStackCount();
                    return;
                }
            }
        }
        
        this.messages.add(message);
        
        int max = com.bame.secondchat.config.GlobalConfig.getInstance().maxMessages;
        if (max > 0) {
            while (this.messages.size() > max) {
                ChatMessage removed = this.messages.remove(0);
                getSelectedLines().removeIf(sl -> sl.getMessage() == removed);
            }
        }
    }
    
    public int getUnreadCount() {
        return unreadCount;
    }
    
    public void incrementUnread() {
        this.unreadCount++;
    }
    
    public void resetUnread() {
        this.unreadCount = 0;
    }
    
    public void clearMessages() {
        if (this.messages != null) this.messages.clear();
        getSelectedLines().clear();
    }
    
    public double getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(double scrollOffset) {
        this.scrollOffset = scrollOffset;
    }
    
    public java.util.Set<SelectedLine> getSelectedLines() {
        if (this.selectedLines == null) {
            this.selectedLines = new java.util.HashSet<>();
        }
        return this.selectedLines;
    }
    
    public void toggleSelection(ChatMessage msg, int lineIndex) {
        SelectedLine sl = new SelectedLine(msg, lineIndex);
        if (getSelectedLines().contains(sl)) {
            getSelectedLines().remove(sl);
        } else {
            getSelectedLines().add(sl);
        }
    }
    
    public boolean isSelected(ChatMessage msg, int lineIndex) {
        return getSelectedLines().contains(new SelectedLine(msg, lineIndex));
    }
    
    public void clearSelection() {
        getSelectedLines().clear();
    }
}
