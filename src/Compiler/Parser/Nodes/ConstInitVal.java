/*
@Time    : 2023/9/29 20:11
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public record ConstInitVal(ConstExp constExp, List<ConstInitVal> constInitValList) {
}
