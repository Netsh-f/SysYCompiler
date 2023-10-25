/*
@Time    : 2023/10/10 11:31
@Author  : Elaikona
*/
package Utils.Error;

import java.util.Comparator;

public record ErrorData(ErrorType type, int lineNum, String info) {
    public static Comparator<ErrorData> lineNumComparator = Comparator.comparingInt(ErrorData::lineNum);
}
