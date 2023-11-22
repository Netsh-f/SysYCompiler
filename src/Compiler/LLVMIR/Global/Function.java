/*
@Time    : 2023/11/13 17:26
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.BasicBlock;
import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Instructions.AllocaInst;
import Compiler.LLVMIR.Instructions.LoadInst;
import Compiler.LLVMIR.Instructions.StoreInst;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.util.ArrayList;
import java.util.List;

public class Function extends GlobalDecl {

    public List<BasicBlock> basicBlockList;
    public String ident;
    public IRType returnIRType;
    private final LabelManager labelManager;
    private final List<Operand> paramOperandList;


    public Function(IRType returnIRType, String ident, List<VarSymbol> varSymbolList) {
        this.paramOperandList = new ArrayList<>();
        this.labelManager = new LabelManager();
        varSymbolList.forEach(varSymbol -> varSymbol.operand = allocTempOperand(new IRType(varSymbol.valueType)));
        this.basicBlockList = new ArrayList<>();
        basicBlockList.add(new BasicBlock());
        var currentBasicBlock = basicBlockList.get(0);

        varSymbolList.forEach(varSymbol -> {
            paramOperandList.add(varSymbol.operand);

            // 因为源语言Sysy不存在“指针”这一概念，也就是说作为函数参数的这个变量，不是指针，它不会改变(我永远只会取它，不会写它，它不会出现'='左边)，所以不再需要开一个 alloc + store 将其放在一个寄存器里面
            if (varSymbol.valueType.shape().isEmpty()) {
                // 非数组，开alloca store
                var tempOperand = allocTempOperand(new IRType(IRType.IRValueType.I32, true, varSymbol.operand.irType.shape));
                currentBasicBlock.instructionList.add(new AllocaInst(tempOperand));
                currentBasicBlock.instructionList.add(new StoreInst(varSymbol.operand, tempOperand));
                varSymbol.operand = tempOperand;
            }
        });
        this.returnIRType = returnIRType;
        this.ident = "@" + ident;
    }

    public void assignLabel() {
        paramOperandList.forEach(operand -> {
            if (operand instanceof TempOperand tempOperand) {
                tempOperand.setLabel(labelManager.allocLabel());
            }
        });
        basicBlockList.forEach(basicBlock -> {
            basicBlock.setLabel(labelManager.allocLabel());
            basicBlock.instructionList.forEach(instruction -> {
                if (instruction.resultOperand != null) {
                    instruction.resultOperand.setLabel(labelManager.allocLabel());
                }
            });
        });
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlockList.add(basicBlock);
    }

    public TempOperand allocTempOperand(IRType irType) {
        return new TempOperand(-1, irType);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\ndefine dso_local ").append(this.returnIRType).append(" ").
                append(this.ident).append("(");
        for (int i = 0; i < paramOperandList.size(); i++) {
            if (i != 0) {
                stringBuilder.append(", ");
            }
            var operand = paramOperandList.get(i);
            stringBuilder.append(operand.irType).append(" ").append(operand);
        }
        stringBuilder.append(") #0 {\n");
        basicBlockList.forEach(stringBuilder::append);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }
}
