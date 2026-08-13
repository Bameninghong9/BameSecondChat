package com.bame.secondchat;
import net.minecraft.client.gui.screen.ChatScreen;
import java.lang.reflect.Method;
public class TestMouse {
    public static void test() {
        for (Method m : ChatScreen.class.getDeclaredMethods()) {
            if (m.getName().toLowerCase().contains("mouse") || m.getName().toLowerCase().contains("click")) {
                System.out.println("ChatScreen method: " + m.getName());
            }
        }
    }
}
