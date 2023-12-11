/*
@Time    : 2023/11/13 21:40
@Author  : Elaikona
*/
package Compiler.LLVMIR.Operand;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Value;
import Compiler.MIPS.regs.Reg;
import Compiler.MIPS.text.MipsAddr;

public class Operand extends Value {
    public IRType irType;
    public Reg reg;
    public MipsAddr mipsAddr;

    public Operand() {
    }

    public Operand(IRType irType) {
        this.irType = irType;
    }
}
