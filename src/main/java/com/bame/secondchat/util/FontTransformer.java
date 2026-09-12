package com.bame.secondchat.util;

public class FontTransformer {
    public enum FontStyle {
        NORMAL("Normal"),
        INVERSE("Inverse"),
        SMALL("SMALL"),
        CIRCLED("CIRCLED"),
        OUTLINED("OUTLINED"),
        FULLWIDTH("FULLWIDTH"),
        SQUARED("SQUARED"),
        CURSIVE("CURSIVE");

        private final String displayName;
        private String formattedName = null;

        FontStyle(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
        
        public String getFormattedName() {
            if (formattedName == null) {
                // Pre-compute and cache to avoid allocating new strings every frame during rendering
                FontStyle oldStyle = currentStyle;
                currentStyle = this;
                formattedName = FontTransformer.transform(displayName);
                currentStyle = oldStyle;
            }
            return formattedName;
        }

        public FontStyle next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    public static FontStyle currentStyle = FontStyle.NORMAL;

    public static String transform(String text) {
        if (text == null || text.isEmpty()) return text;
        
        // Handle commands - we shouldn't translate commands except maybe for their arguments, 
        // but it's safer to just ignore strings starting with '/'
        if (text.startsWith("/")) return text;

        switch (currentStyle) {
            case INVERSE:
                return toInverse(text);
            case SMALL:
                return toSmallCaps(text);
            case CIRCLED:
                return toCircled(text);
            case OUTLINED:
                return toOutlined(text);
            case FULLWIDTH:
                return toFullwidth(text);
            case SQUARED:
                return toSquared(text);
            case CURSIVE:
                return toCursive(text);
            case NORMAL:
            default:
                return text;
        }
    }

    public static String transformWithStyle(String text, FontStyle style) {
        FontStyle oldStyle = currentStyle;
        currentStyle = style;
        String result = transform(text);
        currentStyle = oldStyle;
        return result;
    }

    private static String toSmallCaps(String text) {
        String normal = "abcdefghijklmnopqrstuvwxyz";
        String small = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";
        
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            int index = normal.indexOf(c);
            if (index != -1) {
                result.append(small.charAt(index));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String toInverse(String text) {
        String normalLowercase = "abcdefghijklmnopqrstuvwxyz";
        String inverseLowercase = "ɐqɔpǝɟƃɥᴉɾʞlɯuodbɹsʇnʌʍxʎz";
        
        String normalUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String inverseUppercase = "∀ꓭƆᗡƎℲ⅁HIſꓘꞀWNOԀQᖤSꓕ∩ΛMX⅄Z";
        
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            int lowerIndex = normalLowercase.indexOf(c);
            int upperIndex = normalUppercase.indexOf(c);
            
            if (lowerIndex != -1) {
                result.append(inverseLowercase.charAt(lowerIndex));
            } else if (upperIndex != -1) {
                result.append(inverseUppercase.charAt(upperIndex));
            } else {
                result.append(c);
            }
        }
        
        return result.reverse().toString();
    }

    private static String toCircled(String text) {
        String normalLowercase = "abcdefghijklmnopqrstuvwxyz";
        String circledLowercase = "ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ";
        
        String normalUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String circledUppercase = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ";
        
        String normalDigits = "1234567890";
        String circledDigits = "①②③④⑤⑥⑦⑧⑨⓪";
        
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            int lowerIndex = normalLowercase.indexOf(c);
            int upperIndex = normalUppercase.indexOf(c);
            int digitIndex = normalDigits.indexOf(c);
            
            if (lowerIndex != -1) {
                result.append(circledLowercase.charAt(lowerIndex));
            } else if (upperIndex != -1) {
                result.append(circledUppercase.charAt(upperIndex));
            } else if (digitIndex != -1) {
                result.append(circledDigits.charAt(digitIndex));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String toOutlined(String text) {
        String normalLowercase = "abcdefghijklmnopqrstuvwxyz";
        String outlinedLowercase = "𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙𝕚𝕛𝕜𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫";
        
        String normalUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String outlinedUppercase = "𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ";
        
        String normalDigits = "1234567890";
        String outlinedDigits = "𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡𝟘";
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int lowerIndex = normalLowercase.indexOf(c);
            int upperIndex = normalUppercase.indexOf(c);
            int digitIndex = normalDigits.indexOf(c);
            
            if (lowerIndex != -1) {
                result.appendCodePoint(outlinedLowercase.codePointAt(outlinedLowercase.offsetByCodePoints(0, lowerIndex)));
            } else if (upperIndex != -1) {
                result.appendCodePoint(outlinedUppercase.codePointAt(outlinedUppercase.offsetByCodePoints(0, upperIndex)));
            } else if (digitIndex != -1) {
                result.appendCodePoint(outlinedDigits.codePointAt(outlinedDigits.offsetByCodePoints(0, digitIndex)));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String toFullwidth(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= '!' && c <= '~') {
                result.append((char) (c + 0xFEE0));
            } else if (c == ' ') {
                result.append((char) 0x3000);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String toSquared(String text) {
        String normalLowercase = "abcdefghijklmnopqrstuvwxyz";
        String squaredLowercase = "🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉";
        
        String normalUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String squaredUppercase = "🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉";
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int lowerIndex = normalLowercase.indexOf(c);
            int upperIndex = normalUppercase.indexOf(c);
            
            if (lowerIndex != -1) {
                result.appendCodePoint(squaredLowercase.codePointAt(squaredLowercase.offsetByCodePoints(0, lowerIndex)));
            } else if (upperIndex != -1) {
                result.appendCodePoint(squaredUppercase.codePointAt(squaredUppercase.offsetByCodePoints(0, upperIndex)));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String toCursive(String text) {
        String normalLowercase = "abcdefghijklmnopqrstuvwxyz";
        String cursiveLowercase = "𝓪𝓫𝓬𝓭𝓮𝓯𝓰𝓱𝓲𝓳𝓴𝓵𝓶𝓷𝓸𝓹𝓺𝓻𝓼𝓽𝓾𝓿𝔀𝔁𝔂𝔃";
        
        String normalUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String cursiveUppercase = "𝓐𝓑𝓒𝓓𝓔𝓕𝓖𝓗𝓘𝓙𝓚𝓛𝓜𝓝𝓞𝓟𝓠𝓡𝓢𝓣𝓤𝓥𝓦𝓧𝓨𝓩";
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int lowerIndex = normalLowercase.indexOf(c);
            int upperIndex = normalUppercase.indexOf(c);
            
            if (lowerIndex != -1) {
                result.appendCodePoint(cursiveLowercase.codePointAt(cursiveLowercase.offsetByCodePoints(0, lowerIndex)));
            } else if (upperIndex != -1) {
                result.appendCodePoint(cursiveUppercase.codePointAt(cursiveUppercase.offsetByCodePoints(0, upperIndex)));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
