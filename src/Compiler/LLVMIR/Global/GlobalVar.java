/*
@Time    : 2023/11/13 17:26
@Author  : Elaikona
*/
package Compiler.LLVMIR.Global;

import java.util.List;

public class GlobalVar extends GlobalDecl{
    public String ident;
    public List<Integer> shape;
    public List<Integer> values;

    public GlobalVar(String ident, List<Integer> shape, List<Integer> values) {
        this.ident = ident;
        this.shape = shape;
        this.values = values;
    }
}
