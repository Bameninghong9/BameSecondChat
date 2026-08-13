package com.bame.secondchat;
import net.minecraft.client.gui.widget.ClickableWidget;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
public class TestMethods2 {
    public static void test() {
        for (Method m : ClickableWidget.class.getDeclaredMethods()) {
            if (m.getName().toLowerCase().contains("mouse") || m.getName().toLowerCase().contains("click")) {
                System.out.print(m.getName() + "(");
                for(Parameter p : m.getParameters()) {
                    System.out.print(p.getType().getSimpleName() + ", ");
                }
                System.out.println(") -> " + m.getReturnType().getSimpleName());
            }
        }
    }
}
