/*
@Time    : 2023/11/13 17:26
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.BasicBlock;

import java.util.List;

public class Function extends GlobalDecl {
    public enum FunctionType {
        I32,
        VOID;

        public String toString() {
            return switch (this) {
                case VOID -> "void";
                case I32 -> "i32";
            };
        }
    }

    private List<BasicBlock> basicBlockList;
    private FunctionType functionType;
    private String ident;


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("define dso_local ").append(this.functionType).append(" @").append(this.ident);
        // TODO: 参数和基本块
        return stringBuilder.toString();
    }
}
