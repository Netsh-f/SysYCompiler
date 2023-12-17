/*
@Time    : 2023/12/11 15:25
@Author  : Elaikona
*/
package Compiler.MIPS;

import Compiler.MIPS.data.DataLabel;
import Compiler.MIPS.text.MipsBlock;

import java.util.ArrayList;
import java.util.List;

public class MipsModule {
    public List<DataLabel> dataLabelList;
    public List<MipsBlock> mipsBlockList;

    public MipsModule() {
        this.dataLabelList = new ArrayList<>();
        this.mipsBlockList = new ArrayList<>();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(".data\n");
        dataLabelList.forEach(dataLabel -> stringBuilder.append("    ").append(dataLabel));
        stringBuilder.append(".text\n");
        stringBuilder.append("    j main\n    nop\n");
        mipsBlockList.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
