/*
@Time    : 2023/12/11 22:46
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class MipsDivInst extends QuadrupleInst {
    public MipsDivInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "div " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
