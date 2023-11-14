package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Operand.Operand;

public class RetInst extends Instruction {
    public Operand operand;

    public RetInst(Operand operand) {
        this.operand = operand;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ret ");
        if (this.operand.irType.irValueType == IRType.IRValueType.VOID) {
            stringBuilder.append("void");
        } else {
            stringBuilder.append(this.operand.irType).append(" ").append(this.operand);
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
