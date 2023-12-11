/*
@Time    : 2023/12/11 22:55
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class MipsRemInst extends QuadrupleInst {
    public MipsRemInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "rem " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
