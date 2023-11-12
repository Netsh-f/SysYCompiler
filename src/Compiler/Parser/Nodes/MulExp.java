/*
@Time    : 2023/9/29 20:15
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.Lexer.LexType;

import java.util.List;

public class MulExp extends BaseNode {
    public List<UnaryExp> unaryExpList;
    public List<LexType> opLexTypeList;

    public MulExp(List<UnaryExp> unaryExpList, List<LexType> opLexTypeList) {
        this.unaryExpList = unaryExpList;
        this.opLexTypeList = opLexTypeList;
    }
}
