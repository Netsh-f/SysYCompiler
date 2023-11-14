/*
@Time    : 2023/10/9 15:19
@Author  : Elaikona
*/
package Compiler.SymbolManager.Symbol;

import Compiler.LLVMIR.Operand.Operand;

import java.util.List;

public class VarSymbol {
    public ValueType valueType;
    public boolean isConst;
    public List<Integer> values;

    public Operand operand;

    public VarSymbol(ValueType valueType, boolean isConst, List<Integer> values) {
        this.valueType = valueType;
        this.isConst = isConst;
        this.values = values;
    }

    public int getValue(List<Integer> indexList) {
        int valuesIndex = 0;
        if (isConst && valueType.shape().size() == indexList.size()) {
            int tSize = 1;
            for (int i = indexList.size() - 1; i >= 0; i--) {
                valuesIndex += indexList.get(i) * tSize;
                tSize = tSize * valueType.shape().get(i);
            }
            return values.get(valuesIndex);
        }
        return 0; // 如果是个变量直接返回一个0
    }
}
