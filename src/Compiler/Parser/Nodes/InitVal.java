/*
@Time    : 2023/9/29 20:14
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public record InitVal(Exp exp, List<InitVal> initValList) {
}
