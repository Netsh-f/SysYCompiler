/*
@Time    : 2023/11/12 16:43
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import Compiler.IntermediateCode.Element.BaseElement;

public class BaseNode {
    private BaseElement element;

    public void setElement(BaseElement element) {
        this.element = element;
    }

    public BaseElement getElement() {
        return element;
    }
}
