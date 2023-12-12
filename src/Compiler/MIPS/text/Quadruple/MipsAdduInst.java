/*
@Time    : 2023/12/11 21:15
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class MipsAdduInst extends QuadrupleInst {
    public MipsAdduInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "addu " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
