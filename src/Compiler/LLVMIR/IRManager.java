package Compiler.LLVMIR;

import Compiler.LLVMIR.Global.Function;
import Compiler.LLVMIR.Instructions.Instruction;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;

public class IRManager {
    private IRModule module;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;

    public IRManager() {
        this.module = new IRModule();
    }

    public void addInstruction(Instruction instruction){
        this.currentBasicBlock.instructionList.add(instruction);
    }

    public void addFunctionDecl(ValueTypeEnum type, String ident) {
        var functionType = switch (type){
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
