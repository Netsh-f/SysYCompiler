/*
@Time    : 2023/9/29 20:09
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.Lexer.LexType;

import java.util.List;

public class AddExp extends BaseNode {
    public List<MulExp> mulExpList;
    public List<LexType> opLexTypeList;

    public AddExp(List<MulExp> mulExpList, List<LexType> opLexTypeList) {
        this.mulExpList = mulExpList;
        this.opLexTypeList = opLexTypeList;
    }
}
