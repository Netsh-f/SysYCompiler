/*
@Time    : 2023/12/11 15:38
@Author  : Elaikona
*/
package Compiler.MIPS;

import Compiler.MIPS.data.AsciizDataLabel;
import Compiler.MIPS.data.WordDataLabel;
import Compiler.MIPS.regs.Reg;
import Compiler.MIPS.text.MipsBlock;

import java.util.ArrayList;
import java.util.List;

public class MipsManager {
    private MipsModule mipsModule;
    private MipsBlock currentMipsBlock;
    private int sp;
    private List<Reg> tRegList;

    public MipsManager() {
        this.mipsModule = new MipsModule();
        this.sp = 0;
        this.tRegList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            tRegList.add(new Reg("t" + i));
        }
    }

    public Reg allocTReg() {
        for (Reg reg : tRegList) {
            if (reg.isAvailable) {
                reg.isAvailable = false;
                return reg;
            }
        }
        return null;
    }

    public void freeReg(Reg reg) {
        reg.isAvailable = true;
    }

    public int allocMemory(int word) {
        int ret = sp;
        sp -= word * 4;
        return ret;
    }

    public void addMipsBlock(String ident) {
        currentMipsBlock = new MipsBlock(ident);
        mipsModule.mipsBlockList.add(currentMipsBlock);
    }

    public void addWordDataLabel(String ident, List<Integer> values) {
        mipsModule.dataLabelList.add(new WordDataLabel(ident, values));
    }

    public void addAsciizDataLabel(String ident, String value) {
        mipsModule.dataLabelList.add(new AsciizDataLabel(ident, value));
    }

    public MipsModule getMipsModule() {
        return mipsModule;
    }
}
