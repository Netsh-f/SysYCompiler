/*
@Time    : 2023/9/29 20:11
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.Lexer.LexType;

import java.util.List;

public record EqExp(List<RelExp> relExpList, List<LexType> opLexTypeList) {
}
