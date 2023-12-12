/*
@Time    : 2023/12/12 14:29
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class LaInst extends MipsInst {
    public MipsAddr mipsAddr;

    public LaInst(Reg resultReg, MipsAddr mipsAddr) {
        this.resultReg = resultReg;
        this.mipsAddr = mipsAddr;
    }

    public String toString() {
        return "la " + resultReg + ", " + mipsAddr + "\n";
    }
}
