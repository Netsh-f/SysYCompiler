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

    public BasicBlock() {
        this.label = -1; // 暂不分配label，在进入function的basicBlockList的时候（设置currentBasicBlock时）再分配
        this.instructionList = new ArrayList<>();
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(label).append(":\n");
        instructionList.forEach(instruction -> stringBuilder.append("    ").append(instruction));
        return stringBuilder.toString();
    }
}
