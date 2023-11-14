package Compiler.LLVMIR;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Global.GlobalConst;
import Compiler.LLVMIR.Instructions.Instruction;
import Compiler.LLVMIR.Operand.TempOperand;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;

import java.util.List;

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

    public TempOperand allocTempOperand(Value.IRValueType type) {
        return currentFunction.allocTempOperand(type);
    }

    public void addGlobalVar(String ident, List<Integer> shape, List<Integer> values) {
        this.module.globalDeclList.add(new GlobalConst(ident, shape, Value.IRValueType.I32, values, false));
    }

    public void addGlobalConst(String ident, List<Integer> shape, List<Integer> values) {
        this.module.globalDeclList.add(new GlobalConst(ident, shape, Value.IRValueType.I32, values, true));
    }

    public void addInstruction(Instruction instruction) {
        this.currentBasicBlock.instructionList.add(instruction);
    }

    public void addFunctionDecl(ValueTypeEnum type, String ident) {
        var functionType = switch (type) {
            case VOID -> Value.IRValueType.VOID;
            case INT -> Value.IRValueType.I32;
        };
        this.currentFunction = new Function(functionType, ident);
        this.currentBasicBlock = this.currentFunction.basicBlockList.get(0);
        module.globalDeclList.add(this.currentFunction);
    }

    public IRModule getModule() {
        return this.module;
    }
}
