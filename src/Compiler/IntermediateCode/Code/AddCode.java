/*
@Time    : 2023/11/12 16:25
@Author  : Elaikona
*/
package Compiler.IntermediateCode.Code;

import Compiler.IntermediateCode.Element.BaseElement;

public class AddCode extends BaseCode{
    public AddCode(BaseElement element1, BaseElement element2, BaseElement result) {
        super(element1, element2, result);
    }
}
