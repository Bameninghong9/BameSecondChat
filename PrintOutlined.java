public class PrintOutlined {
    public static void main(String[] args) {
        for (int i = 0; i < 26; i++) {
            System.out.print(new String(Character.toChars(0x1CDD6 + i)));
        }
        System.out.println();
    }
}
