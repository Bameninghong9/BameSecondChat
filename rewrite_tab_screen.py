import sys
import re

# UPDATE GLOBAL SETTINGS TRANSPARENCY
with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/gui/GlobalSettingsScreen.java', 'r', encoding='utf-8') as f:
    g_text = f.read()

g_text = g_text.replace('0x88333333', '0x55333333')
g_text = g_text.replace('0x88000000', '0x55000000')
g_text = g_text.replace('0x88111111', '0x55111111')
g_text = g_text.replace('0x882A2A2A', '0x552A2A2A')
g_text = g_text.replace('0x88222222', '0x55222222')
g_text = g_text.replace('0xAA444444', '0x77444444')
g_text = g_text.replace('0xAA222222', '0x77222222')
g_text = g_text.replace('0xDD000000', '0x77000000')

with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/gui/GlobalSettingsScreen.java', 'w', encoding='utf-8') as f:
    f.write(g_text)

# REWRITE TAB EDIT SCREEN
tab_code = '''package com.bame.secondchat.gui;

import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.FilterRule;
import com.bame.secondchat.data.TabManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class TabEditScreen extends Screen {
    private final Screen parent;
    private final ChatTab tab;
    private final boolean isNew;

    private TextFieldWidget nameField;
    private TextFieldWidget rulesField;
    private CustomButton hideFromVanillaButton;
    private CustomButton ruleTypeButton;

    private boolean hideFromVanilla;
    private int ruleTypeIndex; // 0 = Contains, 1 = Starts With, 2 = Captures Block

    private final int panelWidth = 420;
    private final int panelHeight = 280;

    public TabEditScreen(Screen parent, ChatTab tab, boolean isNew) {
        super(Text.literal(isNew ? "Create Tab" : "Edit Tab"));
        this.parent = parent;
        this.tab = tab;
        this.isNew = isNew;
        this.hideFromVanilla = tab.isHideFromAll();
        
        this.ruleTypeIndex = 0;
        for (FilterRule rule : tab.getRules()) {
            if (rule instanceof com.bame.secondchat.data.CapturesBlockRule) {
                this.ruleTypeIndex = 2;
                break;
            } else if (rule instanceof com.bame.secondchat.data.StartsWithRule) {
                this.ruleTypeIndex = 1;
                break;
            }
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int startY = centerY - panelHeight / 2 + 50;
        int leftCol = centerX - 180;
        int rightCol = centerX + 10;

        // Name Field (Left Col)
        this.nameField = new TextFieldWidget(this.textRenderer, leftCol, startY, 170, 20, Text.literal("Tab Name"));
        this.nameField.setMaxLength(30);
        this.nameField.setText(this.tab.getName());
        this.addDrawableChild(this.nameField);

        // Rules Field (Right Col)
        this.rulesField = new TextFieldWidget(this.textRenderer, rightCol, startY, 170, 20, Text.literal("Rules"));
        this.rulesField.setMaxLength(256);
        
        StringBuilder rulesText = new StringBuilder();
        for (FilterRule rule : tab.getRules()) {
            if (rulesText.length() > 0) rulesText.append(", ");
            rulesText.append(rule.getValue());
        }
        this.rulesField.setText(rulesText.toString());
        this.addDrawableChild(this.rulesField);

        startY += 50;

        // Hide From Vanilla Button
        this.hideFromVanillaButton = new CustomButton(leftCol, startY, 170, 24, getHideFromVanillaText(), 0xFF333333, () -> {
            this.hideFromVanilla = !this.hideFromVanilla;
            this.hideFromVanillaButton.setMessage(getHideFromVanillaText());
        });
        this.addDrawableChild(this.hideFromVanillaButton);
        
        // Rule Type Toggle Button
        this.ruleTypeButton = new CustomButton(rightCol, startY, 170, 24, getRuleTypeText(), 0xFF333333, () -> {
            this.ruleTypeIndex = (this.ruleTypeIndex + 1) % 3;
            this.ruleTypeButton.setMessage(getRuleTypeText());
        });
        this.addDrawableChild(this.ruleTypeButton);
        
        startY += 50;

        // Delete Button & Clear Chat Button
        if (!isNew && !tab.getName().equals("All")) {
            CustomButton deleteButton = new CustomButton(leftCol, startY, 170, 24, Text.literal("✖ Delete Tab").withColor(0xFFFF4444), 0xFFFF4444, () -> {
                TabManager.getInstance().removeTab(this.tab);
                com.bame.secondchat.config.ModConfig.save();
                if (this.client != null) this.client.setScreen(this.parent);
            });
            this.addDrawableChild(deleteButton);
        }
        
        if (!isNew) {
            CustomButton clearChatButton = new CustomButton(rightCol, startY, 170, 24, Text.literal("🗑 Clear Chat").withColor(0xFFFFAA00), 0xFFFFAA00, () -> {
                this.tab.clearMessages();
                if (this.client != null) this.client.setScreen(this.parent);
            });
            this.addDrawableChild(clearChatButton);
        }
        
        // Save & Cancel Buttons
        int bottomY = centerY + panelHeight / 2 - 40;
        CustomButton saveButton = new CustomButton(centerX - 120, bottomY, 110, 24, Text.literal("✔ Save").withColor(0xFF55FF55), 0xFF55FF55, () -> {
            saveTab();
            if (this.client != null) this.client.setScreen(this.parent);
        });
        this.addDrawableChild(saveButton);

        CustomButton cancelButton = new CustomButton(centerX + 10, bottomY, 110, 24, Text.literal("Cancel").withColor(0xFFBBBBBB), 0xFFBBBBBB, () -> {
            if (this.client != null) this.client.setScreen(this.parent);
        });
        this.addDrawableChild(cancelButton);
    }

    private Text getHideFromVanillaText() {
        return Text.literal("Hide from Vanilla: ").append(Text.literal(this.hideFromVanilla ? "ON" : "OFF").withColor(this.hideFromVanilla ? 0xFF55FF55 : 0xFFFF5555));
    }
    
    private Text getRuleTypeText() {
        String typeStr = "Contains";
        int color = 0xFF55FFFF;
        if (this.ruleTypeIndex == 1) { typeStr = "Starts With"; color = 0xFFFFFF55; }
        else if (this.ruleTypeIndex == 2) { typeStr = "Captures Block"; color = 0xFFFF55FF; }
        return Text.literal("Rule: ").append(Text.literal(typeStr).withColor(color));
    }

    private void saveTab() {
        String newName = this.nameField.getText().trim();
        if (newName.isEmpty()) newName = "Unnamed";

        String[] rulesRaw = this.rulesField.getText().split(",");
        List<FilterRule> newRules = new ArrayList<>();
        for (String rRaw : rulesRaw) {
            String r = rRaw.trim();
            if (!r.isEmpty()) {
                if (this.ruleTypeIndex == 2) {
                    newRules.add(new com.bame.secondchat.data.CapturesBlockRule(r));
                } else if (this.ruleTypeIndex == 1) {
                    newRules.add(new com.bame.secondchat.data.StartsWithRule(r));
                } else {
                    newRules.add(new com.bame.secondchat.data.ContainsRule(r));
                }
            }
        }

        if (isNew) {
            ChatTab newTab = new ChatTab(newName, this.hideFromVanilla);
            newTab.getRules().addAll(newRules);
            TabManager.getInstance().addTab(newTab);
        } else {
            if (this.tab != TabManager.getInstance().getAllTab()) {
                TabManager.getInstance().updateTab(this.tab, newName, this.hideFromVanilla, newRules);
            }
        }
        com.bame.secondchat.config.ModConfig.save();
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean inside) {
        for (net.minecraft.client.gui.Element element : this.children()) {
            if (element.mouseClicked(click, inside)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int leftCol = centerX - 180;
        int rightCol = centerX + 10;
        
        // Draw Titles
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, centerY - panelHeight / 2 + 15, 0xFFFFAA00);
        
        // Labels
        context.drawTextWithShadow(this.textRenderer, Text.literal("Tab Name").withColor(0xFFDDDDDD), leftCol, this.nameField.getY() - 12, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Rules (comma separated)").withColor(0xFFDDDDDD), rightCol, this.rulesField.getY() - 12, 0xFFFFFFFF);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Modern glass-like background
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;
        int right = centerX + panelWidth / 2;
        int bottom = centerY + panelHeight / 2;
        
        // Dark screen overlay
        context.fill(0, 0, this.width, this.height, 0x66000000);
        
        // Glass panel
        context.fill(left, top, right, bottom, 0x88111111);
        
        // Glowing cyan/blue border line on top and bottom
        context.fill(left, top, right, top + 2, 0xFF00FFFF);
        context.fill(left, bottom - 2, right, bottom, 0xFF00FFFF);
        // Subtle side borders
        context.fill(left, top, left + 1, bottom, 0xAA00AAAA);
        context.fill(right - 1, top, right, bottom, 0xAA00AAAA);
    }

    private static class CustomButton extends net.minecraft.client.gui.widget.ClickableWidget {
        private final Runnable onClick;
        private final int themeColor;

        public CustomButton(int x, int y, int width, int height, Text message, int themeColor, Runnable onClick) {
            super(x, y, width, height, message);
            this.onClick = onClick;
            this.themeColor = themeColor;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean hovered = this.isHovered();
            
            int bg = hovered ? (this.themeColor & 0x55FFFFFF) : 0x44000000;
            int border = hovered ? this.themeColor : 0xAA555555;
            
            // Draw background
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
            // Draw borders
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, border);
            context.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, border);
            context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, border);
            context.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, border);
            
            // Draw Text
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean inside) {
            double mx = click.x();
            double my = click.y();
            if (this.active && this.visible && mx >= this.getX() && mx <= this.getX() + this.width && my >= this.getY() && my <= this.getY() + this.height) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onClick.run();
                return true;
            }
            return false;
        }
        
        @Override
        protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {}
    }
}
'''

with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/gui/TabEditScreen.java', 'w', encoding='utf-8') as f:
    f.write(tab_code)
