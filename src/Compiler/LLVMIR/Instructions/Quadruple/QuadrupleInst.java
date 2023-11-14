/*
@Time    : 2023/11/13 22:04
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions.Quadruple;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Instructions.Instruction;
import Compiler.LLVMIR.Operand.Operand;

public class QuadrupleInst extends Instruction {
    public Operand resultOperand;
    public IRType.IRValueType type;
    public Operand operand1;
    public Operand operand2;

    public QuadrupleInst(Operand resultOperand, IRType.IRValueType type, Operand operand1, Operand operand2) {
        this.resultOperand = resultOperand;
        this.type = type;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
}
