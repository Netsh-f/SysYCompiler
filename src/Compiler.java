import Compiler.LLVMIR.Global.Function;
import Compiler.Lexer.Lexer;
import Compiler.Parser.Parser;
import Compiler.Visitor.Visitor;
import Utils.FileHelper;
import Utils.OutputHelper;

import java.util.ArrayList;
import java.util.List;


public class Compiler {
    public static void main(String[] args) {
        String source = FileHelper.fileToString("testfile.txt");

        var tokens = new Lexer(source).run();
//        OutputHelper.LexerOutput(tokens);
        var compUnit = new Parser(tokens).run();
        OutputHelper.ParserOutput();
        var irModule = new Visitor(compUnit).run();
        OutputHelper.ErrorOutput();
        OutputHelper.llvmIROutput(irModule);
    }
}