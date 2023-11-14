/*
@Time    : 2023/11/14 13:59
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.SymbolManager.Symbol.ValueType;

public class StoreInst extends Instruction{
    public Operand value;
    public Operand pointer;

    public StoreInst(Operand value, Operand pointer) {
        this.value = value;
        this.pointer = pointer;
    }

    @Override
    public String toString() {
        return "store " + value.irType + " " + value + ", " + pointer.irType + "* " + pointer + "\n";
    }
}
