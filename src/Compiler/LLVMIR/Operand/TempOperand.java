/*
@Time    : 2023/11/13 22:00
@Author  : Elaikona
*/
package Compiler.LLVMIR.Operand;

public class TempOperand extends Operand {
    public int label;

    public TempOperand(int label, IRValueType type) {
        this.label = label;
        this.type = type;
    }

    public String toString() {
        return "%" + this.label;
    }
}
