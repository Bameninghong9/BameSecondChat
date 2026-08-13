public class TestChar {
    public static void main(String[] args) {
        String s = "\uD83D\uDE01"; // 😁
        System.out.println(Character.getName(s.codePointAt(0)));
    }
}
