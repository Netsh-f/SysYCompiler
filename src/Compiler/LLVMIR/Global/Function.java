/*
@Time    : 2023/11/13 17:26
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.BasicBlock;
import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Instructions.AllocaInst;
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
    private final List<VarSymbol> varSymbolList;
    private List<Operand> paramOperandList;


    public Function(IRType returnIRType, String ident, List<VarSymbol> varSymbolList) {
        this.paramOperandList = new ArrayList<>();
        this.labelManager = new LabelManager();
        this.varSymbolList = varSymbolList;
        varSymbolList.forEach(varSymbol -> varSymbol.operand = allocTempOperand(new IRType(varSymbol.valueType)));
        this.basicBlockList = new ArrayList<>();
        basicBlockList.add(new BasicBlock(labelManager.allocLabel()));
        var currentBasicBlock = basicBlockList.get(0);
        varSymbolList.forEach(varSymbol -> {
            paramOperandList.add(varSymbol.operand);
            var tempOperand = allocTempOperand(varSymbol.operand.irType);
            currentBasicBlock.instructionList.add(new AllocaInst(tempOperand));
            currentBasicBlock.instructionList.add(new StoreInst(varSymbol.operand, tempOperand));
            varSymbol.operand = tempOperand;
        });
        this.returnIRType = returnIRType;
        this.ident = "@" + ident;
    }

    public TempOperand allocTempOperand(IRType irType) {
        return new TempOperand(labelManager.allocLabel(), irType);
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
