package com.bame.secondchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GlobalConfig {
    public int maxMessages = 0; // 0 = infinite
    public int stackMessages = 0; // 0 = disabled, >0 = limit
    public String timestampFormat = "dd.MM.yyyy HH:mm 'Uhr'";
    public String timestampColor = "#AAAAAA"; // default grey
    public String selectionColor = "#880000FF"; // default transparent blue

    private static GlobalConfig instance;
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "secondchat_global.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static GlobalConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, GlobalConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (instance == null) {
            instance = new GlobalConfig();
        }
    }

    public static void save() {
        if (instance == null) return;
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int parseSelectionColor() {
        try {
            if (selectionColor.startsWith("#")) {
                long color = Long.parseLong(selectionColor.substring(1), 16);
                if (selectionColor.length() == 7) {
                    color = color | 0xFF000000L; // add alpha if missing
                }
                return (int) color;
            } else if (selectionColor.startsWith("0x")) {
                long color = Long.parseLong(selectionColor.substring(2), 16);
                return (int) color;
            }
        } catch (Exception e) {}
        return 0x880000FF; // fallback
    }
}
