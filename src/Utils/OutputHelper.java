/*
@Time    : 2023/9/29 10:01
@Author  : Elaikona
*/
package Utils;

import Compiler.Lexer.Token;

import java.util.List;

public class OutputHelper {
    private static String outputFileName = "output.txt";
    private OutputHelper() {
    }

    public static void LexerOutput(List<Token> list) {
        StringBuilder output = new StringBuilder();
        list.forEach(token -> output.append(token.lexType()).append(" ").append(token.content()).append("\n"));
        FileHelper.writeToFile(outputFileName, output.toString());
    }

    public static void ParserOutput(StringBuilder stringBuilder){
        FileHelper.writeToFile(outputFileName, stringBuilder.toString());
    }
}
