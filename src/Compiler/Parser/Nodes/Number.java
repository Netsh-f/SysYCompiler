/*
@Time    : 2023/9/29 20:15
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

public class Number extends BaseNode {
    public int intConst;

    public Number(int intConst) {
        this.intConst = intConst;
    }
}
