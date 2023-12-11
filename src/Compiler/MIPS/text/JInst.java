/*
@Time    : 2023/12/11 23:10
@Author  : Elaikona
*/
package Compiler.MIPS.text;

public class JInst extends MipsInst {
    public String label;

    public JInst(String label) {
        this.label = label;
    }

    public String toString() {
        return "j " + label + "\n";
    }
}
