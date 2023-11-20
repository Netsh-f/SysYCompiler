/*
@Time    : 2023/11/13 23:10
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Operand.GlobalOperand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalConst extends GlobalDecl {
    public GlobalOperand operand;
    public List<Integer> values;
    public boolean isConst;

    public GlobalConst(GlobalOperand operand, List<Integer> values, boolean isConst) {
        this.operand = operand;
        this.values = values;
        this.isConst = isConst;
    }

    public String toString() {
        //@y = dso_local constant [2 x [2 x i32]] [[2 x i32] [i32 1, i32 2], [2 x i32] [i32 3, i32 4]], align 16
        //@a = dso_local constant [2 x i32] [i32 1, i32 2], align 4
        //@x = dso_local constant i32 10, align 4
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.operand).append(" = dso_local ");
        if (isConst) {
            stringBuilder.append("constant ");
        } else {
            stringBuilder.append("global ");
        }

        stringBuilder.append(valueToString(this.operand.irType, this.values, new AtomicInteger(0))).append("\n");

        return stringBuilder.toString();
    }

    private String valueToString(IRType irType, List<Integer> values, AtomicInteger index) {
        if (irType.shape.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(irType.toStringWithoutPtr()).append(" ");
            if (index.get() < values.size()) {
                stringBuilder.append(values.get(index.getAndIncrement()));
            } else {
                stringBuilder.append("0"); // 全局非常量，没有initVal，则赋值0
            }
            return stringBuilder.toString();
        } else {
            var newShape = new ArrayList<>(irType.shape);
            newShape.remove(0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(irType.toStringWithoutPtr()).append(" ");
            if (index.get() >= values.size()) {
                stringBuilder.append("zeroinitializer"); // 全局非常量，initVal不完整的话，则赋值0，使用zeroinitializer
                // 但事实上，在语法分析的时候，如果数组初始值长度和定义的长度不一样，不会返回正确的values，且这种情况在sysy文法中不合法，故不再处理
            } else {
                stringBuilder.append("[");
                for (int i = 0; i < irType.shape.get(0); i++) {
                    stringBuilder.append(valueToString(new IRType(irType.irValueType, false, newShape), values, index));
                    if (i < irType.shape.get(0) - 1) {
                        stringBuilder.append(", ");
                    }
                }
                stringBuilder.append("]");
            }
            return stringBuilder.toString();
        }
    }
}
