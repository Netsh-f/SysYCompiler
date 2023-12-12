/*
@Time    : 2023/12/11 21:02
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import Compiler.MIPS.regs.Reg;

import java.util.concurrent.atomic.AtomicInteger;

public class MipsAddr {
    public AtomicInteger stackSize;
    public int off;
    public Reg reg;
    public String label;

    public MipsAddr(AtomicInteger stackSize, int off, Reg reg) {
        this.stackSize = stackSize;
        this.off = off;
        this.reg = reg;
    }

    public MipsAddr(String label, Reg reg) {
        this.label = label;
        this.reg = reg;
    }

    public String toString() {
        if (label != null) {
            return label + "(" + reg + ")";
        }
        return (stackSize.get() + off) + "(" + reg + ")";
    }
}
