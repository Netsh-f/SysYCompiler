/*
@Time    : 2023/11/13 17:26
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.BasicBlock;
import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Operand.TempOperand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Function extends GlobalDecl {

    public List<BasicBlock> basicBlockList;
    public String ident;
    public IRType.IRValueType returnType;
    private LabelManager labelManager;

    public Function(IRType.IRValueType type, String ident) {
        this.labelManager = new LabelManager();
        this.basicBlockList = new ArrayList<>();
        basicBlockList.add(new BasicBlock(labelManager.allocLabel()));
        this.returnType = type;
        this.ident = ident;
    }

    public TempOperand allocTempOperand(IRType irType) {
        return new TempOperand(labelManager.allocLabel(), irType);
    }


    public TempOperand allocTempOperand(IRType.IRValueType type) {
        return new TempOperand(labelManager.allocLabel(), type);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("define dso_local ").append(this.returnType).append(" @").
                append(this.ident).append("() #0 {\n");
        // TODO: 参数输出
        basicBlockList.forEach(stringBuilder::append);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }
}
