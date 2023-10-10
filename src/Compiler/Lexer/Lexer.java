package Compiler.Lexer;


import Utils.CharHelper;
import Utils.Error.ErrorType;
import Utils.OutputHelper;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String source;
    private int pos;
    private int lineNum;
    private StringBuilder token;

    public Lexer(String source) {
        this.source = source;
        this.pos = 0;
        this.lineNum = 1;
        this.token = new StringBuilder();
    }

    public List<Token> run() {
        List<Token> list = new ArrayList<>();
        Token newToken = next();
        while (newToken.lexType() != LexType.LEXER_END) {
            list.add(newToken);
            newToken = next();
        }
        return list;
    }

    private void moveToNotBlankChar() {
        while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
            if (source.charAt(pos) == '\n') {
                lineNum++;
            }
            pos++;
        }
    }

    private char getNextChar() {
        if (pos < source.length()) {
            return source.charAt(pos++);
        }
        return 0;
    }

    private char tokenAppendGetNextChar(char c) {
        token.append(c);
        return getNextChar();
    }

    private void backwardPos() {
        if (pos > 0) {
            pos--;
        }
    }

    private void tokenAppend(char c) {
        token.append(c);
    }

    private Token getToken(LexType lexType) {
        return new Token(lexType, lineNum, token.toString());
    }

    private Token getToken() {
        return new Token(ReserveWord.reserveWordsMap.get(token.toString()), lineNum, token.toString());
    }

    public Token next() {
        token.setLength(0);
        moveToNotBlankChar();
        if (pos >= source.length()) {
            return getToken(LexType.LEXER_END);
        }
        char c = getNextChar();
        if (CharHelper.isAlphaOrUnderscore(c)) {
            c = tokenAppendGetNextChar(c);
            while (CharHelper.isAlnumOrUnderscore(c)) {
                c = tokenAppendGetNextChar(c);
            }
            backwardPos();
            if (ReserveWord.isReserveWord(token.toString())) {
                return getToken();
            }
            return getToken(LexType.IDENFR);
        } else if (c == '0') {
            tokenAppend(c);
            return getToken(LexType.INTCON);
        } else if (CharHelper.isNonZeroDigit(c)) {
            c = tokenAppendGetNextChar(c);
            while (Character.isDigit(c)) {
                c = tokenAppendGetNextChar(c);
            }
            backwardPos();
            return getToken(LexType.INTCON);
        } else if (c == '"') {
            while (true) {
                c = tokenAppendGetNextChar(c);
                if (CharHelper.isNormalChar(c)) {
                    continue;
                } else if (c == '%') {
                    c = tokenAppendGetNextChar(c);
                    if (c == 'd') {
                        continue;
                    } else {
                        OutputHelper.addError(ErrorType.FORMAT_STRING_ERROR, lineNum, "'%" + c + "' is illegal in <FormatString>");
                    }
                } else if (c == '\\') {
                    c = tokenAppendGetNextChar(c);
                    if (c == 'n') {
                        continue;
                    } else {
                        OutputHelper.addError(ErrorType.FORMAT_STRING_ERROR, lineNum, "'\\" + c + "' is illegal in <FormatString>");
                    }
                } else if (c == '"') {
                    tokenAppend(c);
                    return getToken(LexType.STRCON);
                } else {
                    OutputHelper.addError(ErrorType.FORMAT_STRING_ERROR, lineNum, "'" + c + "' is illegal in <FormatString>");
                }
            }
        } else if (c == '&') {
            c = tokenAppendGetNextChar(c);
            if (c == '&') {
                tokenAppend(c);
                return getToken();
            } else {
                backwardPos();
                return getToken(LexType.LEXER_ERROR);
            }
        } else if (c == '|') {
            c = tokenAppendGetNextChar(c);
            if (c == '|') {
                tokenAppend(c);
                return getToken();
            } else {
                backwardPos();
                return getToken(LexType.LEXER_ERROR);
            }
        } else if (c == '+' || c == '-' || c == '*' || c == '%' || c == ';' || c == ',' || c == '(' ||
                c == ')' || c == '[' || c == ']' || c == '{' || c == '}') {
            tokenAppend(c);
            return getToken();
        } else if (c == '<' || c == '>' || c == '=' || c == '!') {
            c = tokenAppendGetNextChar(c);
            if (c == '=') {
                tokenAppend(c);
            } else {
                backwardPos();
            }
            return getToken();
        } else if (c == '/') {
            c = tokenAppendGetNextChar(c);
            if (c == '/') {
                c = tokenAppendGetNextChar(c);
                while (c != '\n' && c != 0) {
                    c = tokenAppendGetNextChar(c);
                }
                lineNum++;
                return next();
            } else if (c == '*') {
                c = tokenAppendGetNextChar(c);
                while (c != 0) {
                    while (c != '*' && c != 0) {
                        if (c == '\n') {
                            lineNum++;
                        }
                        c = tokenAppendGetNextChar(c);
                    }
                    while (c == '*') {
                        c = tokenAppendGetNextChar(c);
                    }
                    if (c == '/') {
                        return next();
                    }
                }
            } else {
                backwardPos();
                return getToken();
            }
        }
        tokenAppend(c);
        return getToken(LexType.LEXER_ERROR);
    }
}
