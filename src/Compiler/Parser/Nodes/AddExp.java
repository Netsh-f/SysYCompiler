/*
@Time    : 2023/9/29 20:09
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.Lexer.LexType;

import java.util.List;

public record AddExp(List<MulExp> mulExpList, List<LexType> opLexTypeList) {
}
