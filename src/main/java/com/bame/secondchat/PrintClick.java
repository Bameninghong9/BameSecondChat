package com.bame.secondchat;
import net.minecraft.text.ClickEvent;
import java.lang.reflect.Method;
public class PrintClick {
    public static void main(String[] args) {
        for (Method m : ClickEvent.class.getMethods()) {
            System.out.println(m.getName() + " -> " + m.getReturnType().getSimpleName());
        }
    }
}
