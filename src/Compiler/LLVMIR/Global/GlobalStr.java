/*
@Time    : 2023/11/20 22:44
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import Compiler.LLVMIR.Operand.GlobalOperand;
import Compiler.LLVMIR.Operand.Operand;

import java.util.ArrayList;

public class GlobalStr extends GlobalDecl {
    public GlobalOperand operand;
    public String content;

    public GlobalStr(GlobalOperand operand, String content) { // 要在最后加一个 '\00'
        var newShape = new ArrayList<Integer>();
        newShape.add(operand.irType.shape.get(0) + 1);
        operand.irType.shape = newShape;
        this.operand = operand;
        this.content = content;
    }

    public String toString() {
        return operand + " = dso_local global " + operand.irType.toStringWithoutPtr() + " c\"" + content + "\00\"\n"; // 这里的类型实际上是后面值的类型，使用了去*的操作数类型进行代替
    }
}
