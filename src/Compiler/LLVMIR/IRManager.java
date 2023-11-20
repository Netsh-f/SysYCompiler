package Compiler.LLVMIR;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Global.GlobalConst;
import Compiler.LLVMIR.Instructions.*;
import Compiler.LLVMIR.Instructions.Quadruple.AddInst;
import Compiler.LLVMIR.Operand.GlobalOperand;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;
import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.util.List;
import java.util.Objects;

public class IRManager {
    private final IRModule module;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;

    public IRManager() {
        this.module = new IRModule();
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


//    public TempOperand allocTempOperand(IRType.IRValueType type) {
//        return currentFunction.allocTempOperand(type);
//    }

    public Operand addGlobalVar(String ident, List<Integer> shape, List<Integer> values) {
        this.module.globalDeclList.add(new GlobalConst(ident, shape, IRType.IRValueType.I32, values, false));
        return new GlobalOperand(ident, new IRType(IRType.IRValueType.I32, false, shape));
    }

    public void addGlobalConst(String ident, List<Integer> shape, List<Integer> values) {
        this.module.globalDeclList.add(new GlobalConst(ident, shape, IRType.IRValueType.I32, values, true));
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
