/*
@Time    : 2023/12/11 23:35
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class SneInst extends QuadrupleInst {
    public SneInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public String toString() {
        return "sne " + resultReg + ", " + reg1 + ", " + reg2 + "\n";
    }

}
