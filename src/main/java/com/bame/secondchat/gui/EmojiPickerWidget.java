package com.bame.secondchat.gui;

import com.bame.secondchat.mixin.ChatScreenAccessor;
import com.bame.secondchat.util.EmojiManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class EmojiPickerWidget implements Drawable {
    private final int x, y, width, height;
    private final TextFieldWidget searchBox;
    private EmojiManager.EmojiCategory activeCategory = null;
    private List<String> currentSymbols;
    
    private int scrollOffset = 0;
    private final int columns = 10;
    private final int symbolSize = 12;
    private final int gridSpacing = 14; 
    private final int rows = 8;
    
    public EmojiPickerWidget(int screenWidth, int screenHeight) {
        this.width = columns * gridSpacing + 12; // 10 * 14 + 12 = 152
        this.height = rows * gridSpacing + 32 + 4; // 8 * 14 + 36 = 148
        this.x = screenWidth - this.width - 6; // 6 pixels margin right
        this.y = screenHeight - this.height - 24; // Above the chat input bar
        
        MinecraftClient client = MinecraftClient.getInstance();
        this.searchBox = new TextFieldWidget(client.textRenderer, this.x + 2, this.y + 16, this.width - 4, 12, Text.literal("Search..."));
        this.searchBox.setChangedListener(this::onSearch);
        
        List<EmojiManager.EmojiCategory> categories = EmojiManager.getCategories();
        if (!categories.isEmpty()) {
            this.activeCategory = categories.get(0);
            this.currentSymbols = this.activeCategory.symbols;
        } else {
            this.currentSymbols = List.of();
        }
    }

    private void onSearch(String text) {
        if (text.isEmpty()) {
            if (this.activeCategory != null) {
                this.currentSymbols = this.activeCategory.symbols;
            }
        } else {
            this.currentSymbols = EmojiManager.search(text);
        }
        this.scrollOffset = 0;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background exactly like symbol-chat
        context.fill(x, y, x + width, y + height, 0xA0000000);
        
        this.searchBox.render(context, mouseX, mouseY, delta);
        
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Draw Categories (Tabs) at the top        // Categories
        int catY = y + 2;
        int catX = x + 2;
        List<EmojiManager.EmojiCategory> cats = EmojiManager.getCategories();
        for (int i = 0; i < cats.size(); i++) {
            EmojiManager.EmojiCategory cat = cats.get(i);
            int tabX = catX + i * gridSpacing;
            
            if (this.activeCategory == cat && searchBox.getText().isEmpty()) {
                context.fill(tabX, catY, tabX + gridSpacing, catY + gridSpacing, 0x44FFFFFF); // subtle highlight for active tab
            } else if (mouseX >= tabX && mouseX < tabX + gridSpacing && mouseY >= catY && mouseY < catY + gridSpacing) {
                context.fill(tabX, catY, tabX + gridSpacing, catY + gridSpacing, 0x22FFFFFF); // hover highlight
            }
            
            String iconName = cat.icon;
            if (iconName.equals("misc")) {
                iconName = "symbols"; // fallback icon
            }
            Identifier iconId = Identifier.of("bamesecondchat", "tab_" + iconName);
            context.drawGuiTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, iconId, tabX, catY, 12, 12);
        }
        
        int gridX = x + 2;
        int gridY = y + 32;
        String hoveredSymbol = null;
        
        // Draw grid of symbols
        for (int i = 0; i < rows * columns; i++) {
            int symbolIndex = scrollOffset * columns + i;
            if (symbolIndex >= currentSymbols.size()) break;
            
            String symbol = currentSymbols.get(symbolIndex);
            
            int cellX = x + 2 + (i % columns) * gridSpacing;
            int cellY = gridY + (i / columns) * gridSpacing;
            
            if (mouseX >= cellX && mouseX < cellX + gridSpacing && mouseY >= cellY && mouseY < cellY + gridSpacing) {
                context.fill(cellX, cellY, cellX + gridSpacing, cellY + gridSpacing, 0x44FFFFFF);
                hoveredSymbol = symbol;
            }
            
            int textWidth = client.textRenderer.getWidth(symbol);
            int textX = cellX + (gridSpacing - textWidth) / 2;
            int textY = cellY + (gridSpacing - 8) / 2;
            context.drawText(client.textRenderer, symbol, textX, textY, 0xFFFFFFFF, false);
        }
        
        // Draw scrollbar
        int maxScroll = (currentSymbols.size() / columns) - rows + 1;
        if (maxScroll < 0) maxScroll = 0;
        
        if (maxScroll > 0) {
            int scrollbarX = x + width - 6;
            int scrollbarY = gridY;
            int scrollbarHeight = rows * gridSpacing;
            context.fill(scrollbarX, scrollbarY, scrollbarX + 4, scrollbarY + scrollbarHeight, 0x55FFFFFF);
            
            int thumbHeight = Math.max(8, scrollbarHeight / (maxScroll + 1));
            int thumbY = scrollbarY + (int)(((float)scrollOffset / maxScroll) * (scrollbarHeight - thumbHeight));
            context.fill(scrollbarX, thumbY, scrollbarX + 4, thumbY + thumbHeight, 0xAAFFFFFF);
        }
        
        if (hoveredSymbol != null) {
            String name = Character.getName(hoveredSymbol.codePointAt(0));
            if (name == null || name.isEmpty()) name = "Unknown Symbol";
            // Capitalize properly
            name = java.util.Arrays.stream(name.toLowerCase().split(" ")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).collect(java.util.stream.Collectors.joining(" "));
            
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, net.minecraft.text.Text.literal(name), mouseX, mouseY);
        }
    }

    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean bl) {
        if (this.searchBox.mouseClicked(click, bl)) {
            this.searchBox.setFocused(true);
            return true;
        } else {
            this.searchBox.setFocused(false);
        }
        
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            
            // Check categories
            int catY = y + 2;
            int catX = x + 2;
            List<EmojiManager.EmojiCategory> cats = EmojiManager.getCategories();
            for (int i = 0; i < cats.size(); i++) {
                int tabX = catX + i * gridSpacing;
                
                if (mouseX >= tabX && mouseX < tabX + gridSpacing && mouseY >= catY && mouseY < catY + gridSpacing) {
                    this.activeCategory = cats.get(i);
                    this.currentSymbols = this.activeCategory.symbols;
                    this.searchBox.setText("");
                    this.scrollOffset = 0;
                    return true;
                }
            }
            
            // Check symbols
            int gridX = x + 2;
            int gridY = y + 32;
            for (int i = 0; i < rows * columns; i++) {
                int symbolIndex = scrollOffset * columns + i;
                if (symbolIndex >= currentSymbols.size()) break;
                
                int cellX = gridX + (i % columns) * gridSpacing;
                int cellY = gridY + (i / columns) * gridSpacing;
                
                if (mouseX >= cellX && mouseX < cellX + gridSpacing && mouseY >= cellY && mouseY < cellY + gridSpacing) {
                    if (button == 0) { // Left click
                        MinecraftClient.getInstance().keyboard.setClipboard(currentSymbols.get(symbolIndex));
                        return true;
                    } else if (button == 1) { // Right click
                        String symbol = currentSymbols.get(symbolIndex);
                        java.util.List<String> favs = com.bame.secondchat.config.GlobalConfig.getInstance().favoriteEmojis;
                        if (favs.contains(symbol)) {
                            favs.remove(symbol);
                        } else {
                            favs.add(symbol);
                        }
                        com.bame.secondchat.config.GlobalConfig.getInstance().save();
                        
                        // If we are currently in the favorites tab, refresh the view
                        if (activeCategory != null && "favorites".equals(activeCategory.name)) {
                            this.currentSymbols = EmojiManager.getCategories().stream().filter(c -> c.name.equals("favorites")).findFirst().get().symbols;
                        }
                        return true;
                    }
                }
            }
            return true; // Clicked inside picker but not on anything interactive
        }
        
        return false;
    }

    private void insertSymbol(String symbol) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof ChatScreen chatScreen) {
            TextFieldWidget chatField = ((ChatScreenAccessor) chatScreen).getChatField();
            if (chatField != null) {
                chatField.write(symbol);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            int maxScroll = (currentSymbols.size() / columns) - rows + 1;
            if (maxScroll < 0) maxScroll = 0;
            
            scrollOffset -= (int) verticalAmount;
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
            return true;
        }
        return false;
    }

    public boolean charTyped(CharInput input) {
        if (this.searchBox.charTyped(input)) {
            return true;
        }
        return false;
    }

    public boolean keyPressed(KeyInput input) {
        if (this.searchBox.keyPressed(input)) {
            return true;
        }
        return false;
    }
}
