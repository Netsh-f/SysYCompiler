package Compiler.LLVMIR.Global;

public class LabelManager {
    private int count;

    public LabelManager() {
        this.count = 0;
    }

    public int allocLabel() {
        return this.count++;
    }
}
