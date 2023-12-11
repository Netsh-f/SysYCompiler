/*
@Time    : 2023/12/11 17:59
@Author  : Elaikona
*/
package Compiler.MIPS.regs;

public class Reg {
    public String label;

    public Reg(String label) {
        this.label = label;
    }

    public String toString() {
        return "$" + label;
    }
}
