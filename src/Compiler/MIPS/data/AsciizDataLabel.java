/*
@Time    : 2023/12/11 16:16
@Author  : Elaikona
*/
package Compiler.MIPS.data;

public class AsciizDataLabel extends DataLabel {
    public String label;
    public String value;

    public AsciizDataLabel(String label, String value) {
        this.label = label;
        this.value = value.replaceAll("\\\\0A", "\\\\n");
    }

    public String toString() {
        return label + ": .asciiz \"" + value + "\"\n";
    }
}
