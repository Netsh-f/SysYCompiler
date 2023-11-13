/*
@Time    : 2023/11/13 22:54
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions.Quadruple;

import Compiler.LLVMIR.Operand.Operand;

public class AddInst extends QuadrupleInst{
    public AddInst(Operand resultOperand, IRValueType type, Operand operand1, Operand operand2) {
        super(resultOperand, type, operand1, operand2);
    }

    @Override
    public String toString() {
        return this.resultOperand + " = add " + this.type + " " + this.operand1 + ", " + this.operand2 + "\n";
    }
}
