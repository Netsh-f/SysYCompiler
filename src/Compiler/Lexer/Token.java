package Compiler.Lexer;

public record Token(LexType lexType, int lineNum, String content) {
}
