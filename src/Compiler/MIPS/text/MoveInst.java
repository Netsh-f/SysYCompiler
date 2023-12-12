/*
@Time    : 2023/12/12 1:30
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class MoveInst extends MipsInst {
    public Reg valueReg;

    public MoveInst(Reg resultReg, Reg valueReg) {
        this.resultReg = resultReg;
        this.valueReg = valueReg;
    }

    public String toString() {
        return "move " + resultReg + ", " + valueReg + "\n";
    }
}
