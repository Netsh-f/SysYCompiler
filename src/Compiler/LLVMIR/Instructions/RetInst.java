package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Value;

public class RetInst extends Instruction {
    public Value value;

    public RetInst(Value value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ret ");
        if (this.value.type == IRValueType.VOID) {
            stringBuilder.append("void");
        } else {
            stringBuilder.append(this.value.type).append(" ").append(this.value);
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
