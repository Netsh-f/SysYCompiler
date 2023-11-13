/*
@Time    : 2023/11/13 17:26
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.BasicBlock;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Function extends GlobalDecl {

    public List<BasicBlock> basicBlockList;
    public String ident;
    private LabelManager labelManager;

    public Function(IRValueType type, String ident) {
        this.labelManager = new LabelManager();
        this.basicBlockList = new ArrayList<>();
        basicBlockList.add(new BasicBlock(labelManager.allocLabel()));
        this.type = type;
        this.ident = ident;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("define dso_local ").append(this.type).append(" @").
                append(this.ident).append("() #0 {\n");
        // TODO: 参数输出
        basicBlockList.forEach(stringBuilder::append);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }
}
