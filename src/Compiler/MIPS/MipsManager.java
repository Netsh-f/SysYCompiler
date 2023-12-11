/*
@Time    : 2023/12/11 15:38
@Author  : Elaikona
*/
package Compiler.MIPS;

import Compiler.MIPS.data.AsciizDataLabel;
import Compiler.MIPS.data.WordDataLabel;
import Compiler.MIPS.regs.Reg;
import Compiler.MIPS.text.*;
import Compiler.MIPS.text.Quadruple.*;

import java.util.ArrayList;
import java.util.List;

public class MipsManager {
    private MipsModule mipsModule;
    private MipsBlock currentMipsBlock;
    private int spOff;
    private List<Reg> tRegList;
    private int tRegNum;

    public Reg spReg;

    public MipsManager() {
        this.mipsModule = new MipsModule();
        this.spOff = 0;
        this.tRegList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tRegList.add(new Reg("t" + i));
        }
        tRegNum = 0;
        spReg = new Reg("sp");
    }

    public Reg addSneInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new SneInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addSeqInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new SeqInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addSgeInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new SgeInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addSgtInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new SgtInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addSleInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new SleInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addSltInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new SltInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public void addJInst(String label) {
        addInst(new JInst(label));
    }

    public void addBnezInst(Reg reg, String label) {
        addInst(new BnezInst(reg, label));
    }

    public Reg addRemInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new MipsRemInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addDivInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new MipsDivInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addMulInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new MipsMulInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addMulInst(Reg reg1, int immediateNum) {
        var resultReg = allocTReg();
        addInst(new MipsMulInst(resultReg, reg1, immediateNum));
        return resultReg;
    }

    public Reg addAddInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new MipsAddInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addSubInst(Reg reg1, Reg reg2) {
        var resultReg = allocTReg();
        addInst(new MipsSubInst(resultReg, reg1, reg2));
        return resultReg;
    }

    public Reg addLwInst(MipsAddr mipsAddr) {
        var resultReg = allocTReg();
        addInst(new LwInst(resultReg, mipsAddr));
        return resultReg;
    }

    public void addSwInst(Reg valueReg, MipsAddr mipsAddr) {
        addInst(new SwInst(valueReg, mipsAddr));
    }

    private void addInst(MipsInst mipsInst) {
        currentMipsBlock.mipsInstList.add(mipsInst);
    }

    public Reg addLiInst(int immediateNum) {
        var reg = allocTReg();
        addInst(new LiInst(reg, immediateNum));
        return reg;
    }

    public Reg allocTReg() {
        tRegNum = (tRegNum + 1) % 4;
        return tRegList.get(tRegNum);
    }

    public MipsAddr allocMemory(int word) {
        int ret = spOff;
        spOff += word * 4;
        return new MipsAddr(-ret, spReg);
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
