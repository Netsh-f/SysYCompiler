/*
@Time    : 2023/12/11 17:59
@Author  : Elaikona
*/
package Compiler.MIPS.regs;

public class Reg {
    public String label;
    public boolean isAvailable;

    public Reg(String label) {
        this.label = label;
        this.isAvailable = true;
    }
}
