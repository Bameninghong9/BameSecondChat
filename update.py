import sys

with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/gui/GlobalSettingsScreen.java', 'r', encoding='utf-8') as f:
    text = f.read()

# Transparencies
text = text.replace('0xCC333333', '0x88333333')
text = text.replace('0xCC111111', '0x88000000')
text = text.replace('0xCC1E1E1E', '0x88111111')
text = text.replace('0xCC2A2A2A', '0x882A2A2A')
text = text.replace('0xCC222222', '0x88222222')

# Replace createToggleButton implementation
old_create = '''    private ButtonWidget createToggleButton(int x, int y, int w, boolean initial, java.util.function.Consumer<Boolean> onChange) {
        Text t = Text.literal(initial ? "ON" : "OFF").withColor(initial ? 0x55FF55 : 0xFF5555);
        return ButtonWidget.builder(t, btn -> {
            boolean current = btn.getMessage().getString().equals("ON");
            boolean next = !current;
            onChange.accept(next);
            btn.setMessage(Text.literal(next ? "ON" : "OFF").withColor(next ? 0x55FF55 : 0xFF5555));
        }).dimensions(x, y, w, 20).build();
    }'''

new_create = '''    private net.minecraft.client.gui.widget.ClickableWidget createToggleButton(int x, int y, int w, boolean initial, java.util.function.Consumer<Boolean> onChange) {
        return new ToggleButtonWidget(x, y, w, 20, initial, onChange);
    }'''

if old_create in text:
    text = text.replace(old_create, new_create)
else:
    print("Could not find old_create")

text = text.replace('private ButtonWidget showFontDropdownButton;', 'private net.minecraft.client.gui.widget.ClickableWidget showFontDropdownButton;')
text = text.replace('private ButtonWidget showEmojiButtonButton;', 'private net.minecraft.client.gui.widget.ClickableWidget showEmojiButtonButton;')
text = text.replace('private ButtonWidget showPlayerHeadsButton;', 'private net.minecraft.client.gui.widget.ClickableWidget showPlayerHeadsButton;')

new_class = '''
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

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.state = !this.state;
            this.onChange.accept(this.state);
        }
        
        @Override
        protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {}
    }
}
'''
if 'class ToggleButtonWidget' not in text:
    last_brace = text.rfind('}')
    text = text[:last_brace] + new_class

with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/gui/GlobalSettingsScreen.java', 'w', encoding='utf-8') as f:
    f.write(text)
