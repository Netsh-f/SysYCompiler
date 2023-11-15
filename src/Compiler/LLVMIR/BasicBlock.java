/*
@Time    : 2023/11/13 17:27
@Author  : Elaikona
*/
package Compiler.LLVMIR;

import Compiler.LLVMIR.Instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value {
    public List<Instruction> instructionList;
    public int label;

    public BasicBlock(int label) {
        this.label = label;
        this.instructionList = new ArrayList<>();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        instructionList.forEach(instruction -> stringBuilder.append("    ").append(instruction));
        return stringBuilder.toString();
    }
}
