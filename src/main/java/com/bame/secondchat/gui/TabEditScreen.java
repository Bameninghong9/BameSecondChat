package com.bame.secondchat.gui;

import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.FilterRule;
import com.bame.secondchat.data.TabManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TabEditScreen extends Screen {
    private final Screen parent;
    private final ChatTab tab;
    private final boolean isNew;

    private TextFieldWidget nameField;
    private ButtonWidget hideFromVanillaButton;
    private TextFieldWidget rulesField;
    
    private boolean hideFromVanilla;
    private int ruleTypeIndex = 0; // 0=Contains, 1=StartsWith, 2=CapturesBlock
    private ButtonWidget ruleTypeButton;
    
    private int panelWidth = 240;
    private int panelHeight = 270;

    public TabEditScreen(Screen parent, ChatTab tab, boolean isNew) {
        super(Text.literal(isNew ? "Create new Chat Tab" : "Edit Chat Tab"));
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
        
        int startY = centerY - panelHeight / 2 + 35;

        // Name Field
        this.nameField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, 200, 20, Text.literal("Tab Name"));
        this.nameField.setMaxLength(30);
        this.nameField.setText(this.tab.getName());
        this.addDrawableChild(this.nameField);

        startY += 35;

        // Hide From Vanilla Button
        this.hideFromVanillaButton = ButtonWidget.builder(getHideFromVanillaText(), button -> {
            this.hideFromVanilla = !this.hideFromVanilla;
            button.setMessage(getHideFromVanillaText());
        }).dimensions(centerX - 100, startY, 200, 20).build();
        this.addDrawableChild(this.hideFromVanillaButton);

        startY += 40;
        
        // Rule Type Toggle Button
        this.ruleTypeButton = ButtonWidget.builder(getRuleTypeText(), button -> {
            this.ruleTypeIndex = (this.ruleTypeIndex + 1) % 3;
            button.setMessage(getRuleTypeText());
        }).dimensions(centerX - 100, startY, 200, 20).build();
        this.addDrawableChild(this.ruleTypeButton);
        
        startY += 35;

        // Rules Field (Comma separated)
        this.rulesField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, 200, 20, Text.literal("Rules"));
        this.rulesField.setMaxLength(256);
        
        StringBuilder rulesText = new StringBuilder();
        for (FilterRule rule : tab.getRules()) {
            if (rulesText.length() > 0) rulesText.append(", ");
            rulesText.append(rule.getValue());
        }
        this.rulesField.setText(rulesText.toString());
        this.addDrawableChild(this.rulesField);

        startY += 35;

        // Delete Button & Clear Chat Button side-by-side
        if (!isNew && !tab.getName().equals("All")) {
            ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("Delete Tab").withColor(0xFFFF5555), button -> {
                TabManager.getInstance().removeTab(this.tab);
                com.bame.secondchat.config.ModConfig.save();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }).dimensions(centerX - 100, startY, 95, 20).build();
            this.addDrawableChild(deleteButton);
        }
        
        if (!isNew) {
            ButtonWidget clearChatButton = ButtonWidget.builder(Text.literal("Clear Chat").withColor(0xFFFFAA00), button -> {
                this.tab.clearMessages();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }).dimensions(centerX + 5, startY, 95, 20).build();
            this.addDrawableChild(clearChatButton);
        }
        
        // Save & Cancel Buttons
        int bottomY = centerY + panelHeight / 2 - 30;
        ButtonWidget saveButton = ButtonWidget.builder(Text.literal("Save").withColor(0xFF55FF55), button -> {
            saveTab();
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }).dimensions(centerX - 100, bottomY, 95, 20).build();
        this.addDrawableChild(saveButton);

        ButtonWidget cancelButton = ButtonWidget.builder(Text.literal("Cancel").withColor(0xFFBBBBBB), button -> {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }).dimensions(centerX + 5, bottomY, 95, 20).build();
        this.addDrawableChild(cancelButton);
    }

    private Text getHideFromVanillaText() {
        if (this.hideFromVanilla) {
            return Text.literal("Hide from Vanilla: ").append(Text.literal("ON").withColor(0xFF55FF55));
        } else {
            return Text.literal("Hide from Vanilla: ").append(Text.literal("OFF").withColor(0xFFFF5555));
        }
    }
    
    private Text getRuleTypeText() {
        String typeStr = "Contains";
        int color = 0xFF55FFFF;
        if (this.ruleTypeIndex == 1) { typeStr = "Starts With"; color = 0xFFFFFF55; }
        else if (this.ruleTypeIndex == 2) { typeStr = "Captures Block"; color = 0xFFFF55FF; }
        return Text.literal("Rule Type: ").append(Text.literal(typeStr).withColor(color));
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, centerY - panelHeight / 2 + 10, 0xFFFFAA00);
        
        context.drawTextWithShadow(this.textRenderer, Text.literal("Tab Name:"), centerX - 100, this.nameField.getY() - 11, 0xFFDDDDDD);
        
        int rulesY = this.rulesField.getY() - 11;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Rules (comma separated):"), centerX - 100, rulesY, 0xFFDDDDDD);
        
        // Horizontal divider above save/cancel
        int bottomY = centerY + panelHeight / 2 - 40;
        context.fill(centerX - 110, bottomY, centerX + 110, bottomY + 1, 0x55FFFFFF);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderDarkening(context);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;
        int right = centerX + panelWidth / 2;
        int bottom = centerY + panelHeight / 2;
        
        // Draw a nice translucent black panel with a subtle border
        context.fill(left, top, right, bottom, 0xDD000000);
        context.fill(left - 1, top - 1, right + 1, top, 0x55FFFFFF); // Top border
        context.fill(left - 1, bottom, right + 1, bottom + 1, 0x55FFFFFF); // Bottom border
        context.fill(left - 1, top, left, bottom, 0x55FFFFFF); // Left border
        context.fill(right, top, right + 1, bottom, 0x55FFFFFF); // Right border
    }
}
