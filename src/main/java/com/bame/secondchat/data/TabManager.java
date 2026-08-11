package com.bame.secondchat.data;

import java.util.ArrayList;
import java.util.List;

public class TabManager {
    private static final TabManager INSTANCE = new TabManager();
    
    private final List<ChatTab> tabs = new ArrayList<>();
    private ChatTab activeTab;
    private final ChatTab allTab;

    // Global positions for the Custom Chat HUD
    private int hudX = -1;
    private int hudY = -1;

    private TabManager() {
        allTab = new ChatTab("All", false);
        tabs.add(allTab);
        activeTab = allTab;
    }

    public static TabManager getInstance() {
        return INSTANCE;
    }

    public List<ChatTab> getTabs() {
        return tabs;
    }

    public ChatTab getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(ChatTab tab) {
        this.activeTab = tab;
        if (tab != null) {
            tab.resetUnread();
        }
    }

    public ChatTab getAllTab() {
        return allTab;
    }

    public void addTab(ChatTab tab) {
        if (!tabs.contains(tab)) {
            tabs.add(tab);
        }
    }

    public void removeTab(ChatTab tab) {
        if (tab != allTab) {
            tabs.remove(tab);
            if (activeTab == tab) {
                activeTab = allTab;
            }
        }
    }

    /**
     * Processes an incoming message and routes it to the matching tabs.
     * @param message The message object
     * @param plainText The plain text representation for filtering
     * @return true if the message should be hidden from the Vanilla chat (All-Tab).
     */
    public boolean processMessage(ChatMessage message, String plainText) {
        boolean hideFromVanilla = false;
        
        boolean addToAll = true;

        for (ChatTab tab : tabs) {
            if (tab == allTab) continue;

            boolean matches = false;
            for (FilterRule rule : tab.getRules()) {
                if (rule.matches(plainText)) {
                    matches = true;
                    break;
                }
            }

            if (matches) {
                tab.addMessage(message);
                if (tab != activeTab) {
                    tab.incrementUnread();
                }
                if (tab.isHideFromAll()) {
                    hideFromVanilla = true;
                    addToAll = false;
                }
            }
        }

        if (addToAll) {
            allTab.addMessage(message);
            if (allTab != activeTab) {
                allTab.incrementUnread();
            }
        }

        return hideFromVanilla;
    }
    
    /**
     * Ersetzt die aktuellen Tabs durch geladene Tabs aus der Config.
     */
    public void loadTabs(List<ChatTab> loadedTabs) {
        tabs.clear();
        tabs.add(allTab);
        
        for (ChatTab tab : loadedTabs) {
            if (!tab.getName().equalsIgnoreCase("All")) {
                tabs.add(tab);
            }
        }
        
        activeTab = allTab;
    }

    public void updateTab(ChatTab tab, String newName, boolean hideFromAll, List<FilterRule> newRules) {
        tab.setName(newName);
        tab.setHideFromAll(hideFromAll);
        tab.getRules().clear();
        tab.getRules().addAll(newRules);
    }

    public int getHudX() {
        return hudX;
    }

    public void setHudX(int hudX) {
        this.hudX = hudX;
    }

    public int getHudY() {
        return hudY;
    }

    public void setHudY(int hudY) {
        this.hudY = hudY;
    }
}
