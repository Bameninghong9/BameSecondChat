package com.bame.secondchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import java.awt.Color;
import java.util.function.Consumer;

public class ColorPickerWidget {
    private int x, y;
    private int width = 120;
    private int height = 130;
    private boolean visible = false;
    
    private float hue = 0.0f;
    private float saturation = 1.0f;
    private float brightness = 1.0f;
    
    private TextFieldWidget hexField;
    private Consumer<String> onColorChanged;
    
    private boolean draggingSV = false;
    private boolean draggingHue = false;
    private boolean internalChange = false;

    public ColorPickerWidget(int x, int y, Consumer<String> onColorChanged) {
        this.x = x;
        this.y = y;
        this.onColorChanged = onColorChanged;
        this.hexField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 10, y + 105, 100, 16, Text.literal("Hex"));
        this.hexField.setMaxLength(9);
        this.hexField.setChangedListener(text -> {
            if (internalChange) return;
            if (!text.startsWith("#")) {
                text = "#" + text;
            }
            if (text.length() == 7 || text.length() == 9) {
                try {
                    int c = (int) Long.parseLong(text.substring(1), 16);
                    if (text.length() == 7) c |= 0xFF000000;
                    updateFromRGB(c);
                    onColorChanged.accept(text);
                } catch (NumberFormatException ignored) {}
            }
        });
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.hexField.setX(x + 10);
        this.hexField.setY(y + 105);
    }
    
    public void setColor(String hex) {
        this.hexField.setText(hex);
        try {
            if (!hex.startsWith("#")) hex = "#" + hex;
            int c = (int) Long.parseLong(hex.substring(1), 16);
            if (hex.length() == 7) c |= 0xFF000000;
            updateFromRGB(c);
        } catch (Exception ignored) {}
    }
    
    private void updateFromRGB(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }
    
    private void updateHexField() {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        String hex = String.format("#%06X", rgb & 0xFFFFFF);
        
        internalChange = true;
        if (!hexField.isFocused()) {
            hexField.setText(hex);
        }
        onColorChanged.accept(hex);
        internalChange = false;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        
        // Background
        context.fill(x, y, x + width, y + height, 0xFF222222);
        context.fill(x - 1, y - 1, x + width + 1, y, 0xFF555555); context.fill(x - 1, y + height, x + width + 1, y + height + 1, 0xFF555555); context.fill(x - 1, y, x, y + height, 0xFF555555); context.fill(x + width, y, x + width + 1, y + height, 0xFF555555);
        
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, "COLOR PICKER", x + 10, y + 5, 0xFFFFFFFF);
        
        // SV Picker (Saturation/Value)
        int svX = x + 10;
        int svY = y + 20;
        int svSize = 70;
        
        // Draw SV gradient
        for (int i = 0; i < svSize; i++) {
            for (int j = 0; j < svSize; j++) {
                float s = (float) i / svSize;
                float b = 1.0f - ((float) j / svSize);
                int color = Color.HSBtoRGB(hue, s, b);
                context.fill(svX + i, svY + j, svX + i + 1, svY + j + 1, color | 0xFF000000);
            }
        }
        
        // Draw SV cursor
        int cursorX = svX + (int)(saturation * svSize);
        int cursorY = svY + (int)((1.0f - brightness) * svSize);
        context.fill(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, 0xFFFFFFFF);
        context.fill(cursorX - 1, cursorY - 1, cursorX + 1, cursorY + 1, 0xFF000000);
        
        // Hue slider
        int hueX = x + 85;
        int hueY = y + 20;
        int hueWidth = 15;
        int hueHeight = 70;
        
        for (int j = 0; j < hueHeight; j++) {
            float h = (float) j / hueHeight;
            int color = Color.HSBtoRGB(h, 1.0f, 1.0f);
            context.fill(hueX, hueY + j, hueX + hueWidth, hueY + j + 1, color | 0xFF000000);
        }
        
        // Draw Hue cursor
        int hCursorY = hueY + (int)(hue * hueHeight);
        context.fill(hueX - 1, hCursorY - 1, hueX + hueWidth + 1, hCursorY + 1, 0xFFFFFFFF);
        context.fill(hueX, hCursorY, hueX + hueWidth, hCursorY, 0xFF000000);
        
        // Preview box
        int color = Color.HSBtoRGB(hue, saturation, brightness) | 0xFF000000;
        context.fill(x + 10, y + 95, x + 100, y + 100, color);
        
        hexField.render(context, mouseX, mouseY, delta);
    }
    
    public boolean mouseClicked(Click click, boolean inside) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (!visible) return false;
        
        if (hexField.mouseClicked(click, inside)) return true;
        
        int svX = x + 10;
        int svY = y + 20;
        int svSize = 70;
        
        if (mouseX >= svX && mouseX <= svX + svSize && mouseY >= svY && mouseY <= svY + svSize) {
            draggingSV = true;
            updateSVFromMouse(mouseX, mouseY);
            return true;
        }
        
        int hueX = x + 85;
        int hueY = y + 20;
        int hueWidth = 15;
        int hueHeight = 70;
        
        if (mouseX >= hueX && mouseX <= hueX + hueWidth && mouseY >= hueY && mouseY <= hueY + hueHeight) {
            draggingHue = true;
            updateHueFromMouse(mouseY);
            return true;
        }
        
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    
    public boolean mouseReleased(Click click) {
        draggingSV = false;
        draggingHue = false;
        return false;
    }
    
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (!visible) return false;
        
        if (draggingSV) {
            updateSVFromMouse(mouseX, mouseY);
            return true;
        }
        
        if (draggingHue) {
            updateHueFromMouse(mouseY);
            return true;
        }
        
        return false;
    }
    
    public boolean keyPressed(KeyInput keyInput) {
        if (!visible) return false;
        return hexField.keyPressed(keyInput);
    }
    
    public boolean charTyped(CharInput charInput) {
        if (!visible) return false;
        return hexField.charTyped(charInput);
    }
    
    private void updateSVFromMouse(double mouseX, double mouseY) {
        int svX = x + 10;
        int svY = y + 20;
        int svSize = 70;
        
        float s = (float) (mouseX - svX) / svSize;
        float b = 1.0f - (float) (mouseY - svY) / svSize;
        
        this.saturation = Math.max(0, Math.min(1, s));
        this.brightness = Math.max(0, Math.min(1, b));
        
        updateHexField();
    }
    
    private void updateHueFromMouse(double mouseY) {
        int hueY = y + 20;
        int hueHeight = 70;
        
        float h = (float) (mouseY - hueY) / hueHeight;
        this.hue = Math.max(0, Math.min(1, h));
        
        updateHexField();
    }
}
