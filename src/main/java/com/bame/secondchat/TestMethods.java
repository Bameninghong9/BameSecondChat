package com.bame.secondchat;
import net.minecraft.client.gui.screen.ChatScreen;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
public class TestMethods {
    public static void test() {
        for (Method m : ChatScreen.class.getDeclaredMethods()) {
            System.out.print(m.getName() + "(");
            for(Parameter p : m.getParameters()) {
                System.out.print(p.getType().getSimpleName() + ", ");
            }
            System.out.println(") -> " + m.getReturnType().getSimpleName());
        }
    }
}
