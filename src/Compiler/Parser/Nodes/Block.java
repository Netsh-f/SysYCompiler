/*
@Time    : 2023/9/29 20:10
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.Lexer.Token;

import java.util.List;

public record Block(List<BlockItem> blockItemList, Token rBraceToken) {
}
