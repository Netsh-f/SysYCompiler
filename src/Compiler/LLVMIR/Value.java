/*
@Time    : 2023/11/13 17:15
@Author  : Elaikona
*/
package Compiler.LLVMIR;

public class Value {
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

    public IRValueType type;

    public Value(){
        this.type = IRValueType.VOID;
    }

    public Value(IRValueType type){
        this.type = type;
    }
}
