/*
@Time    : 2023/12/11 17:40
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class LiInst extends MipsInst {
    public int immediateNum;

    public LiInst(Reg resultReg, int immediateNum) {
        this.resultReg = resultReg;
        this.immediateNum = immediateNum;
    }

    public String toString() {
        return "li " + resultReg + ", " + immediateNum + "\n";
    }
}
