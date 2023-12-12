/*
@Time    : 2023/12/12 0:31
@Author  : Elaikona
*/
package Compiler.MIPS.regs;

import java.util.HashMap;
import java.util.Map;

public class RegManager {
    public static Map<String, Reg> regMap;
    private static int tRegNum;

    static {
        regMap = initRegMap();
        tRegNum = 0;
    }

    private RegManager() {
    }

    public static Reg allocTReg() {
        tRegNum = (tRegNum + 1) % 16;
        if (tRegNum < 8) {
            return regMap.get("t" + tRegNum);
        } else {
            return regMap.get("s" + (tRegNum - 8));
        }
    }

    private static Map<String, Reg> initRegMap() {
        var map = new HashMap<String, Reg>();
        map.put("zero", new Reg("zero", 0));
        map.put("at", new Reg("at", 1));
        map.put("v0", new Reg("v0", 2));
        map.put("v1", new Reg("v1", 3));
        map.put("a0", new Reg("a0", 4));
        map.put("a1", new Reg("a1", 5));
        map.put("a2", new Reg("a2", 6));
        map.put("a3", new Reg("a3", 7));
        map.put("t0", new Reg("t0", 8));
        map.put("t1", new Reg("t1", 9));
        map.put("t2", new Reg("t2", 10));
        map.put("t3", new Reg("t3", 11));
        map.put("t4", new Reg("t4", 12));
        map.put("t5", new Reg("t5", 13));
        map.put("t6", new Reg("t6", 14));
        map.put("t7", new Reg("t7", 15));
        map.put("s0", new Reg("s0", 16));
        map.put("s1", new Reg("s1", 17));
        map.put("s2", new Reg("s2", 18));
        map.put("s3", new Reg("s3", 19));
        map.put("s4", new Reg("s4", 20));
        map.put("s5", new Reg("s5", 21));
        map.put("s6", new Reg("s6", 22));
        map.put("s7", new Reg("s7", 23));
        map.put("t8", new Reg("t8", 24));
        map.put("t9", new Reg("t9", 25));
        map.put("k0", new Reg("k0", 26));
        map.put("k1", new Reg("k1", 27));
        map.put("gp", new Reg("gp", 28));
        map.put("sp", new Reg("sp", 29));
        map.put("fp", new Reg("fp", 30));
        map.put("ra", new Reg("ra", 31));
        map.put("pc", new Reg("pc", -1));
        map.put("hi", new Reg("hi", -1));
        map.put("lo", new Reg("lo", -1));
        return map;
    }
}
