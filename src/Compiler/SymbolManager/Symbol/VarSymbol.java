/*
@Time    : 2023/10/9 15:19
@Author  : Elaikona
*/
package Compiler.SymbolManager.Symbol;

import java.util.List;

public record VarSymbol(ValueType valueType, boolean isConst, List<Integer> values) {
    public int getValue(List<Integer> indexList) {
        int valuesIndex = 0;
        if (isConst && valueType.shape().size() == indexList.size()) {
            int tSize = 1;
            for (int i = indexList.size() - 1; i >= 0; i--) {
                tSize = tSize * valueType.shape().get(i);
                valuesIndex += indexList.get(i) * tSize;
            }
            return values.get(valuesIndex);
        }
        return 0; // 如果是个变量直接返回一个0
    }
}
