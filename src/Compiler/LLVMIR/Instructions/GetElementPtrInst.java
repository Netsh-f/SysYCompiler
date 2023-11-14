/*
@Time    : 2023/11/14 12:55
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.SymbolManager.Symbol.ValueType;

public class GetElementPtrInst extends Instruction{
    public Operand reaultOperand;
    public ValueType valueType;
    public Operand ptrVal;

}
