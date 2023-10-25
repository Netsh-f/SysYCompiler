/*
@Time    : 2023/9/29 10:01
@Author  : Elaikona
*/
package Utils;

import Compiler.Lexer.Token;
import Utils.Error.ErrorData;
import Utils.Error.ErrorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputHelper {
    public static boolean outputSwitch = true;
    private static final String outputFileName = "output.txt";
    private static final Map<ErrorType, String> errorTypeCodeMap = initErrorTypeCodeMap();
    private static final List<ErrorData> errorDataList = new ArrayList<>();

    private OutputHelper() {
    }

    private static Map<ErrorType, String> initErrorTypeCodeMap() {
        var map = new HashMap<ErrorType, String>();
        map.put(ErrorType.FORMAT_STRING_ERROR, "a");
        map.put(ErrorType.IDENT_REDEFINED, "b");
        map.put(ErrorType.IDENT_UNDEFINED, "c");
        map.put(ErrorType.FUNC_PARAM_NUM_ERROR, "d");
        map.put(ErrorType.FUNC_PARAM_TYPE_ERROR, "e");
        map.put(ErrorType.VOID_RETURN, "f");
        map.put(ErrorType.NO_RETURN, "g");
        map.put(ErrorType.MODIFY_CONST, "h");
        map.put(ErrorType.MISSING_SEMICN, "i");
        map.put(ErrorType.MISSING_RPARENT, "j");
        map.put(ErrorType.MISSING_RBRACK, "k");
        map.put(ErrorType.PRINTF_EXP_NUM_ERROR, "l");
        map.put(ErrorType.BREAK_CONTINUE_ERROR, "m");
        return map;
    }

    public static void LexerOutput(List<Token> list) {
        StringBuilder output = new StringBuilder();
        list.forEach(token -> output.append(token.lexType()).append(" ").append(token.content()).append("\n"));
        FileHelper.writeToFile(outputFileName, output.toString());
    }

    public static void ParserOutput(StringBuilder stringBuilder) {
        FileHelper.writeToFile(outputFileName, stringBuilder.toString());
    }

    public static void addError(ErrorType type, int lineNum, String info) {
        if(outputSwitch){
            errorDataList.add(new ErrorData(type, lineNum, info));
        }
    }
}
