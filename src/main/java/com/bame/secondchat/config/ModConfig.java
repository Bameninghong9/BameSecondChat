package com.bame.secondchat.config;

import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.FilterRule;
import com.bame.secondchat.data.TabManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(FilterRule.class, new FilterRuleAdapter())
            .setPrettyPrinting()
            .create();

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bamesecondchat.json");
    
    public static boolean showFontDropdown = true;
    public static boolean showEmojiButton = true;

    public static class ConfigData {
        public int hudX = -1;
        public int hudY = -1;
        public boolean showFontDropdown = true;
        public boolean showEmojiButton = true;
        public List<ChatTab> tabs;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    if (data.tabs != null) {
                        TabManager.getInstance().loadTabs(data.tabs);
                    }
                    TabManager.getInstance().setHudX(data.hudX);
                    TabManager.getInstance().setHudY(data.hudY);
                    
                    // Default to true if not present, but we don't have a direct instance variable for it outside config
                    // We will just keep it statically accessible
                    ModConfig.showFontDropdown = data.showFontDropdown;
                    ModConfig.showEmojiButton = data.showEmojiButton;
                }
            } catch (Exception e) {
                System.err.println("[BameSecondChat] Failed to load config, saving defaults: " + e.getMessage());
                save();
            }
        } else {
            // Save defaults
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            ConfigData data = new ConfigData();
            data.hudX = TabManager.getInstance().getHudX();
            data.hudY = TabManager.getInstance().getHudY();
            data.showFontDropdown = ModConfig.showFontDropdown;
            data.showEmojiButton = ModConfig.showEmojiButton;
            
            data.tabs = TabManager.getInstance().getTabs();
            
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
