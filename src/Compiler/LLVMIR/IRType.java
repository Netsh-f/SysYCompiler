/*
@Time    : 2023/11/14 14:09
@Author  : Elaikona
*/
package Compiler.LLVMIR;

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
    public List<Integer> shape;

    public IRType(IRValueType irValueType) {
        this.irValueType = irValueType;
        this.shape = new ArrayList<>();
    }

    public IRType(IRValueType irValueType, List<Integer> shape) {
        this.irValueType = irValueType;
        this.shape = shape;
    }

    public String toString() {
        if (shape.isEmpty()) {
            return this.irValueType.toString();
        } else {
            return typeToString(this.irValueType, this.shape, 0);
        }
    }

    private String typeToString(IRType.IRValueType type, List<Integer> shape, int floor) {
        if (floor == shape.size()) {
            return type.toString();
        } else {
            return "[" + shape.get(floor) + " x " + typeToString(type, shape, floor + 1) + "]";
        }
    }
}
