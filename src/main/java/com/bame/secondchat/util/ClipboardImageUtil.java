package com.bame.secondchat.util;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

public class ClipboardImageUtil {

    public static void copyMessagesToClipboard(List<com.bame.secondchat.data.SelectedLine> selectedLines, int chatWidth) {
        if (selectedLines == null || selectedLines.isEmpty()) {
            return;
        }

        int lineHeight = 20;
        int padding = 10;
        int width = chatWidth > 0 ? chatWidth : 800;
        int height = (selectedLines.size() * lineHeight) + (padding * 2);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Anti-aliasing for text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g2d.setColor(new Color(40, 40, 40, 200)); // Dark grey background like minecraft chat
        g2d.fillRect(0, 0, width, height);

        // Font
        Font font = new Font("Monospaced", Font.PLAIN, 14);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int currentY = padding + fm.getAscent();

        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();

        for (com.bame.secondchat.data.SelectedLine sl : selectedLines) {
            final int[] currentX = {padding};
            final int y = currentY;

            java.util.List<net.minecraft.text.OrderedText> wrappedLines = client.textRenderer.wrapLines(sl.getMessage().getMessage(), chatWidth - 8);
            if (sl.getLineIndex() >= 0 && sl.getLineIndex() < wrappedLines.size()) {
                net.minecraft.text.OrderedText orderedText = wrappedLines.get(sl.getLineIndex());
                
                orderedText.accept((index, style, codePoint) -> {
                    int rgb = 0xFFFFFF;
                    if (style != null && style.getColor() != null) {
                        rgb = style.getColor().getRgb();
                    }
                    
                    g2d.setColor(new Color(rgb));
                    String string = new String(Character.toChars(codePoint));
                    g2d.drawString(string, currentX[0], y);
                    currentX[0] += fm.stringWidth(string);
                    
                    return true;
                });
            }

            currentY += lineHeight;
        }

        g2d.dispose();

        // Copy to clipboard bypassing HeadlessException by using PowerShell
        try {
            java.io.File tempFile = java.io.File.createTempFile("chat_screenshot", ".png");
            javax.imageio.ImageIO.write(image, "png", tempFile);

            java.io.File tempScript = java.io.File.createTempFile("copy_image", ".ps1");
            String scriptContent = "Add-Type -AssemblyName System.Windows.Forms\n" +
                                   "[System.Windows.Forms.Clipboard]::SetImage([System.Drawing.Image]::FromFile('" + tempFile.getAbsolutePath() + "'))";
            java.nio.file.Files.writeString(tempScript.toPath(), scriptContent);

            ProcessBuilder pb = new ProcessBuilder(
                    "powershell", 
                    "-sta", 
                    "-ExecutionPolicy", "Bypass", 
                    "-WindowStyle", "Hidden", 
                    "-File", tempScript.getAbsolutePath()
            );
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
