package com.bame.secondchat;
import net.minecraft.client.gui.screen.ChatScreen;
import java.lang.reflect.Method;
public class TestRender2 {
    public static void test() {
        Method[] methods = ChatScreen.class.getMethods();
        for (Method m : methods) {
            if (m.getName().equals("render")) {
                System.out.println("ChatScreen has render method from: " + m.getDeclaringClass().getName());
            }
        }
    }
}
