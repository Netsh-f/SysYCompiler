/*
@Time    : 2023/9/29 20:10
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.LLVMIR.BasicBlock;

public class Cond {
    public LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }
}
