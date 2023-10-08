import Compiler.Lexer.Lexer;
import Compiler.Parser.Parser;
import Utils.FileHelper;


public class Compiler {
    public static void main(String[] args) {
        String source = FileHelper.fileToString("testfile.txt");

        var tokens = new Lexer(source).run();
        var compUnit = new Parser(tokens).run();
    }
}