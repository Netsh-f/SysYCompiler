/*
@Time    : 2023/10/25 19:23
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public record FormatString(String content, List<Integer> indexList) {
}
