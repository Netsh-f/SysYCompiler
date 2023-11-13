/*
@Time    : 2023/11/13 16:34
@Author  : Elaikona
*/
package Compiler.LLVMIR;

import Compiler.LLVMIR.Global.GlobalDecl;

import java.util.ArrayList;
import java.util.List;

public class IRModule {
    public List<GlobalDecl> globalDeclList;

    public IRModule(){
        this.globalDeclList = new ArrayList<>();
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        globalDeclList.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
