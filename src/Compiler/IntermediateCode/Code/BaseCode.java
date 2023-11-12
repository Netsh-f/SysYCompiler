/*
@Time    : 2023/11/11 21:59
@Author  : Elaikona
*/
package Compiler.IntermediateCode.Code;

import Compiler.IntermediateCode.Element.BaseElement;

public class BaseCode {
    public BaseElement element1;
    public BaseElement element2;
    public BaseElement result;

    public BaseCode(BaseElement element1, BaseElement element2, BaseElement result) {
        this.element1 = element1;
        this.element2 = element2;
        this.result = result;
    }

    public BaseCode(BaseElement element1, BaseElement result) {
        this.element1 = element1;
        this.result = result;
    }

    public BaseCode() {
    }
}
