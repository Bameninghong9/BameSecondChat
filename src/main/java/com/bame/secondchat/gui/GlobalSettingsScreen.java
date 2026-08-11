package com.bame.secondchat.gui;

import com.bame.secondchat.config.GlobalConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class GlobalSettingsScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget searchField;
    
    private TextFieldWidget maxMessagesField;
    private TextFieldWidget timestampFormatField;
    private TextFieldWidget timestampColorField;
    private TextFieldWidget selectionColorField;
    
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
        config.timestampFormat = this.timestampFormatField.getText();
        config.timestampColor = this.timestampColorField.getText();
        config.selectionColor = this.selectionColorField.getText();
        
        GlobalConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        
        int startY = 50;
        int fieldWidth = 200;
        int xOffset = this.width / 2 - fieldWidth / 2;
        
        String search = this.searchField.getText().toLowerCase();
        
        // Filter logic for drawing
        boolean showMaxMsg = "max messages".contains(search) || search.isEmpty();
        boolean showTsFormat = "timestamp format".contains(search) || search.isEmpty();
        boolean showTsColor = "timestamp color".contains(search) || search.isEmpty();
        boolean showSelColor = "selection color".contains(search) || search.isEmpty();
        
        this.maxMessagesField.visible = showMaxMsg;
        this.timestampFormatField.visible = showTsFormat;
        this.timestampColorField.visible = showTsColor;
        this.selectionColorField.visible = showSelColor;
        
        int currentY = startY;
        
        if (showMaxMsg) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Max Messages (0 = unendlich)"), xOffset, currentY - 10, 0xAAAAAA);
            this.maxMessagesField.setY(currentY);
            currentY += 40;
        }
        
        if (showTsFormat) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Timestamp Format"), xOffset, currentY - 10, 0xAAAAAA);
            this.timestampFormatField.setY(currentY);
            currentY += 40;
        }
        
        if (showTsColor) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Timestamp Color"), xOffset, currentY - 10, 0xAAAAAA);
            this.timestampColorField.setY(currentY);
            currentY += 40;
        }
        
        if (showSelColor) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Selection Color (Hex)"), xOffset, currentY - 10, 0xAAAAAA);
            this.selectionColorField.setY(currentY);
            currentY += 40;
        }
        
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
