import Compiler.Lexer.Lexer;
import Compiler.Parser.Parser;
import Compiler.Visitor.Visitor;
import Utils.FileHelper;
import Utils.OutputHelper;


public class Compiler {
    public static void main(String[] args) {
        String source = FileHelper.fileToString("testfile.txt");

        var tokens = new Lexer(source).run();
//        OutputHelper.LexerOutput(tokens);
        var compUnit = new Parser(tokens).run();
        OutputHelper.ParserOutput();
        new Visitor(compUnit).run();
        OutputHelper.ErrorOutput();
    }
}