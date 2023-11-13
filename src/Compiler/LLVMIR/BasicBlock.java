/*
@Time    : 2023/11/13 17:27
@Author  : Elaikona
*/
package Compiler.LLVMIR;

import Compiler.LLVMIR.Instructions.Instruction;

import java.util.List;

public class BasicBlock extends Value {
    private List<Instruction> instructionList;
}
