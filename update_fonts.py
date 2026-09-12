import sys

with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/util/FontTransformer.java', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('OUTLINED("OUTLINED");', 'OUTLINED("OUTLINED"),\n        FULLWIDTH("FULLWIDTH"),\n        SQUARED("SQUARED"),\n        CURSIVE("CURSIVE");')

switch_case = '''            case OUTLINED:
                return toOutlined(text);
            case FULLWIDTH:
                return toFullwidth(text);
            case SQUARED:
                return toSquared(text);
            case CURSIVE:
                return toCursive(text);'''
text = text.replace('            case OUTLINED:\n                return toOutlined(text);', switch_case)

new_methods = '''
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
'''
if 'private static String toFullwidth' not in text:
    last_brace = text.rfind('}')
    text = text[:last_brace] + new_methods

with open('C:/Users/thorb/.gemini/antigravity/scratch/BameSecondChat/src/main/java/com/bame/secondchat/util/FontTransformer.java', 'w', encoding='utf-8') as f:
    f.write(text)
