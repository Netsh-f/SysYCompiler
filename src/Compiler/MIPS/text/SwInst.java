/*
@Time    : 2023/12/11 17:39
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class SwInst extends MipsInst {
    public Reg valueReg;
    public MipsAddr mipsAddr;

    public SwInst(Reg valueReg, MipsAddr mipsAddr) {
        this.valueReg = valueReg;
        this.mipsAddr = mipsAddr;
    }

    public String toString() {
        return "sw " + valueReg + ", " + mipsAddr + "\n";
    }
}
