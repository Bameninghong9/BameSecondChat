import java.lang.reflect.Method;
public class PrintClick3 {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("net.minecraft.class_2558");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println(m.getName() + " -> " + m.getReturnType().getName());
        }
    }
}
