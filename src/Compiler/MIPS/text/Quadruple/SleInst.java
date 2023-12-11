/*
@Time    : 2023/12/11 23:32
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class SleInst extends QuadrupleInst {
    public SleInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "sle " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
