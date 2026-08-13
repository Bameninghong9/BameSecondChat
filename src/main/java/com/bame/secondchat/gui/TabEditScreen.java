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
    private boolean isStartsWith;
    private ButtonWidget ruleTypeButton;

    public TabEditScreen(Screen parent, ChatTab tab, boolean isNew) {
        super(Text.literal(isNew ? "Create new Chat Tab" : "Edit Chat Tab"));
        this.parent = parent;
        this.tab = tab;
        this.isNew = isNew;
        this.hideFromVanilla = tab.isHideFromAll();
        
        // Determine rule type from existing rules
        this.isStartsWith = false;
        for (FilterRule rule : tab.getRules()) {
            if (rule instanceof com.bame.secondchat.data.StartsWithRule) {
                this.isStartsWith = true;
                break;
            }
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;

        // Name Field
        this.nameField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, 200, 20, Text.literal("Tab Name"));
        this.nameField.setMaxLength(30);
        this.nameField.setText(this.tab.getName());
        this.addDrawableChild(this.nameField);

        startY += 30;

        // Hide From Vanilla Button
        this.hideFromVanillaButton = ButtonWidget.builder(getHideFromVanillaText(), button -> {
            this.hideFromVanilla = !this.hideFromVanilla;
            button.setMessage(getHideFromVanillaText());
        }).dimensions(centerX - 100, startY, 200, 20).build();
        this.addDrawableChild(this.hideFromVanillaButton);

        startY += 35;
        
        // Rule Type Toggle Button
        this.ruleTypeButton = ButtonWidget.builder(getRuleTypeText(), button -> {
            this.isStartsWith = !this.isStartsWith;
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

        startY += 40;

        // Delete Button (only if not new)
        if (!isNew && !tab.getName().equals("All")) {
            ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("Delete Tab").withColor(0xFF5555), button -> {
                TabManager.getInstance().removeTab(this.tab);
                com.bame.secondchat.config.ModConfig.save();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }).dimensions(centerX - 100, startY, 200, 20).build();
            this.addDrawableChild(deleteButton);
            startY += 30;
        }
        
        // Clear Chat Button (only if not new)
        if (!isNew) {
            ButtonWidget clearChatButton = ButtonWidget.builder(Text.literal("Clear Chat").withColor(0xFFFFAA00), button -> {
                this.tab.clearMessages();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }).dimensions(centerX - 100, startY, 200, 20).build();
            this.addDrawableChild(clearChatButton);
            startY += 30;
        }

        // Save & Cancel Buttons
        ButtonWidget saveButton = ButtonWidget.builder(Text.literal("Save"), button -> {
            saveTab();
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }).dimensions(centerX - 100, startY, 95, 20).build();
        this.addDrawableChild(saveButton);

        ButtonWidget cancelButton = ButtonWidget.builder(Text.literal("Cancel"), button -> {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }).dimensions(centerX + 5, startY, 95, 20).build();
        this.addDrawableChild(cancelButton);
    }

    private Text getHideFromVanillaText() {
        return Text.literal("Hide from Vanilla: " + (this.hideFromVanilla ? "ON" : "OFF"));
    }
    
    private Text getRuleTypeText() {
        return Text.literal("Rule Type: " + (this.isStartsWith ? "Starts With" : "Contains"));
    }

    private void saveTab() {
        String newName = this.nameField.getText().trim();
        if (newName.isEmpty()) newName = "Unnamed";

        // Parse rules
        String[] rulesRaw = this.rulesField.getText().split(",");
        List<FilterRule> newRules = new ArrayList<>();
        for (String rRaw : rulesRaw) {
            String r = rRaw.trim();
            if (!r.isEmpty()) {
                if (this.isStartsWith) {
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
            // Updating existing
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
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 15, 0xFFFFFFFF);
        
        context.drawTextWithShadow(this.textRenderer, Text.literal("Tab Name:"), centerX - 100, 38, 0xFFA0A0A0);
        
        // Find Y of rules field
        int rulesY = this.rulesField.getY() - 12;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Rules (comma separated):"), centerX - 100, rulesY, 0xFFA0A0A0);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderDarkening(context);
        
        int centerX = this.width / 2;
        int topY = 10;
        
        // Measure where the lowest button is
        int bottomY = this.height - 10;
        if (this.children().size() > 0) {
            // Find lowest widget
            int maxBottom = topY;
            for (net.minecraft.client.gui.Element e : this.children()) {
                if (e instanceof net.minecraft.client.gui.widget.ClickableWidget cw) {
                    if (cw.getY() + cw.getHeight() > maxBottom) {
                        maxBottom = cw.getY() + cw.getHeight();
                    }
                }
            }
            bottomY = maxBottom + 20;
        }
        
        // Draw window background
        context.fill(centerX - 120, topY, centerX + 120, bottomY, 0xCC000000);
        
        // Draw border
        int borderColor = 0xFF555555;
        context.fill(centerX - 120, topY, centerX + 120, topY + 1, borderColor); // Top
        context.fill(centerX - 120, bottomY - 1, centerX + 120, bottomY, borderColor); // Bottom
        context.fill(centerX - 120, topY, centerX - 119, bottomY, borderColor); // Left
        context.fill(centerX + 119, topY, centerX + 120, bottomY, borderColor); // Right
    }
}
