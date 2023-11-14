/*
@Time    : 2023/10/24 18:52
@Author  : Elaikona
*/
package Compiler.SymbolManager.Symbol;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Value;

import java.util.List;

public record ValueType(ValueTypeEnum type, List<Integer> shape) {
    //    shape:
//    {1, 2} 是一个二维数组，长度为1和2
//    {-1, 2} 指的是 int a[][2] 这样的函数形参
//    shape.length就是维度
//    其值一般不为0
    public boolean isFParamToRParamValid(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ValueType valueType = (ValueType) obj;
        if (this.type.equals(valueType.type) && this.shape.size() == valueType.shape.size()) {
            for (int i = 0; i < this.shape.size(); i++) {
                if (this.shape.get(i) == -1) { // [][3] 和 [2][3] 认为是同类型，而 [2][3] 和 [][3] 不是
                    continue;
                }
                if (!this.shape.get(i).equals(valueType.shape.get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
