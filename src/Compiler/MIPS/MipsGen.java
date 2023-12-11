/*
@Time    : 2023/12/11 14:48
@Author  : Elaikona
*/
package Compiler.MIPS;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Global.GlobalConst;
import Compiler.LLVMIR.Global.GlobalStr;
import Compiler.LLVMIR.IRModule;
import Compiler.LLVMIR.Instructions.*;
import Compiler.LLVMIR.Instructions.Quadruple.*;
import Compiler.LLVMIR.Operand.ConstantOperand;
import Compiler.MIPS.regs.Reg;
import Compiler.MIPS.text.MipsAddr;

import java.util.ArrayList;
import java.util.Collections;

public class MipsGen {
    private final IRModule irModule;
    private final MipsManager mipsManager;

    public MipsGen(IRModule irModule) {
        this.irModule = irModule;
        this.mipsManager = new MipsManager();
    }

    public MipsModule run() {
        return irModuleVisitor();
    }

    private MipsModule irModuleVisitor() {
        int globalDeclIndex;
        for (globalDeclIndex = 0; globalDeclIndex < irModule.globalDeclList.size(); globalDeclIndex++) {
            var globalDecl = irModule.globalDeclList.get(globalDeclIndex);
            if (globalDecl instanceof GlobalConst globalConst) {
                var values = globalConst.values;
                if (values.isEmpty()) { // 若没有初始值则设置为0
                    values = new ArrayList<>(Collections.nCopies(globalConst.operand.irType.getLength(), 0));
                }
                mipsManager.addWordDataLabel(globalConst.operand.ident, values);
            } else if (globalDecl instanceof GlobalStr globalStr) {
                mipsManager.addAsciizDataLabel(globalStr.operand.ident, globalStr.content);
            } else {
                break;
            }
        }
        for (; globalDeclIndex < irModule.globalDeclList.size(); globalDeclIndex++) {
            var globalDecl = irModule.globalDeclList.get(globalDeclIndex);
            if (globalDecl instanceof Function function) {
                for (int i = 0; i < function.basicBlockList.size(); i++) {
                    var basicBlock = function.basicBlockList.get(i);
                    if (i == 0) {
                        mipsManager.addMipsBlock(function.ident);
                    } else {
                        mipsManager.addMipsBlock(function.ident + "__" + basicBlock.label);
                    }
                    basicBlock.instructionList.forEach(instruction -> {
                        if (instruction instanceof AllocaInst allocaInst) {
                            visit(allocaInst);
                        } else if (instruction instanceof StoreInst storeInst) {
                            visit(storeInst);
                        } else if (instruction instanceof GetElementPtrInst getElementPtrInst) {
                            visit(getElementPtrInst);
                        } else if (instruction instanceof LoadInst loadInst) {
                            visit(loadInst);
                        } else if (instruction instanceof AddInst addInst) {
                            visit(addInst);
                        } else if (instruction instanceof SubInst subInst) {
                            visit(subInst);
                        } else if (instruction instanceof MulInst mulInst) {
                            visit(mulInst);
                        } else if (instruction instanceof SdivInst sdivInst) {
                            visit(sdivInst);
                        } else if (instruction instanceof SremInst sremInst) {
                            visit(sremInst);
                        } else if (instruction instanceof BrInst brInst) {
                            visit(brInst, function);
                        } else if (instruction instanceof IcmpInst icmpInst) {
                            visit(icmpInst);
                        }
                    });
                }
            }
        }
        return mipsManager.getMipsModule();
    }

    private void visit(IcmpInst icmpInst) {
        Reg reg1 = icmpInst.operand1.reg, reg2 = icmpInst.operand2.reg;
        if (icmpInst.operand1 instanceof ConstantOperand constantOperand) {
            reg1 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        if (icmpInst.operand2 instanceof ConstantOperand constantOperand) {
            reg2 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        switch (icmpInst.cond) {
            case SLT -> icmpInst.resultOperand.reg = mipsManager.addSltInst(reg1, reg2);
            case SLE -> icmpInst.resultOperand.reg = mipsManager.addSleInst(reg1, reg2);
            case SGT -> icmpInst.resultOperand.reg = mipsManager.addSgtInst(reg1, reg2);
            case SGE -> icmpInst.resultOperand.reg = mipsManager.addSgeInst(reg1, reg2);
            case EQ -> icmpInst.resultOperand.reg = mipsManager.addSeqInst(reg1, reg2);
            case NE -> icmpInst.resultOperand.reg = mipsManager.addSneInst(reg1, reg2);
        }
    }

    private void visit(BrInst brInst, Function function) {
        if (brInst.condOperand == null) {
            // br label <dest>
            mipsManager.addJInst(function.ident + "__" + brInst.trueBasicBlock.label);
        } else {
            // br i1 <cond>, label <iftrue>, label <iffalse>
            Reg condReg = brInst.condOperand.reg;
            if (brInst.condOperand instanceof ConstantOperand constantOperand) { // todo 是否有这种情况？
                condReg = mipsManager.addLiInst(constantOperand.intNumber);
            }
            mipsManager.addBnezInst(condReg, function.ident + "__" + brInst.trueBasicBlock.label);
            mipsManager.addJInst(function.ident + "__" + brInst.falseBasicBlock.label);
        }
    }

    private void visit(SremInst sremInst) {
        Reg reg1 = sremInst.operand1.reg, reg2 = sremInst.operand2.reg;
        if (sremInst.operand1 instanceof ConstantOperand constantOperand) {
            reg1 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        if (sremInst.operand2 instanceof ConstantOperand constantOperand) {
            reg2 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        sremInst.resultOperand.reg = mipsManager.addRemInst(reg1, reg2);
    }

    private void visit(SdivInst sdivInst) {
        Reg reg1 = sdivInst.operand1.reg, reg2 = sdivInst.operand2.reg;
        if (sdivInst.operand1 instanceof ConstantOperand constantOperand) {
            reg1 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        if (sdivInst.operand2 instanceof ConstantOperand constantOperand) {
            reg2 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        sdivInst.resultOperand.reg = mipsManager.addDivInst(reg1, reg2);
    }

    private void visit(MulInst mulInst) {
        Reg reg1 = mulInst.operand1.reg, reg2 = mulInst.operand2.reg;
        if (mulInst.operand1 instanceof ConstantOperand constantOperand) {
            reg1 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        if (mulInst.operand2 instanceof ConstantOperand constantOperand) {
            reg2 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        mulInst.resultOperand.reg = mipsManager.addMulInst(reg1, reg2);
    }

    private void visit(SubInst subInst) {
        Reg reg1 = subInst.operand1.reg, reg2 = subInst.operand2.reg;
        if (subInst.operand1 instanceof ConstantOperand constantOperand) {
            reg1 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        if (subInst.operand2 instanceof ConstantOperand constantOperand) {
            reg2 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        subInst.resultOperand.reg = mipsManager.addSubInst(reg1, reg2);
    }

    private void visit(AddInst addInst) {
        Reg reg1 = addInst.operand1.reg, reg2 = addInst.operand2.reg;
        if (addInst.operand1 instanceof ConstantOperand constantOperand) {
            reg1 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        if (addInst.operand2 instanceof ConstantOperand constantOperand) {
            reg2 = mipsManager.addLiInst(constantOperand.intNumber);
        }
        addInst.resultOperand.reg = mipsManager.addAddInst(reg1, reg2);
    }

    private void visit(LoadInst loadInst) {
        loadInst.resultOperand.reg = mipsManager.addLwInst(loadInst.pointerOperand.mipsAddr);
    }

    private void visit(GetElementPtrInst getElementPtrInst) {
        var indexOperandList = getElementPtrInst.indexOperandList;
        Reg resultReg = null;
        for (int i = 0; i < 2 && i < indexOperandList.size(); i++) {
            Reg indexReg;
            if (indexOperandList.get(indexOperandList.size() - 1 - i) instanceof ConstantOperand constantOperand) {
                indexReg = mipsManager.addLiInst(constantOperand.intNumber);
            } else {
                indexReg = indexOperandList.get(indexOperandList.size() - 1 - i).reg;
            }

            if (i == 0) {
                resultReg = mipsManager.addMulInst(indexReg, 4);
            } else if (i == 1) {
                var tReg = mipsManager.addMulInst(indexReg, 4 * getElementPtrInst.ptrOperand.irType.shape.get(0));
                resultReg = mipsManager.addAddInst(tReg, resultReg);
            }
        }
        getElementPtrInst.resultOperand.mipsAddr = new MipsAddr(0, resultReg);
    }

    private void visit(AllocaInst allocaInst) {
        allocaInst.resultOperand.mipsAddr = mipsManager.allocMemory(allocaInst.resultOperand.irType.getLength());
    }

    private void visit(StoreInst storeInst) {
        Reg valueReg;
        if (storeInst.value instanceof ConstantOperand constantOperand) {
            valueReg = mipsManager.addLiInst(constantOperand.intNumber);
        } else {
            valueReg = storeInst.value.reg;
        }

        mipsManager.addSwInst(valueReg, storeInst.pointer.mipsAddr);
    }
}
