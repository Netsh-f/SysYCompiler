/*
@Time    : 2023/11/12 15:25
@Author  : Elaikona
*/
package Compiler.IntermediateCode;

import Compiler.IntermediateCode.Code.BaseCode;

import java.util.ArrayList;
import java.util.List;

public class IMCContainer {
    public List<BaseCode> intermediateCodeList = new ArrayList<>();

    public void addCode(BaseCode baseCode) {
        intermediateCodeList.add(baseCode);
    }
}
