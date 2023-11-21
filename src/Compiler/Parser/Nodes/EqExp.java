/*
@Time    : 2023/9/29 20:11
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.LLVMIR.BasicBlock;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.Lexer.LexType;

import java.util.List;

public class EqExp {
    public List<RelExp> relExpList;
    public List<LexType> opLexTypeList;
    public Operand operand;

    public BasicBlock eqExpBasicBlock;
    public BasicBlock nextLAndExpBasicBlock;
    public BasicBlock nextEqExpBasicBlock;

    public EqExp(List<RelExp> relExpList, List<LexType> opLexTypeList) {
        this.relExpList = relExpList;
        this.opLexTypeList = opLexTypeList;
    }
}
