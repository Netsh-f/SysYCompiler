/*
@Time    : 2023/12/11 17:59
@Author  : Elaikona
*/
package Compiler.MIPS.regs;

public class Reg {
    public String label;
    public int number;

    public Reg(String label, int number) {
        this.label = label;
        this.number = number;
    }

    public String toString() {
        return "$" + label;
    }
}
