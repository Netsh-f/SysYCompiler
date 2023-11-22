package Compiler.LLVMIR;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Global.GlobalConst;
import Compiler.LLVMIR.Global.GlobalStr;
import Compiler.LLVMIR.Global.LabelManager;
import Compiler.LLVMIR.Instructions.*;
import Compiler.LLVMIR.Instructions.Quadruple.*;
import Compiler.LLVMIR.Operand.ConstantOperand;
import Compiler.LLVMIR.Operand.GlobalOperand;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;
import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IRManager {
    private final IRModule module;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;
    private final LabelManager strLabelManager;

    public IRManager() {
        this.module = new IRModule();
        this.strLabelManager = new LabelManager();
    }

    public void finalizeProcessing() {
        this.module.globalDeclList.forEach(globalDecl -> {
            if (globalDecl instanceof Function function) {
                function.finalizeProcessing();
            }
        });
    }

    public void addBrInst(BasicBlock basicBlock) {
        addInstruction(new BrInst(basicBlock));
    }

    public void addBrInst(Operand condOperand, BasicBlock trueBasicBlock, BasicBlock falseBasicBlock) {
        addInstruction(new BrInst(condOperand, trueBasicBlock, falseBasicBlock));
    }


    public void setCurrentBasicBlock(BasicBlock basicBlock) {
        currentFunction.addBasicBlock(basicBlock);
        currentBasicBlock = basicBlock;
    }

    public boolean isInGlobal() {
        return currentFunction == null;
    }

    public TempOperand addIcmpInst(IcmpInst.IcmpCond cond, Operand operand1, Operand operand2) {
        // 如果op1与op2类型不同，那么将op1的类型转为op2的类型
        if (operand1.irType.irValueType != operand2.irType.irValueType) { // 不管数组了，理论上不会出现，应该只可能出现i1转i32
            if (operand1.irType.irValueType == IRType.IRValueType.I1) {
                operand1 = addZextInst(operand2.irType, operand1);
            } else if (operand2.irType.irValueType == IRType.IRValueType.I1) {
                operand2 = addZextInst(operand1.irType, operand2);
            }
        }
        var tempOperand = allocTempOperand(new IRType(IRType.IRValueType.I1, false));
        addInstruction(new IcmpInst(tempOperand, cond, operand1, operand2));
        return tempOperand;
    }

    public TempOperand addZextInst(IRType resultIRType, Operand valueOperand) {
        var tempOperand = allocTempOperand(resultIRType);
        addInstruction(new ZextInst(tempOperand, valueOperand));
        return tempOperand;
    }

    public TempOperand addAddInst(Operand operand1, Operand operand2) {
        var tempOperand = allocTempOperand(new IRType(operand1.irType.irValueType, false));
        addInstruction(new AddInst(tempOperand, tempOperand.irType.irValueType, operand1, operand2));
        return tempOperand;
    }

    public TempOperand addSubInst(Operand operand1, Operand operand2) {
        var tempOperand = allocTempOperand(new IRType(operand1.irType.irValueType, false));
        addInstruction(new SubInst(tempOperand, tempOperand.irType.irValueType, operand1, operand2));
        return tempOperand;
    }

    public TempOperand addMulInst(Operand operand1, Operand operand2) {
        var tempOperand = allocTempOperand(new IRType(operand1.irType.irValueType, false));
        addInstruction(new MulInst(tempOperand, tempOperand.irType.irValueType, operand1, operand2));
        return tempOperand;
    }

    public void addVoidCallInst(Function function, List<Operand> paramOperandList) {
        addInstruction(new CallInst(function, paramOperandList));
    }

    public TempOperand addCallInst(Function function, List<Operand> paramOperandList) {
        var tempOperand = allocTempOperand(function.returnIRType);
        addInstruction(new CallInst(tempOperand, function, paramOperandList));
        return tempOperand;
    }

    public TempOperand addSdivInst(Operand operand1, Operand operand2) {
        var tempOperand = allocTempOperand(new IRType(operand1.irType.irValueType, false));
        addInstruction(new SdivInst(tempOperand, tempOperand.irType.irValueType, operand1, operand2));
        return tempOperand;
    }

    public TempOperand addSremInst(Operand operand1, Operand operand2) {
        var tempOperand = allocTempOperand(new IRType(operand1.irType.irValueType, false));
        addInstruction(new SremInst(tempOperand, tempOperand.irType.irValueType, operand1, operand2));
        return tempOperand;
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

    public Operand addGetElementPtrInst(List<Integer> newShape, Operand ptrOperand, List<Operand> indexList) {
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
                var indexOperandList = new ArrayList<Operand>();
                indexOperandList.add(new ConstantOperand(0));
                indexOperandList.add(new ConstantOperand(0));
                var tempOperand = addGetElementPtrInst(new ArrayList<>(), operand, indexOperandList);
                addInstruction(new CallPutStrInst(tempOperand));
            }
            addInstruction(new CallPutIntInst(expOperandList.get(i)));
            beginIndex = indexList.get(i) + 2; // beginIndex = %d的后一个
        }
        if (beginIndex < formatString.length()) { // 如果还有剩的
            var operand = addGlobalStr(formatString.substring(beginIndex));
            var indexOperandList = new ArrayList<Operand>();
            indexOperandList.add(new ConstantOperand(0));
            indexOperandList.add(new ConstantOperand(0));
            var tempOperand = addGetElementPtrInst(new ArrayList<>(), operand, indexOperandList);
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
