/*
@Time    : 2023/11/20 22:44
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.Operand.Operand;

public class GlobalStr extends GlobalDecl {
    public Operand operand;
    public String content;

    public GlobalStr(Operand operand, String content) {
        this.operand = operand;
        this.content = content;
    }

    public String toString() {
        return operand + " = dso_local global " + operand.irType.toStringWithoutPtr() + " c\"" + content + "\"\n"; // 这里的类型实际上是后面值的类型，使用了去*的操作数类型进行代替
    }
}
