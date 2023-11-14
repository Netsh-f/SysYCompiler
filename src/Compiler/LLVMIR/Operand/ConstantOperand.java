/*
@Time    : 2023/11/13 21:40
@Author  : Elaikona
*/
package Compiler.LLVMIR.Operand;

import Compiler.LLVMIR.IRType;

public class ConstantOperand extends Operand {
    public int intNumber;

    public ConstantOperand(int intNumber) {
        this.irType = new IRType(IRType.IRValueType.I32);
        this.intNumber = intNumber;
    }

    public String toString() {
        return String.valueOf(intNumber);
    }
}
