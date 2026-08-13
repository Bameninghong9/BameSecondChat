package com.bame.secondchat.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmojiManager {
    
    public static class EmojiCategory {
        public String name;
        public String icon;
        public List<String> symbols; // We store each symbol as a String (can be 1 or 2 chars depending on surrogate pairs)

        public EmojiCategory(String name, String icon, List<String> symbols) {
            this.name = name;
            this.icon = icon;
            this.symbols = symbols;
        }
    }

    private static final Map<String, EmojiCategory> CATEGORIES = new LinkedHashMap<>();
    private static final List<String> ALL_SYMBOLS = new ArrayList<>();
    
    private static final Map<String, String[]> CATEGORY_GROUPS = new LinkedHashMap<>();
    static {
        CATEGORY_GROUPS.put("faces_people", new String[]{"faces.txt", "people.txt", "hands.txt", "body.txt"});
        CATEGORY_GROUPS.put("nature_food", new String[]{"environment.txt", "greenery.txt", "animals.txt", "food.txt"});
        CATEGORY_GROUPS.put("things", new String[]{"things.txt", "clothes.txt"});
        CATEGORY_GROUPS.put("activities_transport_places", new String[]{"activities.txt", "transport.txt"});
        CATEGORY_GROUPS.put("symbols", new String[]{"symbols.txt", "numbers.txt", "arrows.txt"});
        CATEGORY_GROUPS.put("shapes", new String[]{"shapes.txt"});
        CATEGORY_GROUPS.put("kaomojis", new String[]{"kaomojis.txt"});
        CATEGORY_GROUPS.put("misc", new String[]{"misc.txt"});
    }

    public static void load() {
        if (!CATEGORIES.isEmpty()) return; // Already loaded

        for (Map.Entry<String, String[]> entry : CATEGORY_GROUPS.entrySet()) {
            String catName = entry.getKey();
            List<String> symbols = new ArrayList<>();
            for (String file : entry.getValue()) {
                String path = "/assets/bamesecondchat/symbols/" + file;
                try (InputStream is = EmojiManager.class.getResourceAsStream(path)) {
                    if (is != null) {
                        String content = IOUtils.toString(is, StandardCharsets.UTF_8);
                        
                        // Read codepoints correctly (since emojis can be surrogate pairs)
                        for (int i = 0; i < content.length(); ) {
                            int cp = content.codePointAt(i);
                            if (!Character.isWhitespace(cp)) { // ignore newlines and spaces
                                symbols.add(new String(Character.toChars(cp)));
                                ALL_SYMBOLS.add(new String(Character.toChars(cp)));
                            }
                            i += Character.charCount(cp);
                        }
                    } else {
                        System.err.println("[BameSecondChat] Resource not found: " + path);
                    }
                } catch (Exception e) {
                    System.err.println("[BameSecondChat] Failed to load emoji category: " + file);
                    e.printStackTrace();
                }
            }
            CATEGORIES.put(catName, new EmojiCategory(catName, catName, symbols)); // Using catName as the icon identifier
        }
        System.out.println("[BameSecondChat] Loaded " + ALL_SYMBOLS.size() + " emojis across " + CATEGORIES.size() + " categories.");
    }

    public static List<EmojiCategory> getCategories() {
        return new ArrayList<>(CATEGORIES.values());
    }

    public static List<String> search(String query) {
        if (query == null || query.isBlank()) {
            return ALL_SYMBOLS; // Or empty list?
        }
        String lowerQuery = query.toLowerCase().trim();
        List<String> results = new ArrayList<>();
        
        for (String symbol : ALL_SYMBOLS) {
            int cp = symbol.codePointAt(0);
            String name = Character.getName(cp); // Java built-in Unicode name lookup!
            if (name != null && name.toLowerCase().contains(lowerQuery)) {
                results.add(symbol);
            }
        }
        return results;
    }
}
