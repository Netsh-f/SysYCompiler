/*
@Time    : 2023/12/11 19:56
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;
import Compiler.MIPS.text.MipsInst;

public class QuadrupleInst extends MipsInst {
    public Reg reg1;
    public Reg reg2;

    public QuadrupleInst(Reg resultReg, Reg reg1, Reg reg2) {
        this.resultReg = resultReg;
        this.reg1 = reg1;
        this.reg2 = reg2;
    }
}
