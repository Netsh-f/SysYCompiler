/*
@Time    : 2023/11/21 16:33
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.BasicBlock;
import Compiler.LLVMIR.Operand.Operand;

public class BrInst extends Instruction {
    public Operand condOperand;
    public BasicBlock trueBasicBlock;
    public BasicBlock falseBasicBlock;

    public BrInst(Operand condOperand, BasicBlock trueBasicBlock, BasicBlock falseBasicBlock) {
        // br i1 <cond>, label <iftrue>, label <iffalse>
        this.condOperand = condOperand;
        this.trueBasicBlock = trueBasicBlock;
        this.falseBasicBlock = falseBasicBlock;
    }

    public BrInst(BasicBlock trueBasicBlock) {
        // br label <dest>
        this.trueBasicBlock = trueBasicBlock;
    }

    public String toString() {
        if (condOperand == null) {
            return "br label %" + trueBasicBlock.label + "\n";
        } else {
            return "br i1 " + condOperand + ", label %" + trueBasicBlock.label + ", label %" + falseBasicBlock.label + "\n";
        }
    }
}
