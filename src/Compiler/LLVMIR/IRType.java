/*
@Time    : 2023/11/14 14:09
@Author  : Elaikona
*/
package Compiler.LLVMIR;

import Compiler.SymbolManager.Symbol.ValueType;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class IRType {
    public enum IRValueType {
        I32,
        VOID,
        I1;

        public String toString() {
            return switch (this) {
                case VOID -> "void";
                case I32 -> "i32";
                case I1 -> "i1";
            };
        }
    }

    public IRValueType irValueType;
    public boolean isPtr;
    public List<Integer> shape;


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
        this.isPtr = !valueType.shape().isEmpty();
        var newShape = new ArrayList<Integer>(valueType.shape());
        if (!valueType.shape().isEmpty()) {
            newShape.remove(0);
        }
        this.shape = newShape;
    }
}
