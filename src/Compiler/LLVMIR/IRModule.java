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

    public IRModule() {
        this.globalDeclList = new ArrayList<>();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("declare i32 @getint()\n" +
                "declare void @putint(i32)\n" +
                "declare void @putch(i32)\n" +
                "declare void @putstr(i8*)\n\n");
        globalDeclList.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
