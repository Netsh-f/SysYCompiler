/*
@Time    : 2023/12/11 23:25
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class SltInst extends QuadrupleInst {
    public SltInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "slt " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
