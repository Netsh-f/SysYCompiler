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
import Compiler.LLVMIR.Operand.GlobalOperand;
import Compiler.MIPS.regs.Reg;
import Compiler.MIPS.regs.RegManager;
import Compiler.MIPS.text.MipsAddr;
import Compiler.MIPS.text.Quadruple.AddiuInst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

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
                globalConst.operand.mipsAddr = new MipsAddr(globalConst.operand.ident, RegManager.regMap.get("zero"));
            } else if (globalDecl instanceof GlobalStr globalStr) {
                mipsManager.addAsciizDataLabel(globalStr.operand.ident, globalStr.content);
                globalStr.operand.mipsAddr = new MipsAddr(globalStr.operand.ident, RegManager.regMap.get("zero"));
            } else {
                break;
            }
        }
        for (; globalDeclIndex < irModule.globalDeclList.size(); globalDeclIndex++) {
            var globalDecl = irModule.globalDeclList.get(globalDeclIndex);
            if (globalDecl instanceof Function function) {

                AddiuInst startAddiuInst = new AddiuInst(RegManager.regMap.get("sp"), RegManager.regMap.get("sp"), 0);
                AddiuInst endAddiuInst = new AddiuInst(RegManager.regMap.get("sp"), RegManager.regMap.get("sp"), 0);
                int maxFuncParamNum = getMaxFuncParamNum(function);
                AtomicInteger stackSize = new AtomicInteger();
                int spOff = 0;

                for (int i = 0; i < function.basicBlockList.size(); i++) {
                    var basicBlock = function.basicBlockList.get(i);
                    if (i == 0) {
                        mipsManager.addMipsBlock(function.ident);
                        mipsManager.addAddiuInst(startAddiuInst);
                        spOff -= 4;
                        mipsManager.addSwInst(RegManager.regMap.get("ra"), new MipsAddr(stackSize, spOff, RegManager.regMap.get("sp")));
                        spOff -= 4;
                        mipsManager.addSwInst(RegManager.regMap.get("fp"), new MipsAddr(stackSize, spOff, RegManager.regMap.get("sp")));
                        mipsManager.addMoveInst(RegManager.regMap.get("fp"), RegManager.regMap.get("sp"));

                        //给形参传值
                        for (int j = 0; j < 4 && j < function.paramOperandList.size(); j++) {
                            function.paramOperandList.get(j).reg = RegManager.regMap.get("a" + j);
                        }
                        for (int j = 4; j < function.paramOperandList.size(); j++) {
                            function.paramOperandList.get(j).reg = mipsManager.addLwInst(new MipsAddr(stackSize, 64 + (j - 4) * 4, RegManager.regMap.get("fp")));
                        }
                    } else {
                        mipsManager.addMipsBlock(function.ident + "__" + basicBlock.label);
                    }
                    for (var instruction : basicBlock.instructionList) {
                        if (instruction instanceof AllocaInst allocaInst) {
                            spOff -= allocaInst.resultOperand.irType.getLength() * 4;
                            allocaInst.resultOperand.mipsAddr = new MipsAddr(stackSize, spOff, RegManager.regMap.get("fp"));
                        } else if (instruction instanceof StoreInst storeInst) {
                            visit(storeInst);
                        } else if (instruction instanceof GetElementPtrInst getElementPtrInst) {
                            visit(getElementPtrInst, stackSize);
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
                        } else if (instruction instanceof CallGetIntInst callGetIntInst) {
                            visit(callGetIntInst);
                        } else if (instruction instanceof CallPutStrInst callPutStrInst) {
                            visit(callPutStrInst);
                        } else if (instruction instanceof CallPutIntInst callPutIntInst) {
                            visit(callPutIntInst);
                        } else if (instruction instanceof ZextInst zextInst) {
                            visit(zextInst);
                        } else if (instruction instanceof RetInst retInst) {
                            if (retInst.operand instanceof ConstantOperand constantOperand) {
                                retInst.operand.reg = mipsManager.addLiInst(constantOperand.intNumber);
                            }
                            if (retInst.operand.reg != null) {
                                mipsManager.addMoveInst(RegManager.regMap.get("v0"), retInst.operand.reg);
                            }
                            mipsManager.addMoveInst(RegManager.regMap.get("sp"), RegManager.regMap.get("fp"));
                            mipsManager.addLwInst(RegManager.regMap.get("fp"), new MipsAddr(stackSize, -8, RegManager.regMap.get("sp")));
                            mipsManager.addLwInst(RegManager.regMap.get("ra"), new MipsAddr(stackSize, -4, RegManager.regMap.get("sp")));
                            mipsManager.addAddiuInst(endAddiuInst);
                            if (function.ident == "main") {
                                mipsManager.addLiInst(RegManager.regMap.get("v0"), 10);
                                mipsManager.addSyscallInst();
                            } else {
                                mipsManager.addJrInst(RegManager.regMap.get("ra"));
                            }
                        } else if (instruction instanceof CallInst callInst) {
                            for (int j = 0; j < 4 && j < callInst.paramOperandList.size(); j++) {
                                var paramOperand = callInst.paramOperandList.get(j);
                                Reg paramReg = paramOperand.reg;
                                if (paramOperand instanceof ConstantOperand constantOperand) {
                                    paramReg = mipsManager.addLiInst(constantOperand.intNumber);
                                }
                                mipsManager.addMoveInst(RegManager.regMap.get("a" + j), paramReg);
                            }
                            for (int j = 4; j < callInst.paramOperandList.size(); j++) {
                                var paramOperand = callInst.paramOperandList.get(j);
                                Reg paramReg = paramOperand.reg;
                                if (paramOperand instanceof ConstantOperand constantOperand) {
                                    paramReg = mipsManager.addLiInst(constantOperand.intNumber);
                                }
                                mipsManager.addSwInst(paramReg, new MipsAddr(new AtomicInteger(0), 64 + (j - 4) * 4, RegManager.regMap.get("sp")));
                            }

//                            for (int j = 0; j < 16; j++) { // 保存现场
//                                if (j < 8) {
//                                    mipsManager.addSwInst(RegManager.regMap.get("t" + j), new MipsAddr(new AtomicInteger(0), i * 4, RegManager.regMap.get("sp")));
//                                } else {
//                                    mipsManager.addSwInst(RegManager.regMap.get("s" + (j - 8)), new MipsAddr(new AtomicInteger(0), i * 4, RegManager.regMap.get("sp")));
//                                }
//                            }
                            mipsManager.addJalInst(callInst.function.ident);
//                            for (int j = 0; j < 16; j++) {
//                                if (j < 8) {
//                                    mipsManager.addLwInst(RegManager.regMap.get("t" + j), new MipsAddr(new AtomicInteger(0), i * 4, RegManager.regMap.get("sp")));
//                                } else {
//                                    mipsManager.addLwInst(RegManager.regMap.get("s" + (j - 8)), new MipsAddr(new AtomicInteger(0), i * 4, RegManager.regMap.get("sp")));
//                                }
//                            }

                            if (callInst.resultOperand != null) {
                                callInst.resultOperand.reg = mipsManager.addMoveInst(RegManager.regMap.get("v0"));
                            }
                        }
                    }
                }
                spOff -= Math.max(maxFuncParamNum - 4, 0) * 4;
                int finalStackSize = -spOff + 64; // 保存现场用
                stackSize.updateAndGet(x -> x + finalStackSize);
                startAddiuInst.immediateNum = -finalStackSize;
                endAddiuInst.immediateNum = finalStackSize;
            }
        }
        return mipsManager.getMipsModule();
    }

    private void visit(ZextInst zextInst) {
        mipsManager.addMoveInst(zextInst.resultOperand.reg, zextInst.valueOperand.reg);
    }

    private void visit(CallPutIntInst callPutIntInst) {
        Reg valueReg = callPutIntInst.operand.reg;
        if (callPutIntInst.operand instanceof ConstantOperand constantOperand) {
            valueReg = mipsManager.addLiInst(constantOperand.intNumber);
        }
        mipsManager.addMoveInst(RegManager.regMap.get("a0"), valueReg);
        mipsManager.addLiInst(RegManager.regMap.get("v0"), 1);
        mipsManager.addSyscallInst();
    }

    private void visit(CallPutStrInst callPutStrInst) {
        mipsManager.addLaInst(RegManager.regMap.get("a0"), callPutStrInst.valueOperand.mipsAddr);
        mipsManager.addLiInst(RegManager.regMap.get("v0"), 4);
        mipsManager.addSyscallInst();
    }

    private void visit(CallGetIntInst callGetIntInst) {
        mipsManager.addLiInst(RegManager.regMap.get("v0"), 5);
        mipsManager.addSyscallInst();
        callGetIntInst.resultOperand.reg = mipsManager.addMoveInst(RegManager.regMap.get("v0"));
    }

    private static int getMaxFuncParamNum(Function function) {
        int maxFuncParamNum = 0;
        for (var basicBlock : function.basicBlockList) {
            for (var instruction : basicBlock.instructionList) {
                if (instruction instanceof CallInst callInst) {
                    if (callInst.paramOperandList.size() > maxFuncParamNum) {
                        maxFuncParamNum = callInst.paramOperandList.size();
                    }
                }
            }
        }
        return maxFuncParamNum;
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
            if (brInst.condOperand instanceof ConstantOperand constantOperand) {
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
        addInst.resultOperand.reg = mipsManager.addAdduInst(reg1, reg2);
    }

    private void visit(LoadInst loadInst) {
        loadInst.resultOperand.reg = mipsManager.addLwInst(loadInst.pointerOperand.mipsAddr);
    }

    private void visit(GetElementPtrInst getElementPtrInst, AtomicInteger stackSize) {
        String label = null;
        if (getElementPtrInst.ptrOperand instanceof GlobalOperand) {
            if (getElementPtrInst.ptrOperand.mipsAddr.label != null && getElementPtrInst.ptrOperand.mipsAddr.label.startsWith("sysystr")) { // 如果是全局的字符串
                getElementPtrInst.resultOperand.mipsAddr = new MipsAddr(getElementPtrInst.ptrOperand.mipsAddr.label, RegManager.regMap.get("zero"));
                return;
            } else {
                label = getElementPtrInst.ptrOperand.mipsAddr.label;
            }
        }
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
                resultReg = mipsManager.addAdduInst(tReg, resultReg);
            }
        }
        if (label != null) {
            getElementPtrInst.resultOperand.mipsAddr = new MipsAddr(label, resultReg);
        } else {
            resultReg = mipsManager.addAdduInst(resultReg, RegManager.regMap.get("fp"));
            getElementPtrInst.resultOperand.mipsAddr = new MipsAddr(stackSize, getElementPtrInst.ptrOperand.mipsAddr.off, resultReg);
        }
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
