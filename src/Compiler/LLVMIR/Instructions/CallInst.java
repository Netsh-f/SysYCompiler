/*
@Time    : 2023/11/15 15:26
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

import java.util.List;

public class CallInst extends Instruction {
    public Function function;
    public List<Operand> paramOperandList;

    public CallInst(Function function, List<Operand> paramOperandList) {
        this.resultOperand = null;
        this.function = function;
        this.paramOperandList = paramOperandList;
    }

    public CallInst(TempOperand resultOperand, Function function, List<Operand> paramOperandList) {
        this.resultOperand = resultOperand;
        this.function = function;
        this.paramOperandList = paramOperandList;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (resultOperand != null) {
            stringBuilder.append(resultOperand).append(" = ");
        }
        stringBuilder.append("call ").append(function.returnIRType).append(" @").append(function.ident).append("(");
        for (int i = 0; i < paramOperandList.size(); i++) {
            if (i != 0) {
                stringBuilder.append(", ");
            }
            var operand = paramOperandList.get(i);
            stringBuilder.append(operand.irType).append(" ").append(operand);
        }
        stringBuilder.append(")\n");
        return stringBuilder.toString();
    }
}
