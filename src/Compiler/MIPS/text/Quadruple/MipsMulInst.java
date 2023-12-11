/*
@Time    : 2023/12/11 22:24
@Author  : Elaikona
*/
package Compiler.MIPS.text.Quadruple;

import Compiler.MIPS.regs.Reg;

public class MipsMulInst extends QuadrupleInst {
    public int immediateNum;

    public MipsMulInst(Reg resultReg, Reg reg1, Reg reg2) {
        super(resultReg, reg1, reg2);
    }

    public MipsMulInst(Reg resultReg, Reg reg1, int immediateNum) {
        super(resultReg, reg1, null);
        this.immediateNum = immediateNum;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mul ").append(resultReg).append(", ").append(reg1).append(", ");
        if (reg2 == null) {
            stringBuilder.append(immediateNum);
        } else {
            stringBuilder.append(reg2);
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
