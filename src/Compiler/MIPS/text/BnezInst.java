/*
@Time    : 2023/12/11 23:07
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class BnezInst extends MipsInst {
    public Reg reg;
    public String label;

    public BnezInst(Reg reg, String label) {
        this.reg = reg;
        this.label = label;
    }

    public String toString() {
        return "bnez " + reg + ", " + label + "\n";
    }
}
