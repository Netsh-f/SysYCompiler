/*
@Time    : 2023/12/12 1:44
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class JrInst extends MipsInst {
    public Reg addrReg;

    public JrInst(Reg addrReg) {
        this.addrReg = addrReg;
    }

    public String toString() {
        return "jr " + addrReg + "\n";
    }
}
