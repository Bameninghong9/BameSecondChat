package com.bame.secondchat;
import net.minecraft.client.gui.screen.Screen;
import java.lang.reflect.Method;
public class PrintScreen {
    public static void main(String[] args) {
        for (Method m : Screen.class.getDeclaredMethods()) {
            System.out.print(m.getName() + "(");
            for (Class<?> p : m.getParameterTypes()) {
                System.out.print(p.getSimpleName() + ", ");
            }
            System.out.println(") -> " + m.getReturnType().getSimpleName());
        }
    }
}
