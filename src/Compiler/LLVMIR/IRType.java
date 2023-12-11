/*
@Time    : 2023/11/14 14:09
@Author  : Elaikona
*/
package Compiler.LLVMIR;

import Compiler.SymbolManager.Symbol.ValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IRType {
    public enum IRValueType {
        I32,
        VOID,
        I1,
        I8;

        public String toString() {
            return switch (this) {
                case VOID -> "void";
                case I32 -> "i32";
                case I1 -> "i1";
                case I8 -> "i8";
            };
        }
    }

    public IRValueType irValueType;
    public boolean isPtr;
    public List<Integer> shape;

    public int getLength() {
        AtomicInteger size = new AtomicInteger(1);
        if (!shape.isEmpty()) {
            shape.forEach(l -> size.updateAndGet(v -> v * l));
        }
        return size.get();
    }


    public IRType(IRValueType irValueType, boolean isPtr) {
        this.irValueType = irValueType;
        this.shape = new ArrayList<>();
        this.isPtr = isPtr;
    }

    public IRType(IRValueType irValueType, boolean isPtr, List<Integer> shape) {
        this.irValueType = irValueType;
        this.shape = shape;
        this.isPtr = isPtr;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(typeToString(this.irValueType, this.shape, 0));
        if (isPtr) {
            stringBuilder.append("*");
        }
        return stringBuilder.toString();
    }

    public String toStringWithoutPtr() {
        return typeToString(this.irValueType, this.shape, 0);
    }

    private String typeToString(IRType.IRValueType type, List<Integer> shape, int floor) {
        if (floor == shape.size()) {
            return type.toString();
        } else {
            return "[" + shape.get(floor) + " x " + typeToString(type, shape, floor + 1) + "]";
        }
    }

    public IRType(ValueType valueType) {
        this.irValueType = switch (valueType.type()) {
            case INT -> IRValueType.I32;
            case VOID -> IRValueType.VOID;
        };
        this.isPtr = !valueType.shape().isEmpty(); // 如果形参没有中括号，那么就不是ptr
        var newShape = new ArrayList<Integer>(valueType.shape());
        if (!valueType.shape().isEmpty()) {
            newShape.remove(0);
        }
        this.shape = newShape;
    }
}
