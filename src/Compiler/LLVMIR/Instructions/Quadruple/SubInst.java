/*
@Time    : 2023/11/13 21:59
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions.Quadruple;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

public class SubInst extends QuadrupleInst {
    public SubInst(TempOperand resultOperand, IRType.IRValueType type, Operand operand1, Operand operand2) {
        super(resultOperand, type, operand1, operand2);
    }

    @Override
    public String toString() {
        return this.resultOperand + " = sub " + this.type + " " + this.operand1 + ", " + this.operand2 + "\n";
    }
}
