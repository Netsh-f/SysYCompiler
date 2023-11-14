/*
@Time    : 2023/11/13 23:10
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalConst extends GlobalDecl {
    public String ident;
    public List<Integer> shape;
    public IRValueType valueType;
    public List<Integer> values;
    public boolean isConst;

    public GlobalConst(String ident, List<Integer> shape, IRValueType valueType, List<Integer> values, boolean isConst) {
        this.ident = ident;
        this.shape = shape;
        this.valueType = valueType;
        this.values = values;
        this.isConst = isConst;
    }

    public String toString() {
        //@y = dso_local constant [2 x [2 x i32]] [[2 x i32] [i32 1, i32 2], [2 x i32] [i32 3, i32 4]], align 16
        //@a = dso_local constant [2 x i32] [i32 1, i32 2], align 4
        //@x = dso_local constant i32 10, align 4
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@").append(this.ident).append(" = dso_local ");
        if(isConst){
            stringBuilder.append("constant ");
        }else{
            stringBuilder.append("global ");
        }

        stringBuilder.append(valueToString(this.valueType, this.shape, this.values, new AtomicInteger(0))).append("\n");

        return stringBuilder.toString();
    }

    private String typeToString(IRValueType type, List<Integer> shape, int floor) {
        if (floor == shape.size()) {
            return type.toString();
        } else {
            return "[" + shape.get(floor) + " x " + typeToString(type, shape, floor + 1) + "]";
        }
    }

    private String valueToString(IRValueType type, List<Integer> shape, List<Integer> values, AtomicInteger index) {
        if (shape.isEmpty()) {
            return typeToString(type, shape, 0) + " " + values.get(index.getAndIncrement());
        } else {
            var newShape = new ArrayList<>(shape);
            newShape.remove(0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(typeToString(type, shape, 0)).append(" [");
            for (int i = 0; i < shape.get(0); i++) {
                stringBuilder.append(valueToString(type, newShape, values, index));
                if (i < shape.get(0) - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }
}
