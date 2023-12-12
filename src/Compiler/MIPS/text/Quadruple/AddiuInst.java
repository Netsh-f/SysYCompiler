/*
@Time    : 2023/12/12 1:13
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class AddiuInst extends QuadrupleInst {
    public int immediateNum;

    public AddiuInst(Reg resultReg, Reg reg1, int immediateNum) {
        super(resultReg, reg1, null);
        this.immediateNum = immediateNum;
    }

    public String toString() {
        return "addiu " + resultReg + ", " + reg1 + ", " + immediateNum + "\n";
    }
}
