package com.bame.secondchat.gui;

import com.bame.secondchat.util.FontTransformer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;

public class FontDropdownWidget extends ClickableWidget {
    private boolean isOpen = false;
    private static final int ITEM_HEIGHT = 16;
    
    public FontDropdownWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal(""));
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Draw main button background so it's visible against bright skies
        int mainBgColor = this.isHovered() ? 0x66000000 : 0x44000000;
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + ITEM_HEIGHT, mainBgColor);
        
        // Draw current text
        String currentText = FontTransformer.currentStyle.getDisplayName();
        String displayCurrentText = FontTransformer.transformWithStyle(currentText, FontTransformer.currentStyle);
        context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(displayCurrentText), this.getX() + this.width / 2, this.getY() + (ITEM_HEIGHT - 8) / 2, 0xFFFFFFFF);
        
        // Draw dropdown if open
        if (isOpen) {
            FontTransformer.FontStyle[] styles = FontTransformer.FontStyle.values();
            for (int i = 0; i < styles.length; i++) {
                int itemY = this.getY() + ITEM_HEIGHT + (i * ITEM_HEIGHT);
                boolean isItemHovered = mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                                        mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT;
                                        
                int itemBgColor = isItemHovered ? 0x88404050 : 0x88101015; // semi-transparent background for dropdown items
                context.fill(this.getX(), itemY, this.getX() + this.width, itemY + ITEM_HEIGHT, itemBgColor);
                
                String itemText = styles[i].getDisplayName();
                String displayItemText = FontTransformer.transformWithStyle(itemText, styles[i]);
                context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(displayItemText), this.getX() + this.width / 2, itemY + (ITEM_HEIGHT - 8) / 2, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean bl) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        
        System.out.println("[FontDropdownWidget] Click at " + mouseX + ", " + mouseY + " (button " + button + ")");
        
        if (!this.active || !this.visible) {
            System.out.println("[FontDropdownWidget] Not active or visible");
            return false;
        }
        
        if (button == 0) {
            if (mouseY < this.getY() + ITEM_HEIGHT && mouseX >= this.getX() && mouseX <= this.getX() + this.width) {
                // Clicked the main button
                this.isOpen = !this.isOpen;
                System.out.println("[FontDropdownWidget] Main button clicked. isOpen=" + this.isOpen);
                if (this.isOpen) {
                    this.setHeight(ITEM_HEIGHT + (FontTransformer.FontStyle.values().length * ITEM_HEIGHT));
                } else {
                    this.setHeight(ITEM_HEIGHT);
                }
                return true;
            } else if (this.isOpen) {
                // Clicked inside the dropdown
                FontTransformer.FontStyle[] styles = FontTransformer.FontStyle.values();
                for (int i = 0; i < styles.length; i++) {
                    int itemY = this.getY() + ITEM_HEIGHT + (i * ITEM_HEIGHT);
                    if (mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT && mouseX >= this.getX() && mouseX <= this.getX() + this.width) {
                        FontTransformer.currentStyle = styles[i];
                        this.isOpen = false;
                        this.setHeight(ITEM_HEIGHT);
                        System.out.println("[FontDropdownWidget] Selected style: " + styles[i].name());
                        return true;
                    }
                }
                // clicked outside while open
                System.out.println("[FontDropdownWidget] Clicked outside dropdown");
                this.isOpen = false;
                this.setHeight(ITEM_HEIGHT);
                return true;
            }
        }
        return false;
    }
    
    // Override the default appendNarrations method in 1.20+ 
    protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
    }
}
