package Compiler.SymbolManager.Symbol;

import Compiler.LLVMIR.IRType;

public enum ValueTypeEnum {
    INT,
    VOID;

    public IRType.IRValueType toIRValueType() {
        return switch (this) {
            case INT -> IRType.IRValueType.I32;
            case VOID -> IRType.IRValueType.VOID;
        };
    }
}
