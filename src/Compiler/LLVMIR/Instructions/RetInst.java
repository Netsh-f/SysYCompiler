package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Value;

public class RetInst extends Instruction {
    public Value value;

    public RetInst(Value value) {
        this.type = IRValueType.I32;
        this.value = value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ret ").append(this.value.type);
        if (this.value.type != IRValueType.VOID) {
            stringBuilder.append(" ").append(this.value);
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
