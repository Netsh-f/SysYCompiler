package Compiler.Lexer;

import Enums.LexType;

public record Token(LexType lexType, int lineNum, String content) {
}
