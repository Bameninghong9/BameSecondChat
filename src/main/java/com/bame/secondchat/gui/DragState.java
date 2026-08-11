package com.bame.secondchat.gui;

public class DragState {
    public static boolean isDraggingTab = false;
    public static boolean isResizing = false;
    public static boolean isDraggingScrollbar = false;
    
    public static double startMouseX = 0;
    public static double startMouseY = 0;
    
    public static int startTabX = 0;
    public static int startTabY = 0;
    
    public static int startTabWidth = 0;
    public static int startTabHeight = 0;
    
    public static int startScrollbarMouseY = 0;
    public static double startScrollOffset = 0;
    
    public static com.bame.secondchat.data.ChatTab draggedTab = null;
}
