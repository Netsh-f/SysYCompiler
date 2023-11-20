/*
@Time    : 2023/11/14 12:55
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

import java.util.List;

public class GetElementPtrInst extends Instruction {
    public Operand resultOperand;
    public Operand ptrOperand;
    public List<Integer> indexes;

    public GetElementPtrInst(Operand resultOperand, Operand ptrOperand, List<Integer> indexes) {
        this.resultOperand = resultOperand;
        this.ptrOperand = ptrOperand;
        this.indexes = indexes;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.resultOperand).append(" = getelementptr ").append(ptrOperand.irType.toStringWithoutPtr())
                .append(", ").append(ptrOperand.irType.toStringWithoutPtr()).append("* ").append(ptrOperand);
        indexes.forEach(index -> {
            stringBuilder.append(", ").append(ptrOperand.irType.irValueType).append(" ").append(index);
        });
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
