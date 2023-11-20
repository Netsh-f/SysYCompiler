package Compiler.LLVMIR;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Global.GlobalConst;
import Compiler.LLVMIR.Global.GlobalStr;
import Compiler.LLVMIR.Global.LabelManager;
import Compiler.LLVMIR.Instructions.*;
import Compiler.LLVMIR.Instructions.Quadruple.AddInst;
import Compiler.LLVMIR.Operand.GlobalOperand;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;
import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IRManager {
    private final IRModule module;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;
    private LabelManager strLabelManager;

    public IRManager() {
        this.module = new IRModule();
        this.strLabelManager = new LabelManager();
    }

    public boolean isInGlobal() {
        return currentFunction == null;
    }

    public TempOperand allocTempOperand(IRType irType) {
        if (currentFunction == null) {
            return null;
        }
        return currentFunction.allocTempOperand(irType);
    }

    public Operand addGlobalVar(String ident, List<Integer> shape, List<Integer> values) {
        var operand = new GlobalOperand(ident, new IRType(IRType.IRValueType.I32, true, shape));
        this.module.globalDeclList.add(new GlobalConst(operand, values, false));
        return operand;
    }

    public Operand addGlobalConst(String ident, List<Integer> shape, List<Integer> values) {
        var operand = new GlobalOperand(ident, new IRType(IRType.IRValueType.I32, true, shape));
        this.module.globalDeclList.add(new GlobalConst(operand, values, true));
        return operand;
    }

    public void addInstruction(Instruction instruction) {
        if (currentBasicBlock == null) {
            return;
        }
        this.currentBasicBlock.instructionList.add(instruction);
    }

    public Operand addAllocaInst(IRType.IRValueType type, List<Integer> shape) {
        var tempOperand = allocTempOperand(new IRType(type, true, shape));
        var allocaInst = new AllocaInst(tempOperand);
        addInstruction(allocaInst);
        return tempOperand;
    }

    public Operand addLoadInst(Operand pointerOperand) {
        var tempOperand = allocTempOperand(new IRType(pointerOperand.irType.irValueType, false, pointerOperand.irType.shape));
        var loadInst = new LoadInst(tempOperand, pointerOperand);
        addInstruction(loadInst);
        return tempOperand;
    }

    public Operand addGetElementPtrInst(List<Integer> newShape, Operand ptrOperand, List<Integer> indexList) {
        var tempOperand = allocTempOperand(new IRType(ptrOperand.irType.irValueType, true, newShape));
        var getElementPtrInst = new GetElementPtrInst(tempOperand, ptrOperand, indexList);
        addInstruction(getElementPtrInst);
        return tempOperand;
    }

    public void addRetInst(Operand operand) {
        addInstruction(new RetInst(Objects.requireNonNullElseGet(operand, () -> new Operand(new IRType(IRType.IRValueType.VOID, false)))));
    }

    public Operand addCallGetIntInst() {
        var tempOperand = allocTempOperand(new IRType(IRType.IRValueType.I32, false));
        addInstruction(new CallGetIntInst(tempOperand));
        return tempOperand;
    }

    public void addStoreInst(Operand valueOperand, Operand pointerOperand) {
        addInstruction(new StoreInst(valueOperand, pointerOperand));
    }

    private GlobalOperand addGlobalStr(String string) {
        var newString = string.replaceAll("\\\\n", "\\\\0A");
        var shape = new ArrayList<Integer>();
        shape.add(2 * string.length() - newString.length());
        var operand = new GlobalOperand("str" + strLabelManager.allocLabel(), new IRType(IRType.IRValueType.I8, true, shape));
        module.globalDeclList.add(0, new GlobalStr(operand, newString));
        return operand;
    }

    public void addCallPutInst(String formatString, List<Integer> indexList, List<Operand> expOperandList) {
        // indexList: the index of '%' for "%d"
        int beginIndex = 0;
        for (int i = 0; i < indexList.size(); i++) {
            if (beginIndex < indexList.get(i)) {
                var operand = addGlobalStr(formatString.substring(beginIndex, indexList.get(i))); // 到%的前一个
                var tempOperand = addGetElementPtrInst(new ArrayList<>(), operand, Arrays.asList(0, 0));
                addInstruction(new CallPutStrInst(tempOperand));
            }
            addInstruction(new CallPutIntInst(expOperandList.get(i)));
            beginIndex = indexList.get(i) + 2; // beginIndex = %d的后一个
        }
        if (beginIndex < formatString.length()) { // 如果还有剩的
            var operand = addGlobalStr(formatString.substring(beginIndex));
            var tempOperand = addGetElementPtrInst(new ArrayList<>(), operand, Arrays.asList(0, 0));
            addInstruction(new CallPutStrInst(tempOperand));
        }
    }

    public void addFunctionDecl(ValueTypeEnum type, String ident, List<VarSymbol> varSymbolList, FuncSymbol funcSymbol) {
        var functionType = switch (type) {
            case VOID -> IRType.IRValueType.VOID;
            case INT -> IRType.IRValueType.I32;
        };
        this.currentFunction = new Function(new IRType(functionType, false), ident, varSymbolList);
        this.currentBasicBlock = this.currentFunction.basicBlockList.get(0);
        module.globalDeclList.add(this.currentFunction);
        funcSymbol.function = this.currentFunction;
    }


    public IRModule getModule() {
        return this.module;
    }
}
