/*
@Time    : 2023/9/29 0:17
@Author  : Elaikona
*/
package Utils;

public class CharHelper {
    private CharHelper() {
    }

    public static boolean isAlphaOrUnderscore(char c) {
        return Character.isLetter(c) || '_' == c;
    }

    public static boolean isAlnumOrUnderscore(char c) {
        return Character.isLetterOrDigit(c) || '_' == c;
    }

    public static boolean isNormalChar(char c) {
        return (c == 32) || (c == 33) || (c >= 40 && c <= 126);
    }

    public static boolean isNonZeroDigit(char c) {
        return c >= '1' && c <= '9';
    }


}
