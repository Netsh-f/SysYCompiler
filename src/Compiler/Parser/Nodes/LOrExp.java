/*
@Time    : 2023/9/29 20:14
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.LLVMIR.BasicBlock;

import java.util.List;

public class LOrExp {
    public List<LAndExp> lAndExpList;
    public BasicBlock condBasicBlock;
    public BasicBlock stmt1BasicBlock;
    public BasicBlock stmt2BasicBlock;
    public BasicBlock stmt3BasicBlock;

    public LOrExp(List<LAndExp> lAndExpList) {
        this.lAndExpList = lAndExpList;
    }
}
