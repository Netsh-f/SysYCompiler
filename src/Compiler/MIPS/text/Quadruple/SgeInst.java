/*
@Time    : 2023/12/11 23:33
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class SgeInst extends QuadrupleInst {
    public SgeInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "sge " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
