/*
@Time    : 2023/11/11 22:02
@Author  : Elaikona
*/
package Compiler.IntermediateCode.Element;

public class ConstElement extends BaseElement {
    public int constNumber;

    public ConstElement(int constNumber) {
        this.constNumber = constNumber;
    }
}
