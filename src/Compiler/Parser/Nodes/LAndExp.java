/*
@Time    : 2023/9/29 20:14
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public record LAndExp(List<EqExp> eqExpList) {
}
