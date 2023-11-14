/*
@Time    : 2023/11/14 12:01
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.SymbolManager.Symbol.ValueType;

public class AllocaInst extends Instruction {
    public Operand resultOperand;
    public ValueType valueType;

    public AllocaInst(Operand resultOperand, ValueType valueType) {
        this.resultOperand = resultOperand;
        this.valueType = valueType;
    }

    public String toString() {
        return resultOperand + " = alloca " + valueType + "\n";
    }
}
