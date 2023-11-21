/*
@Time    : 2023/11/21 14:23
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

public class IcmpInst extends Instruction {
    // <result> = icmp <cond> <ty> <op1>, <op2>
//    eq: equal
//    ne: not equal
//    sgt: signed greater than
//    sge: signed greater or equal
//    slt: signed less than
//    sle: signed less or equal
    public enum IcmpCond {
        EQ,
        NE,
        SLT,
        SLE,
        SGT,
        SGE;

        public String toString() {
            return switch (this) {
                case EQ -> "eq";
                case NE -> "ne";
                case SLT -> "slt";
                case SLE -> "sle";
                case SGT -> "sgt";
                case SGE -> "sge";
            };
        }
    }

    public Operand resultOperand;
    public IcmpCond cond;
    public Operand operand1;
    public Operand operand2;

    public IcmpInst(Operand resultOperand, IcmpCond cond, Operand operand1, Operand operand2) {
        this.resultOperand = resultOperand;
        this.cond = cond;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public String toString() {
        return resultOperand + " = icmp " + cond + " " + operand1.irType + " " + operand1 + ", " + operand2 + "\n";
    }
}
