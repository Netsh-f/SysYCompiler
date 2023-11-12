/*
@Time    : 2023/11/12 16:27
@Author  : Elaikona
*/
package Compiler.IntermediateCode.Code;

public class LabelCode extends BaseCode {
    public String ident;

    public LabelCode(String ident) {
        this.ident = ident;
    }
}
