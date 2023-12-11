/*
@Time    : 2023/12/11 14:48
@Author  : Elaikona
*/
package Compiler.MIPS;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Global.GlobalConst;
import Compiler.LLVMIR.Global.GlobalStr;
import Compiler.LLVMIR.IRModule;
import Compiler.LLVMIR.Instructions.AllocaInst;
import Compiler.LLVMIR.Instructions.StoreInst;
import Compiler.LLVMIR.Operand.ConstantOperand;

import java.util.ArrayList;
import java.util.Collections;

public class MipsGen {
    private IRModule irModule;
    private MipsManager mipsManager;

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
                        }else if(instruction instanceof StoreInst storeInst){
                            visit(storeInst);
                        }
                    });
                }
            }
        }
        return mipsManager.getMipsModule();
    }

    private void visit(AllocaInst allocaInst) {
        allocaInst.resultOperand.sp = mipsManager.allocMemory(allocaInst.resultOperand.irType.getLength());
    }

    private void visit(StoreInst storeInst){
        if(storeInst.value instanceof ConstantOperand){

        }
    }
}
