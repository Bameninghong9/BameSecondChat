package com.bame.secondchat.gui;

import com.bame.secondchat.config.GlobalConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;

public class GlobalSettingsScreen extends Screen {

    private final Screen parent;
    
    private int currentTab = 0; // 0 = Behavior, 1 = Appearance, 2 = Features
    
    private TextFieldWidget maxMessagesField;
    private TextFieldWidget stackMessagesField;
    
    private TextFieldWidget timestampFormatField;
    private TextFieldWidget timestampColorField;
    private TextFieldWidget selectionColorField;
    private ButtonWidget timestampColorResetButton;
    private ButtonWidget selectionColorResetButton;
    private net.minecraft.client.gui.widget.SliderWidget opacitySlider;
    
    private net.minecraft.client.gui.widget.ClickableWidget showFontDropdownButton;
    private net.minecraft.client.gui.widget.ClickableWidget showEmojiButtonButton;
    private net.minecraft.client.gui.widget.ClickableWidget showPlayerHeadsButton;
    
    private ButtonWidget saveButton;
    private ColorPickerWidget colorPicker;
    private String activePickerField = null;

    public GlobalSettingsScreen(Screen parent) {
        super(Text.literal("Global Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        
        com.bame.secondchat.config.GlobalConfig config = com.bame.secondchat.config.GlobalConfig.getInstance();
        
        int panelWidth = 400;
        int panelHeight = 260;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;
        
        int contentX = panelX + 110;
        int widgetX = contentX + 130;
        int widgetW = 140;
        
        // BEHAVIOR (Tab 0)
        this.maxMessagesField = new TextFieldWidget(this.textRenderer, widgetX, panelY + 30, widgetW, 20, Text.literal("Max Messages"));
        this.maxMessagesField.setText(String.valueOf(config.maxMessages));
        this.addDrawableChild(this.maxMessagesField);
        
        this.stackMessagesField = new TextFieldWidget(this.textRenderer, widgetX, panelY + 70, widgetW, 20, Text.literal("Stack Messages"));
        this.stackMessagesField.setText(String.valueOf(config.stackMessages));
        this.addDrawableChild(this.stackMessagesField);
        
        // APPEARANCE (Tab 1)
        this.timestampFormatField = new TextFieldWidget(this.textRenderer, widgetX, panelY + 30, widgetW, 20, Text.literal("Timestamp Format"));
        this.timestampFormatField.setMaxLength(50);
        this.timestampFormatField.setText(config.timestampFormat);
        this.addDrawableChild(this.timestampFormatField);
        
        this.timestampColorField = new TextFieldWidget(this.textRenderer, widgetX + 25, panelY + 70, widgetW - 25 - 50, 20, Text.literal("Timestamp Color"));
        this.timestampColorField.setMaxLength(9);
        this.timestampColorField.setText(config.timestampColor);
        this.addDrawableChild(this.timestampColorField);
        
        this.timestampColorResetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            this.timestampColorField.setText("#AAAAAA");
        }).dimensions(widgetX + widgetW - 45, panelY + 70, 45, 20).build();
        this.addDrawableChild(this.timestampColorResetButton);
        
        this.selectionColorField = new TextFieldWidget(this.textRenderer, widgetX + 25, panelY + 110, widgetW - 25 - 50, 20, Text.literal("Selection Color"));
        this.selectionColorField.setMaxLength(9);
        this.selectionColorField.setText(config.selectionColor);
        this.addDrawableChild(this.selectionColorField);
        
        this.selectionColorResetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            this.selectionColorField.setText("#5555FF");
        }).dimensions(widgetX + widgetW - 45, panelY + 110, 45, 20).build();
        this.addDrawableChild(this.selectionColorResetButton);
        
        this.opacitySlider = new net.minecraft.client.gui.widget.SliderWidget(widgetX, panelY + 150, widgetW, 20, Text.literal(config.chatBackgroundOpacity + "%"), config.chatBackgroundOpacity / 100.0) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal((int)(this.value * 100) + "%"));
            }
            @Override
            protected void applyValue() {
                config.chatBackgroundOpacity = (int)(this.value * 100);
            }
        };
        this.addDrawableChild(this.opacitySlider);
        
        // FEATURES (Tab 2)
        this.showFontDropdownButton = createToggleButton(widgetX, panelY + 30, widgetW, com.bame.secondchat.config.ModConfig.showFontDropdown, val -> com.bame.secondchat.config.ModConfig.showFontDropdown = val);
        this.addDrawableChild(this.showFontDropdownButton);
        
        this.showEmojiButtonButton = createToggleButton(widgetX, panelY + 70, widgetW, com.bame.secondchat.config.ModConfig.showEmojiButton, val -> com.bame.secondchat.config.ModConfig.showEmojiButton = val);
        this.addDrawableChild(this.showEmojiButtonButton);
        
        this.showPlayerHeadsButton = createToggleButton(widgetX, panelY + 110, widgetW, com.bame.secondchat.config.ModConfig.showPlayerHeads, val -> com.bame.secondchat.config.ModConfig.showPlayerHeads = val);
        this.addDrawableChild(this.showPlayerHeadsButton);

        // SAVE BUTTON
        this.saveButton = ButtonWidget.builder(Text.literal("Save & Close"), button -> {
            saveSettings();
            this.client.setScreen(this.parent);
        }).dimensions(contentX + 20, panelY + panelHeight - 35, 240, 20).build();
        this.addDrawableChild(this.saveButton);
        
        this.colorPicker = new ColorPickerWidget(0, 0, hex -> {
            if ("selection".equals(activePickerField)) {
                this.selectionColorField.setText(hex);
            } else if ("timestamp".equals(activePickerField)) {
                this.timestampColorField.setText(hex);
            }
        });
        
        updateVisibility();
    }
    
    private net.minecraft.client.gui.widget.ClickableWidget createToggleButton(int x, int y, int w, boolean initial, java.util.function.Consumer<Boolean> onChange) {
        return new ToggleButtonWidget(x, y, w, 20, initial, onChange);
    }
    
    private void updateVisibility() {
        this.maxMessagesField.visible = (currentTab == 0);
        this.stackMessagesField.visible = (currentTab == 0);
        
        this.timestampFormatField.visible = (currentTab == 1);
        this.timestampColorField.visible = (currentTab == 1);
        this.timestampColorResetButton.visible = (currentTab == 1);
        this.selectionColorField.visible = (currentTab == 1);
        this.selectionColorResetButton.visible = (currentTab == 1);
        this.opacitySlider.visible = (currentTab == 1);
        
        this.showFontDropdownButton.visible = (currentTab == 2);
        this.showEmojiButtonButton.visible = (currentTab == 2);
        this.showPlayerHeadsButton.visible = (currentTab == 2);
    }
    
    private void saveSettings() {
        com.bame.secondchat.config.GlobalConfig config = com.bame.secondchat.config.GlobalConfig.getInstance();
        try {
            config.maxMessages = Integer.parseInt(this.maxMessagesField.getText());
        } catch (NumberFormatException ignored) {}
        try {
            config.stackMessages = Integer.parseInt(this.stackMessagesField.getText());
        } catch (NumberFormatException ignored) {}
        
        config.timestampFormat = this.timestampFormatField.getText();
        config.timestampColor = this.timestampColorField.getText();
        config.selectionColor = this.selectionColorField.getText();
        
        com.bame.secondchat.config.GlobalConfig.save();
        
        com.bame.secondchat.config.ModConfig.save();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        
        int panelWidth = 400;
        int panelHeight = 260;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;
        int sidebarW = 100;
        
        // Background and Border
        context.fill(panelX - 1, panelY - 1, panelX + panelWidth + 1, panelY + panelHeight + 1, 0x88333333);
        context.fill(panelX, panelY, panelX + sidebarW, panelY + panelHeight, 0x88000000); // Sidebar
        context.fill(panelX + sidebarW, panelY, panelX + panelWidth, panelY + panelHeight, 0x88111111); // Main content
        
        // Sidebar Title
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Settings").withColor(0xFFFFAA00), panelX + sidebarW / 2, panelY + 15, 0xFFFFFFFF);
        context.fill(panelX + 10, panelY + 30, panelX + sidebarW - 10, panelY + 31, 0x88333333); // Divider
        
        // Sidebar Tabs
        String[] tabs = {"Behavior", "Appearance", "Features"};
        for (int i = 0; i < tabs.length; i++) {
            int tabY = panelY + 40 + (i * 35);
            boolean hovered = mouseX >= panelX && mouseX <= panelX + sidebarW && mouseY >= tabY && mouseY < tabY + 35;
            boolean active = (currentTab == i);
            
            if (active) {
                context.fill(panelX, tabY, panelX + sidebarW, tabY + 35, 0x882A2A2A);
                context.fill(panelX, tabY, panelX + 3, tabY + 35, 0xFFFFAA00); // Orange indicator
            } else if (hovered) {
                context.fill(panelX, tabY, panelX + sidebarW, tabY + 35, 0x88222222);
            }
            
            int color = active ? 0xFFFFFFFF : 0xFFAAAAAA;
            context.drawTextWithShadow(this.textRenderer, Text.literal(tabs[i]), panelX + 15, tabY + 14, color);
        }
        
        // Content Area Labels
        int contentX = panelX + 110;
        int widgetX = contentX + 130;
        
        if (currentTab == 0) { // Behavior
            context.drawTextWithShadow(this.textRenderer, Text.literal("Max Messages:"), contentX, panelY + 36, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal("0 = unlimited"), contentX, panelY + 48, 0xFF888888);
            
            context.drawTextWithShadow(this.textRenderer, Text.literal("Stack Messages:"), contentX, panelY + 76, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal("0 = disabled"), contentX, panelY + 88, 0xFF888888);
        } else if (currentTab == 1) { // Appearance
            context.drawTextWithShadow(this.textRenderer, Text.literal("Timestamp Format:"), contentX, panelY + 36, 0xFFFFFFFF);
            
            context.drawTextWithShadow(this.textRenderer, Text.literal("Timestamp Color:"), contentX, panelY + 76, 0xFFFFFFFF);
            int tColor = parseColorForPreview(this.timestampColorField.getText());
            context.fill(widgetX - 1, panelY + 69, widgetX + 21, panelY + 91, 0xFFFFFFFF);
            context.fill(widgetX, panelY + 70, widgetX + 20, panelY + 90, tColor);
            
            context.drawTextWithShadow(this.textRenderer, Text.literal("Selection Color:"), contentX, panelY + 116, 0xFFFFFFFF);
            int sColor = parseColorForPreview(this.selectionColorField.getText());
            context.fill(widgetX - 1, panelY + 109, widgetX + 21, panelY + 131, 0xFFFFFFFF);
            context.fill(widgetX, panelY + 110, widgetX + 20, panelY + 130, sColor);
            
            context.drawTextWithShadow(this.textRenderer, Text.literal("Chat Background:"), contentX, panelY + 156, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal("Opacity"), contentX, panelY + 168, 0xFF888888);
        } else if (currentTab == 2) { // Features
            context.drawTextWithShadow(this.textRenderer, Text.literal("Show Font Dropdown:"), contentX, panelY + 36, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal("Show Emoji Button:"), contentX, panelY + 76, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal("Show Player Heads:"), contentX, panelY + 116, 0xFFFFFFFF);
        }
        
        super.render(context, mouseX, mouseY, delta);
        
        if (colorPicker != null) colorPicker.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(Click click, boolean inside) {
        double mouseX = click.x();
        double mouseY = click.y();
        
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.mouseClicked(click, inside)) {
                return true;
            } else {
                colorPicker.setVisible(false);
            }
        }
        
        int panelWidth = 400;
        int panelHeight = 260;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;
        int sidebarW = 100;
        
        // Sidebar Click Detection
        if (mouseX >= panelX && mouseX <= panelX + sidebarW) {
            for (int i = 0; i < 3; i++) {
                int tabY = panelY + 40 + (i * 35);
                if (mouseY >= tabY && mouseY < tabY + 35) {
                    currentTab = i;
                    updateVisibility();
                    return true;
                }
            }
        }
        
        // Color Box Click Detection
        if (currentTab == 1) {
            int contentX = panelX + 110;
            int widgetX = contentX + 130;
            
            if (this.timestampColorField.visible) {
                if (mouseX >= widgetX && mouseX <= widgetX + 20 && mouseY >= panelY + 70 && mouseY <= panelY + 90) {
                    activePickerField = "timestamp";
                    colorPicker.setPosition(widgetX - 130, panelY + 70);
                    colorPicker.setColor(this.timestampColorField.getText());
                    colorPicker.setVisible(true);
                    return true;
                }
            }
            if (this.selectionColorField.visible) {
                if (mouseX >= widgetX && mouseX <= widgetX + 20 && mouseY >= panelY + 110 && mouseY <= panelY + 130) {
                    activePickerField = "selection";
                    colorPicker.setPosition(widgetX - 130, panelY + 110);
                    colorPicker.setColor(this.selectionColorField.getText());
                    colorPicker.setVisible(true);
                    return true;
                }
            }
        }
        
        return super.mouseClicked(click, inside);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.mouseReleased(click)) return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.mouseDragged(click, deltaX, deltaY)) return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.charTyped(charInput)) return true;
        }
        return super.charTyped(charInput);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.keyPressed(keyInput)) return true;
        }
        return super.keyPressed(keyInput);
    }

    private int parseColorForPreview(String hex) {
        if (hex == null || hex.isEmpty()) return 0xFF000000;
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            if (hex.length() == 6) {
                return 0xFF000000 | Integer.parseInt(hex, 16);
            } else if (hex.length() == 8) {
                long val = Long.parseLong(hex, 16);
                return (int) val;
            }
        } catch (Exception ignored) {}
        return 0xFF000000;
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private static class ToggleButtonWidget extends net.minecraft.client.gui.widget.ClickableWidget {
        private boolean state;
        private final java.util.function.Consumer<Boolean> onChange;

        public ToggleButtonWidget(int x, int y, int width, int height, boolean initialState, java.util.function.Consumer<Boolean> onChange) {
            super(x, y, width, height, net.minecraft.text.Text.empty());
            this.state = initialState;
            this.onChange = onChange;
        }

        @Override
        protected void renderWidget(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
            int bg = this.isHovered() ? 0xAA444444 : 0xAA222222;
            
            // Draw background
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
            // Draw border
            context.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY(), 0xFF555555);
            context.fill(this.getX() - 1, this.getY() + this.height, this.getX() + this.width + 1, this.getY() + this.height + 1, 0xFF555555);
            context.fill(this.getX() - 1, this.getY(), this.getX(), this.getY() + this.height, 0xFF555555);
            context.fill(this.getX() + this.width, this.getY(), this.getX() + this.width + 1, this.getY() + this.height, 0xFF555555);
            
            // Draw indicator
            int color = state ? 0xFF00DD00 : 0xFFDD0000;
            int indWidth = this.width / 2;
            int indX = state ? (this.getX() + this.width - indWidth) : this.getX();
            context.fill(indX, this.getY(), indX + indWidth, this.getY() + this.height, color);
            
            // Draw text
            net.minecraft.text.Text text = net.minecraft.text.Text.literal(state ? "ON" : "OFF").withColor(0xFFFFFFFF);
            context.drawCenteredTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer, text, this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFFFFF);
        }

        public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean inside) {
            if (this.active && this.visible && inside && true) { // 1 = press usually
                this.state = !this.state;
                this.onChange.accept(this.state);
                return true;
            }
            return false;
        }
        
        @Override
        protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {}
    }
}
