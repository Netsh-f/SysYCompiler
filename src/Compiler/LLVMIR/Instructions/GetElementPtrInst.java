/*
@Time    : 2023/11/14 12:55
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

import java.util.List;

public class GetElementPtrInst extends Instruction {
    public Operand ptrOperand;
    public List<Operand> indexOperandList;

    public GetElementPtrInst(TempOperand resultOperand, Operand ptrOperand, List<Operand> indexOperandList) {
        this.resultOperand = resultOperand;
        this.ptrOperand = ptrOperand;
        this.indexOperandList = indexOperandList;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.resultOperand).append(" = getelementptr ").append(ptrOperand.irType.toStringWithoutPtr())
                .append(", ").append(ptrOperand.irType.toStringWithoutPtr()).append("* ").append(ptrOperand);
        indexOperandList.forEach(index -> {
            stringBuilder.append(", ").append(index.irType).append(" ").append(index);
        });
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
