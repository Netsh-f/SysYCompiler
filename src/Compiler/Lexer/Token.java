package Compiler.Lexer;

public record Token(LexType lexType, int lineNum, String content) {
    @Override
    public String toString() {
        return lexType.name() + " " + content;
    }
}
