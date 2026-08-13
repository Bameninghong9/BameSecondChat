package com.bame.secondchat;
import net.minecraft.client.gui.screen.Screen;
import java.lang.reflect.Method;
public class PrintScreen2 {
    public static void main(String[] args) {
        for (Method m : Screen.class.getMethods()) {
            if (m.getParameterCount() > 0 && m.getParameterTypes()[0].getName().contains("Style")) {
                System.out.println(m.getName() + " takes Style");
            }
        }
    }
}
