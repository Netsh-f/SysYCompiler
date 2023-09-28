package Compiler.Lexer;

import java.util.HashMap;
import java.util.Map;

public class ReserveWord {
    static Map<String, LexType> reserveWordsMap = initReserveWordsMap();

    private ReserveWord() {
    }

    static public boolean isReserveWord(String s) {
        return reserveWordsMap.containsKey(s);
    }

    static private Map<String, LexType> initReserveWordsMap() {
        Map<String, LexType> map = new HashMap<>();
        map.put("main", LexType.MAINTK);
        map.put("const", LexType.CONSTTK);
        map.put("int", LexType.INTTK);
        map.put("break", LexType.BREAKTK);
        map.put("continue", LexType.CONTINUETK);
        map.put("if", LexType.IFTK);
        map.put("else", LexType.ELSETK);
        map.put("!", LexType.NOT);
        map.put("&&", LexType.AND);
        map.put("||", LexType.OR);
        map.put("for", LexType.FORTK);
        map.put("getint", LexType.GETINTTK);
        map.put("printf", LexType.PRINTFTK);
        map.put("return", LexType.RETURNTK);
        map.put("+", LexType.PLUS);
        map.put("-", LexType.MINU);
        map.put("void", LexType.VOIDTK);
        map.put("*", LexType.MULT);
        map.put("/", LexType.DIV);
        map.put("%", LexType.MOD);
        map.put("<", LexType.LSS);
        map.put("<=", LexType.LEQ);
        map.put(">", LexType.GRE);
        map.put(">=", LexType.GEQ);
        map.put("==", LexType.EQL);
        map.put("!=", LexType.NEQ);
        map.put("=", LexType.ASSIGN);
        map.put(";", LexType.SEMICN);
        map.put(",", LexType.COMMA);
        map.put("(", LexType.LPARENT);
        map.put(")", LexType.RPARENT);
        map.put("[", LexType.LBRACK);
        map.put("]", LexType.RBRACK);
        map.put("{", LexType.LBRACE);
        map.put("}", LexType.RBRACE);
        return map;
    }
}
