/*
@Time    : 2023/12/11 15:49
@Author  : Elaikona
*/
package Compiler.MIPS.data;

import java.util.List;
import java.util.ListIterator;

public class WordDataLabel extends DataLabel {
    public String label;
    public List<Integer> values;

    public WordDataLabel(String label, List<Integer> values) {
        this.label = label;
        this.values = values;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(label).append(": .word ");

//        ListIterator<Integer> listIterator = values.listIterator(values.size()); // 似乎不用倒序
//        while (listIterator.hasPrevious()) {
//            Integer value = listIterator.previous();
//            stringBuilder.append(value);
//            if (listIterator.hasPrevious()) {
//                stringBuilder.append(",");
//            }
//        }
        ListIterator<Integer> listIterator = values.listIterator();
        while (listIterator.hasNext()) {
            Integer value = listIterator.next();
            stringBuilder.append(value);

            if (listIterator.hasNext()) {
                stringBuilder.append(",");
            }
        }

        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
