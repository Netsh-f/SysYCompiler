/*
@Time    : 2023/12/11 20:39
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class LwInst extends MipsInst {
    public MipsAddr mipsAddr;

    public LwInst(Reg resultReg, MipsAddr mipsAddr) {
        this.resultReg = resultReg;
        this.mipsAddr = mipsAddr;
    }

    public String toString() {
        return "lw " + resultReg + ", " + mipsAddr + "\n";
    }
}
