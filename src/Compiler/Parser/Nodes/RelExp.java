/*
@Time    : 2023/9/29 20:15
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.Lexer.LexType;

import java.util.List;

public class RelExp {
    public List<AddExp> addExpList;
    public List<LexType> opLexTypeList;
    public Operand operand;

    public RelExp(List<AddExp> addExpList, List<LexType> opLexTypeList) {
        this.addExpList = addExpList;
        this.opLexTypeList = opLexTypeList;
    }
}
