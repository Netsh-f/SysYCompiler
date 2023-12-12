/*
@Time    : 2023/12/12 1:55
@Author  : Elaikona
*/
package Compiler.MIPS.text;

public class JalInst extends MipsInst {
    public String label;

    public JalInst(String label) {
        this.label = label;
    }

    public String toString() {
        return "jal " + label + "\n";
    }
}
