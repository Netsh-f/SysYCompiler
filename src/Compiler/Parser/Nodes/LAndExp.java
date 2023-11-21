/*
@Time    : 2023/9/29 20:14
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.LLVMIR.BasicBlock;

import java.util.List;

public class LAndExp {
    public List<EqExp> eqExpList;
    public BasicBlock lAndExpBasicBlock;
    public BasicBlock stmt1BasicBlock;
    public BasicBlock nextLAndExpBasicBlock;

    public LAndExp(List<EqExp> eqExpList) {
        this.eqExpList = eqExpList;
    }
}
