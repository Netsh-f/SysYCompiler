/*
@Time    : 2023/9/29 20:11
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.IntermediateCode.Code.BaseCode;

public class Exp extends BaseCode {
    public AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }
}
