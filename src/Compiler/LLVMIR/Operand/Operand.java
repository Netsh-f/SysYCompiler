/*
@Time    : 2023/11/13 21:40
@Author  : Elaikona
*/
package Compiler.LLVMIR.Operand;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Value;

public class Operand extends Value {
    public IRType irType;

    public Operand() {
    }

    public Operand(IRType irType) {
        this.irType = irType;
    }
}
