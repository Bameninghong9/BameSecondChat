with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/gui/GlobalSettingsScreen.java', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Add fields
text = text.replace('private ButtonWidget saveButton;', 'private ButtonWidget saveButton;\n    private ColorPickerWidget colorPicker;\n    private String activePickerField = null;')

# 2. Add init
text = text.replace('this.addDrawableChild(this.saveButton);', '''this.addDrawableChild(this.saveButton);
        
        this.colorPicker = new ColorPickerWidget(0, 0, hex -> {
            if ("selection".equals(activePickerField)) {
                this.selectionColorField.setText(hex);
            } else if ("timestamp".equals(activePickerField)) {
                this.timestampColorField.setText(hex);
            }
        });''')

# 3. Handle click for popup
click_method = '''
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.mouseClicked(mouseX, mouseY, button)) {
                return true;
            } else {
                colorPicker.setVisible(false); // Close if clicked outside
            }
        }
        
        int fieldWidth = 200;
        int xOffset = this.width / 2 - fieldWidth / 2;
        
        if (this.timestampColorField.visible) {
            int y = this.timestampColorField.getY();
            if (mouseX >= xOffset - 26 && mouseX <= xOffset - 4 && mouseY >= y - 1 && mouseY <= y + 21) {
                activePickerField = "timestamp";
                colorPicker.setPosition(xOffset - 150, y);
                colorPicker.setColor(this.timestampColorField.getText());
                colorPicker.setVisible(true);
                return true;
            }
        }
        
        if (this.selectionColorField.visible) {
            int y = this.selectionColorField.getY();
            if (mouseX >= xOffset - 26 && mouseX <= xOffset - 4 && mouseY >= y - 1 && mouseY <= y + 21) {
                activePickerField = "selection";
                colorPicker.setPosition(xOffset - 150, y);
                colorPicker.setColor(this.selectionColorField.getText());
                colorPicker.setVisible(true);
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (colorPicker != null && colorPicker.isVisible()) {
            if (colorPicker.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
'''

text = text.replace('private void saveSettings() {', click_method + '\n    private void saveSettings() {')

# 4. Render picker (needs to render AFTER everything else so it's on top)
text = text.replace('        this.saveButton.setY(currentY + 20);', '        this.saveButton.setY(currentY + 20);\n        if (colorPicker != null) colorPicker.render(context, mouseX, mouseY, delta);')

with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/gui/GlobalSettingsScreen.java', 'w', encoding='utf-8') as f:
    f.write(text)
