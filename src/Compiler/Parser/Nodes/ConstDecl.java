/*
@Time    : 2023/9/29 20:10
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public record ConstDecl(BType bType, List<ConstDef> constDefList) {
}
