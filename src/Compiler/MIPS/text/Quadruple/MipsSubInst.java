/*
@Time    : 2023/12/11 19:55
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class MipsSubInst extends QuadrupleInst {
    public MipsSubInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "sub " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
