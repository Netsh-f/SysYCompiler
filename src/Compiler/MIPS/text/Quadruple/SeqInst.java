/*
@Time    : 2023/12/11 23:35
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class SeqInst extends QuadrupleInst{
    public SeqInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "seq " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }
}
