/*
@Time    : 2023/11/13 17:15
@Author  : Elaikona
*/
package Compiler.LLVMIR;

public class Value {
    public enum IRValueType {
        I32,
        VOID;

        public String toString() {
            return switch (this) {
                case VOID -> "void";
                case I32 -> "i32";
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
