/*
@Time    : 2023/12/11 17:08
@Author  : Elaikona
*/
package Compiler.MIPS.text;

import java.util.ArrayList;
import java.util.List;

public class MipsBlock {
    public String label;
    public List<MipsInst> mipsInstList;

    public MipsBlock(String label) {
        this.label = label;
        this.mipsInstList = new ArrayList<>();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(label).append(":\n");
        mipsInstList.forEach(mipsInst -> stringBuilder.append("    ").append(mipsInst));
        return stringBuilder.toString();
    }
}
