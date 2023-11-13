/*
@Time    : 2023/11/13 23:10
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.Value;

import java.util.List;

public class GlobalConst extends GlobalDecl {
    public String ident;
    public List<Integer> shape;
    public IRValueType valueType;
    public List<Integer> values;

    public GlobalConst(String ident, List<Integer> shape, IRValueType valueType, List<Integer> values) {
        this.ident = ident;
        this.shape = shape;
        this.valueType = valueType;
        this.values = values;
    }

    public String toString() {
        //@y = dso_local constant [2 x [2 x i32]] [[2 x i32] [i32 1, i32 2], [2 x i32] [i32 3, i32 4]], align 16
        //@a = dso_local constant [2 x i32] [i32 1, i32 2], align 4
        //@x = dso_local constant i32 10, align 4
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@").append(this.ident).append(" = dso_local constant ");

        this.shape.forEach(len -> {
            stringBuilder.append("[").append(len).append(" x ");
        });
        if (!shape.isEmpty()) {
            stringBuilder.append(this.valueType);
        }
        for (Integer len : this.shape) {
            stringBuilder.append("]");
        }
        stringBuilder.append(" "); //

        for (int i = 0; i < shape.size(); i++) {
            stringBuilder.append("[");
            // TODO
            for (int j = 0; j < shape.get(i); j++) {

            }
        }

        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
