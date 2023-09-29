/*
@Time    : 2023/9/29 20:15
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Enums.LexType;

import java.util.List;

public record MulExp(List<UnaryExp> unaryExpList, List<LexType> opLexTypeList) {
}
