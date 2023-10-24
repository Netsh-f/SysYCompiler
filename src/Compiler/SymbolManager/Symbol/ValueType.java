/*
@Time    : 2023/10/24 18:52
@Author  : Elaikona
*/
package Compiler.SymbolManager.Symbol;

import java.util.List;

public record ValueType(ValueTypeEnum type, List<Integer> shape) {
//    shape:
//    {1, 2} 是一个二维数组，长度为1和2
//    {-1, 2} 指的是 int a[][2] 这样的函数形参
//    shape.length就是维度
//    其值一般不为0
}
