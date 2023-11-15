/*
@Time    : 2023/11/14 12:55
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

import java.util.List;

public class GetElementPtrInst extends Instruction {
    public Operand resultOperand;
    public Operand addrOperand;
    public List<Integer> indexes;

    public GetElementPtrInst(Operand resultOperand, Operand addrOperand, List<Integer> indexes) {
        this.resultOperand = resultOperand;
        this.addrOperand = addrOperand;
        this.indexes = indexes;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.resultOperand).append(" = getelementptr ").append(addrOperand.irType.toStringWithoutPtr())
                .append(", ").append(addrOperand.irType).append(" ").append(addrOperand);
        stringBuilder.append(", ").append(addrOperand.irType.irValueType).append(" 0");
        indexes.forEach(index -> {
            stringBuilder.append(", ").append(addrOperand.irType.irValueType).append(" ").append(index);
        });
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
