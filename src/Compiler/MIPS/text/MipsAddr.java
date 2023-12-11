/*
@Time    : 2023/12/11 21:02
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

public class MipsAddr {
    public int off;
    public Reg reg;

    public MipsAddr(int off, Reg reg) {
        this.off = off;
        this.reg = reg;
    }

    public String toString() {
        return off + "(" + reg + ")";
    }
}
