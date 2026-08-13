package com.bame.secondchat;
import net.minecraft.client.gui.screen.ChatScreen;
import java.lang.reflect.Method;
public class TestRender {
    public static void test() {
        for (Method m : ChatScreen.class.getDeclaredMethods()) {
            if (m.getName().equals("render")) {
                System.out.println("ChatScreen has render method!");
            }
        }
    }
}
