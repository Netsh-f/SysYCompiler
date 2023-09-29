import Compiler.Lexer.Lexer;
import Utils.FileHelper;
import Utils.OutputHelper;


public class Compiler {
    public static void main(String[] args) {
        String source = FileHelper.fileToString("testfile.txt");

        var list = new Lexer(source).run();

        OutputHelper.LexerOutput(list);
    }
}