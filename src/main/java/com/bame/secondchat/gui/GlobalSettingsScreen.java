package com.bame.secondchat.gui;

import com.bame.secondchat.config.GlobalConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class GlobalSettingsScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget searchField;
    
    private TextFieldWidget maxMessagesField;
    private TextFieldWidget timestampFormatField;
    private TextFieldWidget timestampColorField;
    private TextFieldWidget selectionColorField;
    
    private TextFieldWidget stackMessagesField;
    private ButtonWidget timestampColorResetButton;
    private ButtonWidget selectionColorResetButton;
    private ButtonWidget showFontDropdownButton;
    private ButtonWidget showEmojiButtonButton;
    private ButtonWidget showPlayerHeadsButton;
    
    private ButtonWidget saveButton;

    public GlobalSettingsScreen(Screen parent) {
        super(Text.literal("Global Settings - SecondChat"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        
        int startY = 50;
        int fieldWidth = 200;
        int fieldHeight = 20;
        int xOffset = this.width / 2 - fieldWidth / 2;
        
        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 15, 200, 20, Text.literal("Search"));
        this.searchField.setPlaceholder(Text.literal("Search settings..."));
        this.addDrawableChild(this.searchField);
        
        GlobalConfig config = GlobalConfig.getInstance();
        
        this.maxMessagesField = new TextFieldWidget(this.textRenderer, xOffset, startY, fieldWidth, fieldHeight, Text.literal("Max Messages"));
        this.maxMessagesField.setText(String.valueOf(config.maxMessages));
        this.addDrawableChild(this.maxMessagesField);
        
        this.timestampFormatField = new TextFieldWidget(this.textRenderer, xOffset, startY + 30, fieldWidth, fieldHeight, Text.literal("Timestamp Format"));
        this.timestampFormatField.setMaxLength(50);
        this.timestampFormatField.setText(config.timestampFormat);
        this.addDrawableChild(this.timestampFormatField);
        
        this.timestampColorField = new TextFieldWidget(this.textRenderer, xOffset, startY + 60, fieldWidth, fieldHeight, Text.literal("Timestamp Color"));
        this.timestampColorField.setMaxLength(20);
        this.timestampColorField.setText(config.timestampColor);
        this.addDrawableChild(this.timestampColorField);
        
        this.selectionColorField = new TextFieldWidget(this.textRenderer, xOffset, startY + 90, fieldWidth, fieldHeight, Text.literal("Selection Color"));
        this.selectionColorField.setMaxLength(20);
        this.selectionColorField.setText(config.selectionColor);
        this.addDrawableChild(this.selectionColorField);
        
        this.stackMessagesField = new TextFieldWidget(this.textRenderer, xOffset, startY + 120, fieldWidth, fieldHeight, Text.literal("Stack Messages"));
        this.stackMessagesField.setText(String.valueOf(config.stackMessages));
        this.addDrawableChild(this.stackMessagesField);
        
        this.timestampColorResetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            this.timestampColorField.setText("§7");
        }).dimensions(xOffset + fieldWidth + 5, startY + 60, 50, 20).build();
        this.addDrawableChild(this.timestampColorResetButton);
        
        this.selectionColorResetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            this.selectionColorField.setText("#880000FF");
        }).dimensions(xOffset + fieldWidth + 5, startY + 90, 50, 20).build();
        this.addDrawableChild(this.selectionColorResetButton);
        
        Text initialText = Text.literal(com.bame.secondchat.config.ModConfig.showFontDropdown ? "Yes" : "No")
            .withColor(com.bame.secondchat.config.ModConfig.showFontDropdown ? 0x00FF00 : 0xFF0000);
            
        this.showFontDropdownButton = ButtonWidget.builder(initialText, button -> {
            com.bame.secondchat.config.ModConfig.showFontDropdown = !com.bame.secondchat.config.ModConfig.showFontDropdown;
            Text newText = Text.literal(com.bame.secondchat.config.ModConfig.showFontDropdown ? "Yes" : "No")
                .withColor(com.bame.secondchat.config.ModConfig.showFontDropdown ? 0x00FF00 : 0xFF0000);
            button.setMessage(newText);
        }).dimensions(xOffset, startY + 150, fieldWidth, 20).build();
        this.addDrawableChild(this.showFontDropdownButton);
        
        Text initialEmojiText = Text.literal(com.bame.secondchat.config.ModConfig.showEmojiButton ? "Yes" : "No")
            .withColor(com.bame.secondchat.config.ModConfig.showEmojiButton ? 0x00FF00 : 0xFF0000);
            
        this.showEmojiButtonButton = ButtonWidget.builder(initialEmojiText, button -> {
            com.bame.secondchat.config.ModConfig.showEmojiButton = !com.bame.secondchat.config.ModConfig.showEmojiButton;
            Text newText = Text.literal(com.bame.secondchat.config.ModConfig.showEmojiButton ? "Yes" : "No")
                .withColor(com.bame.secondchat.config.ModConfig.showEmojiButton ? 0x00FF00 : 0xFF0000);
            button.setMessage(newText);
        }).dimensions(xOffset, startY + 180, fieldWidth, 20).build();
        this.addDrawableChild(this.showEmojiButtonButton);
        
        Text initialHeadsText = Text.literal(com.bame.secondchat.config.ModConfig.showPlayerHeads ? "Yes" : "No")
            .withColor(com.bame.secondchat.config.ModConfig.showPlayerHeads ? 0x00FF00 : 0xFF0000);
            
        this.showPlayerHeadsButton = ButtonWidget.builder(initialHeadsText, button -> {
            com.bame.secondchat.config.ModConfig.showPlayerHeads = !com.bame.secondchat.config.ModConfig.showPlayerHeads;
            Text newText = Text.literal(com.bame.secondchat.config.ModConfig.showPlayerHeads ? "Yes" : "No")
                .withColor(com.bame.secondchat.config.ModConfig.showPlayerHeads ? 0x00FF00 : 0xFF0000);
            button.setMessage(newText);
        }).dimensions(xOffset, startY + 210, fieldWidth, 20).build();
        this.addDrawableChild(this.showPlayerHeadsButton);
        
        this.saveButton = ButtonWidget.builder(Text.literal("Save & Close"), button -> {
            saveSettings();
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height - 30, 200, 20).build();
        
        this.addDrawableChild(this.saveButton);
    }
    
    private void saveSettings() {
        GlobalConfig config = GlobalConfig.getInstance();
        try {
            config.maxMessages = Integer.parseInt(this.maxMessagesField.getText());
        } catch (NumberFormatException e) {
            config.maxMessages = 0;
        }
        try {
            config.stackMessages = Integer.parseInt(this.stackMessagesField.getText());
        } catch (NumberFormatException e) {
            config.stackMessages = 0;
        }
        config.timestampFormat = this.timestampFormatField.getText();
        config.timestampColor = this.timestampColorField.getText();
        config.selectionColor = this.selectionColorField.getText();
        
        GlobalConfig.save();
    }

    private int parseColorForPreview(String colorStr) {
        if (colorStr == null || colorStr.trim().isEmpty()) return 0xFF000000;
        if (colorStr.startsWith("§")) {
            // Very basic mapping for legacy codes
            char c = colorStr.length() > 1 ? colorStr.charAt(1) : 'f';
            return switch (c) {
                case '0' -> 0xFF000000; case '1' -> 0xFF0000AA; case '2' -> 0xFF00AA00; case '3' -> 0xFF00AAAA;
                case '4' -> 0xFFAA0000; case '5' -> 0xFFAA00AA; case '6' -> 0xFFFFAA00; case '7' -> 0xFFAAAAAA;
                case '8' -> 0xFF555555; case '9' -> 0xFF5555FF; case 'a' -> 0xFF55FF55; case 'b' -> 0xFF55FFFF;
                case 'c' -> 0xFFFF5555; case 'd' -> 0xFFFF55FF; case 'e' -> 0xFFFFFF55; case 'f' -> 0xFFFFFFFF;
                default -> 0xFFFFFFFF;
            };
        }
        if (colorStr.startsWith("#")) {
            try {
                long val = Long.parseLong(colorStr.substring(1), 16);
                if (colorStr.length() == 7) { // #RRGGBB
                    return (int) (0xFF000000 | val);
                } else if (colorStr.length() == 9) { // #AARRGGBB
                    return (int) val;
                }
            } catch (Exception ignored) {}
        }
        return 0xFF000000;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFFFF);
        
        int startY = 50;
        int fieldWidth = 200;
        int xOffset = this.width / 2 - fieldWidth / 2;
        
        String search = this.searchField.getText().toLowerCase();
        
        // Filter logic for drawing
        boolean showMaxMsg = "max messages".contains(search) || search.isEmpty();
        boolean showTsFormat = "timestamp format".contains(search) || search.isEmpty();
        boolean showTsColor = "timestamp color".contains(search) || search.isEmpty();
        boolean showSelColor = "selection color".contains(search) || search.isEmpty();
        boolean showStackMsg = "stack messages".contains(search) || search.isEmpty();
        
        this.maxMessagesField.visible = showMaxMsg;
        this.timestampFormatField.visible = showTsFormat;
        this.timestampColorField.visible = showTsColor;
        this.selectionColorField.visible = showSelColor;
        this.stackMessagesField.visible = showStackMsg;
        
        this.timestampColorResetButton.visible = showTsColor;
        this.selectionColorResetButton.visible = showSelColor;
        
        boolean showFontDropdownSetting = "font dropdown".contains(search) || search.isEmpty();
        this.showFontDropdownButton.visible = showFontDropdownSetting;
        
        boolean showEmojiBtnSetting = "emoji button".contains(search) || search.isEmpty();
        this.showEmojiButtonButton.visible = showEmojiBtnSetting;
        
        boolean showHeadsBtnSetting = "player heads skin".contains(search) || search.isEmpty();
        this.showPlayerHeadsButton.visible = showHeadsBtnSetting;
        
        int currentY = startY;
        
        if (showMaxMsg) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Max Messages (0 = unendlich)"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.maxMessagesField.setY(currentY);
            currentY += 40;
        }
        
        if (showTsFormat) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Timestamp Format"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.timestampFormatField.setY(currentY);
            currentY += 40;
        }
        
        if (showTsColor) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Timestamp Color"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.timestampColorField.setY(currentY);
            this.timestampColorResetButton.setY(currentY);
            
            int color = parseColorForPreview(this.timestampColorField.getText());
            context.fill(xOffset - 26, currentY - 1, xOffset - 4, currentY + 21, 0xFFFFFFFF);
            context.fill(xOffset - 25, currentY, xOffset - 5, currentY + 20, color);
            
            currentY += 40;
        }
        
        if (showSelColor) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Selection Color (Hex)"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.selectionColorField.setY(currentY);
            this.selectionColorResetButton.setY(currentY);
            
            int color = parseColorForPreview(this.selectionColorField.getText());
            context.fill(xOffset - 26, currentY - 1, xOffset - 4, currentY + 21, 0xFFFFFFFF);
            context.fill(xOffset - 25, currentY, xOffset - 5, currentY + 20, color);
            
            currentY += 40;
        }
        
        if (showStackMsg) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Stack Messages (0 = aus)"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.stackMessagesField.setY(currentY);
            currentY += 40;
        }
        
        if (showFontDropdownSetting) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Show Font Dropdown"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.showFontDropdownButton.setY(currentY);
            currentY += 40;
        }
        
        if (showEmojiBtnSetting) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Show Emoji Button"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.showEmojiButtonButton.setY(currentY);
            currentY += 40;
        }
        
        if (showHeadsBtnSetting) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Show Player Heads"), xOffset, currentY - 10, 0xFFAAAAAA);
            this.showPlayerHeadsButton.setY(currentY);
            currentY += 40;
        }
        
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
