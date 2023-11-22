/*
@Time    : 2023/10/9 15:18
@Author  : Elaikona
*/
package Compiler.Visitor;

import Compiler.LLVMIR.BasicBlock;
import Compiler.LLVMIR.IRManager;
import Compiler.LLVMIR.IRModule;
import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Instructions.*;
import Compiler.LLVMIR.Operand.ConstantOperand;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.Lexer.LexType;
import Compiler.Parser.Nodes.*;
import Compiler.Parser.Nodes.Number;
import Compiler.Parser.Nodes.StmtNode.*;
import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.ValueType;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;
import Compiler.SymbolManager.Symbol.VarSymbol;
import Compiler.SymbolManager.SymbolManager;
import Utils.Error.ErrorType;
import Utils.OutputHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Visitor {
    private final CompUnit unit;
    private final SymbolManager symbolManager;
    private ValueTypeEnum curFuncReturnType = ValueTypeEnum.VOID;

    // LLVM IR attr
    private final IRManager irManager;

    public Visitor(CompUnit compUnit) {
        this.unit = compUnit;
        this.symbolManager = new SymbolManager();
        this.irManager = new IRManager();
    }

    public IRModule run() {
        visit(this.unit);
        irManager.finalizeProcessing();
        return irManager.getModule();
    }

    private VisitResult visit(AddExp addExp) {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        // 改写为 AddExp -> MulExp { ('+' | '−') MulExp }

        if (addExp == null || addExp.mulExpList.isEmpty()) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }

        int value = 0;
        var result = visit(addExp.mulExpList.get(0));
        var valueType = result.valueType;
        if (result.isConst) {
            value = result.value;
        }
        boolean isConst = result.isConst;

        for (int i = 0; i < addExp.opLexTypeList.size(); i++) {
            result = visit(addExp.mulExpList.get(1 + i));
            if (isConst && result.isConst) {
                switch (addExp.opLexTypeList.get(i)) {
                    case PLUS -> value += result.value;
                    case MINU -> value -= result.value;
                    default -> throw new IllegalStateException("Unexpected value: " + addExp.opLexTypeList.get(i));
                }
            } else {
                isConst = false;
            }
        }

        addExp.operand = addExp.mulExpList.get(0).operand;
        if (!isConst) { // 常量优化
            for (int i = 0; i < addExp.opLexTypeList.size(); i++) {
                var mulExp = addExp.mulExpList.get(1 + i);
                addExp.operand = switch (addExp.opLexTypeList.get(i)) {
                    case PLUS -> irManager.addAddInst(addExp.operand, mulExp.operand);
                    case MINU -> irManager.addSubInst(addExp.operand, mulExp.operand);
                    default -> throw new IllegalStateException("Unexpected value: " + addExp.opLexTypeList.get(i));
                };
            }
        } else {
            addExp.operand = new ConstantOperand(value); // 如果能计算出来，那么直接开一个常量操作数，且只需要在addExp开就够了
        }

        return new VisitResult(valueType, isConst, value);
    }

    private void visit(BlockItem blockItem) {
        //  BlockItem → Decl | Stmt
        if (blockItem == null) {
            return;
        }
        if (blockItem.decl() != null) {
            visit(blockItem.decl());
        } else if (blockItem.stmt() != null) {
            visit(blockItem.stmt());
        }
        // error
    }

    private void visit(Block block, boolean checkReturn) { // 不在这里创建新的符号表是考虑到在进入函数定义的时候，将参数算入了函数的block当中，需要提前创建新的符号表
        // Block → '{' { BlockItem } '}'
        if (block == null) {
            return;
        }

        BlockItem lastBlockItem = null;
        for (int i = 0; i < block.blockItemList().size(); i++) {
            visit(block.blockItemList().get(i));
            if (i == block.blockItemList().size() - 1) {
                lastBlockItem = block.blockItemList().get(i);
            }
        }
        if (checkReturn && !(lastBlockItem != null && lastBlockItem.stmt() != null && lastBlockItem.stmt() instanceof StmtReturn)) {
            OutputHelper.addError(ErrorType.NO_RETURN, block.rBraceToken().lineNum(), "missing return stmt in non-void function");
        }
    }

    private ValueTypeEnum visit(BType bType) {
        if (bType == null) {
            return ValueTypeEnum.VOID;
        }
        return bType.valueTypeEnum();
    }


    private void visit(CompUnit compUnit) {
        //CompUnit → {Decl} {FuncDef} MainFuncDef
        if (compUnit == null) {
            return;
        }
        compUnit.declList().forEach(this::visit);
        compUnit.funcDefList().forEach(this::visit);
        visit(compUnit.mainFuncDef());
    }

    private void visit(Cond cond) {
        // Cond → LOrExp
        if (cond == null) {
            return;
        }
        visit(cond.lOrExp);
    }

    private void visit(ConstDecl constDecl) {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        if (constDecl == null) {
            return;
        }
        var valueTypeEnum = visit(constDecl.bType());
        for (var constDef : constDecl.constDefList()) {
            visit(constDef, valueTypeEnum);
        }
    }

    private void visit(ConstDef constDef, ValueTypeEnum valueTypeEnum) {
        // ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        if (constDef == null) {
            return;
        }

        var shape = new ArrayList<Integer>();
        var values = new ArrayList<Integer>();

        var identToken = constDef.ident().token();
        if (symbolManager.isVarSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "const redefinition of '" + identToken.content() + "'");
            return;
        }

        constDef.constExpList().forEach(constExp -> shape.add(visit(constExp).value));

        var varSymbol = new VarSymbol(new ValueType(valueTypeEnum, shape), true, values);


        if (irManager.isInGlobal()) {
            // 如果在全局位置
            varSymbol.operand = irManager.addGlobalConst(identToken.content(), shape, values);
        } else {
            varSymbol.operand = irManager.addAllocaInst(IRType.IRValueType.I32, shape);
        }

        visit(constDef.constInitVal(), shape, values, varSymbol); // 在这里面进行维数及其长度对比，和数值赋值
        symbolManager.addVarSymbol(identToken.content(), varSymbol); // 符号表在 initVal 之后再添加
    }

    private VisitResult visit(ConstExp constExp) {
        if (constExp == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        return visit(constExp.addExp());
    }

    private Operand initValForConstAndVar(List<Integer> values, VarSymbol varSymbol) {
        var indexes = new ArrayList<Integer>();
        int size = 1;
        for (int i = varSymbol.valueType.shape().size() - 1; i >= 0; i--) {
            var len = varSymbol.valueType.shape().get(i);
            indexes.add((values.size() - 1) / size % len);
            size *= len;
        }
        indexes.add(0);
        Collections.reverse(indexes);
        return irManager.addGetElementPtrInst(new ArrayList<>(), varSymbol.operand, indexes);
    }

    private void visit(ConstInitVal constInitVal, List<Integer> shape, List<Integer> values, VarSymbol varSymbol) {
        //  ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if (constInitVal == null) {
            return;
        }
        if (constInitVal.constExp() != null) {
            var result = visit(constInitVal.constExp());
            values.add(result.value);
            if (!irManager.isInGlobal()) {
                // 添加指令
                if (!varSymbol.operand.irType.shape.isEmpty()) {
                    // 如果是数组
                    // 确定 getelementptr 的下标
                    var tempOperand = initValForConstAndVar(values, varSymbol);
                    irManager.addInstruction(new StoreInst(constInitVal.constExp().addExp().operand, tempOperand));
                } else {
                    // 不是数组
                    irManager.addInstruction(new StoreInst(constInitVal.constExp().addExp().operand, varSymbol.operand));
                }
            }
        } else if (!constInitVal.constInitValList().isEmpty()) {
            if (!shape.isEmpty() && shape.get(0) == constInitVal.constInitValList().size()) {
                for (ConstInitVal constInitVal1 : constInitVal.constInitValList()) {
                    var newShape = new ArrayList<>(shape);
                    newShape.remove(0);
                    visit(constInitVal1, newShape, values, varSymbol); // 传入去除当前维度之后的shape
                }
            }
            // else 长度不匹配，error
        }
    }

    private void visit(Decl decl) {
        // Decl → ConstDecl | VarDecl
        if (decl == null) {
            return;
        }
        if (decl.constDecl() != null) {
            visit(decl.constDecl());
        } else if (decl.varDecl() != null) {
            visit(decl.varDecl());
        }
    }

    private void visit(EqExp eqExp) {
        // EqExp → RelExp | { ('==' | '!=') RelExp }
        if (eqExp == null) {
            return;
        }
        eqExp.relExpList.forEach(this::visit);

        eqExp.operand = eqExp.relExpList.get(0).operand;
        for (int i = 0; i < eqExp.opLexTypeList.size(); i++) { // 这个还可以处理 2 == 2 == 1的情况
            var relExp = eqExp.relExpList.get(1 + i);
            eqExp.operand = switch (eqExp.opLexTypeList.get(i)) {
                case EQL -> irManager.addIcmpInst(IcmpInst.IcmpCond.EQ, eqExp.operand, relExp.operand);
                case NEQ -> irManager.addIcmpInst(IcmpInst.IcmpCond.NE, eqExp.operand, relExp.operand);
                default -> throw new IllegalStateException("Unexpected value: " + eqExp.opLexTypeList.get(i));
            };
        }
        if (eqExp.operand.irType.irValueType != IRType.IRValueType.I1) {
            eqExp.operand = irManager.addIcmpInst(IcmpInst.IcmpCond.NE, new ConstantOperand(0), eqExp.operand);
        }
        irManager.addBrInst(eqExp.operand, eqExp.nextEqExpBasicBlock, eqExp.nextLAndExpBasicBlock);
    }

    private VisitResult visit(Exp exp) {
        //  Exp → AddExp
        if (exp == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        var result = visit(exp.addExp);
        exp.operand = exp.addExp.operand;
        return result;
    }

    private void visit(ForStmt forStmt) {
        // ForStmt → LVal '=' Exp
        if (forStmt == null) {
            return;
        }
        visit(forStmt.lVal(), true, false); // 题目没有要求在这个地方检查是否改变常量
        visit(forStmt.exp());
    }

    private void visit(FuncDef funcDef) {
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        if (funcDef == null) {
            return;
        }
        var returnValueType = visit(funcDef.funcType());

        var identToken = funcDef.ident().token();
        if (symbolManager.isFuncSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "function redefinition of '" + identToken.content() + "'");
        }

        symbolManager.createSymbolTable();

        List<VarSymbol> varSymbolList = new ArrayList<>();
        if (funcDef.funcFParams() != null) {
            visit(funcDef.funcFParams(), varSymbolList);
        }

        var funcSymbol = new FuncSymbol(returnValueType, varSymbolList);
        symbolManager.addFuncSymbol(identToken.content(), funcSymbol);
        curFuncReturnType = returnValueType; // 记录当前函数的返回类型，为检查return语句错误做准备

        irManager.addFunctionDecl(curFuncReturnType, identToken.content(), varSymbolList, funcSymbol);

        visit(funcDef.block(), returnValueType != ValueTypeEnum.VOID); // 如果不为void函数则检查最后一个语句是否为return
        if (returnValueType == ValueTypeEnum.VOID) { // 如果函数是void，无论是否有"return;"，均在此添加指令
            irManager.addRetInst(null);
            irManager.setCurrentBasicBlock(new BasicBlock());
        }
        curFuncReturnType = ValueTypeEnum.VOID;

        symbolManager.traceBack();
    }

    private void visit(FuncFParam funcFParam, List<VarSymbol> varSymbolList) {
        // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        if (funcFParam == null) {
            return;
        }
        var valueTypeEnum = visit(funcFParam.bType());
        var identToken = funcFParam.ident().token();
        VarSymbol varSymbol;

        if (symbolManager.isVarSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "redefinition of parameter '" + identToken.content() + "'");
            return;
        }

        if (!funcFParam.isArray()) {
            // 不是数组
            varSymbol = new VarSymbol(new ValueType(valueTypeEnum, new ArrayList<>()), false, null);
        } else if (funcFParam.constExpList().isEmpty()) {
            varSymbol = new VarSymbol(new ValueType(valueTypeEnum, new ArrayList<>(List.of(-1))), false, null);
        } else {
            var shape = new ArrayList<>(List.of(-1)); // -1 用来表示[]而不是[3]
            for (var constExp : funcFParam.constExpList()) { // 其实二维不需要这个，这样写就可以支持多维
                var result = visit(constExp);
                shape.add(result.value);
            }
            varSymbol = new VarSymbol(new ValueType(valueTypeEnum, shape), false, null);
        }
        varSymbol.isFuncFParam = true; // 留给lVal生成llvm的load时使用
        symbolManager.addVarSymbol(identToken.content(), varSymbol);
        varSymbolList.add(varSymbol);
    }

    private void visit(FuncFParams funcFParams, List<VarSymbol> varSymbolList) {
        // FuncFParams → FuncFParam { ',' FuncFParam }
        funcFParams.funcFParamList().forEach(funcFParam -> visit(funcFParam, varSymbolList));
    }

    private List<VisitResult> visit(FuncRParams funcRParams, List<Operand> operandList) {
        //  FuncRParams → Exp { ',' Exp }
        var visitResultList = new ArrayList<VisitResult>();
        if (funcRParams == null) {
            return visitResultList;
        }
        funcRParams.expList().forEach(exp -> {
            visitResultList.add(visit(exp));
            operandList.add(exp.operand);
        });
        return visitResultList;
    }

    private ValueTypeEnum visit(FuncType funcType) {
        if (funcType == null) {
            return ValueTypeEnum.VOID;
        }
        if (funcType.type() == LexType.INTTK) {
            return ValueTypeEnum.INT;
        }
        return ValueTypeEnum.VOID;
    }

    private void visit(InitVal initVal, List<Integer> shape, List<Integer> values, VarSymbol varSymbol) {
//        InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'// 1.表达式初值 2.一维数组初值 3.二维数组初值
        if (initVal == null) {
            return;
        }
        if (initVal.exp() != null) {
            var result = visit(initVal.exp());
            values.add(result.value);

            if (!irManager.isInGlobal()) {
                // 添加指令
                if (!varSymbol.operand.irType.shape.isEmpty()) {
                    // 如果是数组
                    // 确定 getelementptr 的下标
                    var tempOperand = initValForConstAndVar(values, varSymbol);
                    irManager.addInstruction(new StoreInst(initVal.exp().operand, tempOperand));
                } else {
                    //不是数组
                    irManager.addInstruction(new StoreInst(initVal.exp().operand, varSymbol.operand));
                }
            }
        } else if (!initVal.initValList().isEmpty()) {
            // 递归
            if (!shape.isEmpty() && shape.get(0) == initVal.initValList().size()) {
                for (InitVal initVal1 : initVal.initValList()) {
                    var newShape = new ArrayList<>(shape);
                    newShape.remove(0);
                    visit(initVal1, newShape, values, varSymbol); // same to constInitVal 为了在全局变量初始化时计算出初始值
                }
            }
            // else 维度的长度不匹配，error
        }
    }

    private void visit(LAndExp lAndExp) {
        //LAndExp → EqExp | LAndExp '&&' EqExp
        //改写为 LAndExp -> EqExp { '&&' EqExp }
        if (lAndExp == null) {
            return;
        }

        for (int i = 0; i < lAndExp.eqExpList.size(); i++) {
            var eqExp = lAndExp.eqExpList.get(i);
            eqExp.nextLAndExpBasicBlock = lAndExp.nextLAndExpBasicBlock; // 如果外层是最后一个lAnd那么这个就是stmt2(如果没有stmt2那么就是stmt3)
            if (i == 0) {
                eqExp.eqExpBasicBlock = lAndExp.lAndExpBasicBlock;
            } else {
                eqExp.eqExpBasicBlock = new BasicBlock();
            }
        }
        for (int i = 0; i < lAndExp.eqExpList.size(); i++) {
            var eqExp = lAndExp.eqExpList.get(i);
            if (i == lAndExp.eqExpList.size() - 1) {
                // 如果是最后一个eqExp
                eqExp.nextEqExpBasicBlock = lAndExp.stmt1BasicBlock;
            } else {
                eqExp.nextEqExpBasicBlock = lAndExp.eqExpList.get(1 + i).eqExpBasicBlock;
            }
            if (i != 0) {
                irManager.setCurrentBasicBlock(eqExp.eqExpBasicBlock);
            }
            visit(eqExp);
        }
    }

    private void visit(LOrExp lOrExp) {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        //改写为 LOrExp -> LAndExp { '||' LAndExp }
        if (lOrExp == null) {
            return;
        }
        for (int i = 0; i < lOrExp.lAndExpList.size(); i++) {
            var lAndExp = lOrExp.lAndExpList.get(i);
            lAndExp.stmt1BasicBlock = lOrExp.stmt1BasicBlock;
            if (i == 0) {
                lAndExp.lAndExpBasicBlock = lOrExp.condBasicBlock;
            } else {
                lAndExp.lAndExpBasicBlock = new BasicBlock();
            }
        }
        for (int i = 0; i < lOrExp.lAndExpList.size(); i++) {
            var lAndExp = lOrExp.lAndExpList.get(i);
            if (i == lOrExp.lAndExpList.size() - 1) {
                // 如果是最后一个lAndExp
                if (lOrExp.stmt2BasicBlock == null) {
                    // 如果没有else stmt
                    lAndExp.nextLAndExpBasicBlock = lOrExp.stmt3BasicBlock;
                } else {
                    lAndExp.nextLAndExpBasicBlock = lOrExp.stmt2BasicBlock;
                }
            } else {
                lAndExp.nextLAndExpBasicBlock = lOrExp.lAndExpList.get(1 + i).lAndExpBasicBlock;
            }
            if (i != 0) {
                // 如果不是第一个，那么就设置一下进入新的block
                irManager.setCurrentBasicBlock(lAndExp.lAndExpBasicBlock);
            }
            visit(lAndExp);
        }
    }

    private VisitResult visit(LVal lVal, boolean checkConst, boolean isFromPrimaryExp) {
        //  LVal → Ident {'[' Exp ']'}
        if (lVal == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        var identToken = lVal.ident.token();
        var varSymbol = symbolManager.findVarSymbol(identToken.content());
        if (varSymbol == null) {
            OutputHelper.addError(ErrorType.IDENT_UNDEFINED, identToken.lineNum(), "'" + identToken.content() + "' undeclared");
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, null), false, 0);
        } else {
            // 包含了常量和变量的两种情况
            if (checkConst && varSymbol.isConst) {
                OutputHelper.addError(ErrorType.MODIFY_CONST, identToken.lineNum(), "assignment of read-only variable 'a', modify const");
            }
            var newShape = new ArrayList<>(varSymbol.valueType.shape());
            var indexList = new ArrayList<Integer>();
            for (var exp : lVal.expList) {
                var result = visit(exp);
                indexList.add(result.value);
                newShape.remove(0);
            }

            // llvm
            if (varSymbol.valueType.shape().isEmpty()) {
                //如果 varSymbol 不是数组
                lVal.operand = varSymbol.operand;
            } else {
                // 如果 varSymbol 是数组

                // 计算 getElementPtr 的 index
                var getElementPtrIndexList = new ArrayList<Integer>();
                if (!varSymbol.isFuncFParam) { // 如果不是函数参数，就在一开始加一个0（将[2 x [2 x i32]]变成[2 x i32]*），如果是函数形参，那么就不要加这个0，因为其本来就是[2 x i32]*
                    getElementPtrIndexList.add(0);
                }
                getElementPtrIndexList.addAll(indexList); // 把 Exp 的索引加进去
                if (indexList.size() < varSymbol.valueType.shape().size()) { // 后面补0
                    getElementPtrIndexList.add(0);
                }

                // 计算新 shape
                var newOperandShape = new ArrayList<>(varSymbol.valueType.shape());
                if (varSymbol.isFuncFParam) { // 如果是参数的shape，那么先把表示[]的-1删去
                    newOperandShape.remove(0);
                }
                for (int i = 1; i < getElementPtrIndexList.size(); i++) {
                    newOperandShape.remove(0);
                }

                lVal.operand = irManager.addGetElementPtrInst(newOperandShape, varSymbol.operand, getElementPtrIndexList);
            }

            if (isFromPrimaryExp && varSymbol.valueType.shape().size() == indexList.size()) {
                // 如果取到了元素那一级，那么就开一个 LoadInst
                lVal.operand = irManager.addLoadInst(lVal.operand);
            }

            return new VisitResult(new ValueType(varSymbol.valueType.type(), newShape), varSymbol.isConst, varSymbol.getValue(indexList)); // 如果是变量的话getValue()会直接返回一个0
        }
    }

    private void visit(MainFuncDef mainFuncDef) {
        // MainFuncDef → 'int' 'main' '(' ')' Block
        if (mainFuncDef == null) {
            return;
        }

        curFuncReturnType = ValueTypeEnum.INT;
        symbolManager.createSymbolTable();

        irManager.addFunctionDecl(curFuncReturnType, "main", new ArrayList<>(), new FuncSymbol(ValueTypeEnum.INT, new ArrayList<>())); // main的funcSymbol没有存下来，这里写是为了和一般的function保持一致

        visit(mainFuncDef.block(), true);
        symbolManager.traceBack();
        curFuncReturnType = ValueTypeEnum.VOID;
    }

    private VisitResult visit(MulExp mulExp) {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //改写为 MulExp -> UnaryExp { ('*' | '/' | '%') UnaryExp }
        if (mulExp == null || mulExp.unaryExpList.isEmpty()) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        int value = 0;
        var result = visit(mulExp.unaryExpList.get(0));
        var valueType = result.valueType;
        if (result.isConst) {
            value = result.value;
        }
        boolean isConst = result.isConst;

        for (int i = 0; i < mulExp.opLexTypeList.size(); i++) {
            result = visit(mulExp.unaryExpList.get(1 + i));
            if (isConst && result.isConst) {
                switch (mulExp.opLexTypeList.get(i)) {
                    case MULT -> value *= result.value;
                    case DIV -> value /= result.value;
                    case MOD -> value %= result.value;
                    default -> throw new IllegalStateException("Unexpected value: " + mulExp.opLexTypeList.get(i));
                }
            } else {
                isConst = false;
            }
        }

        mulExp.operand = mulExp.unaryExpList.get(0).operand;
        if (!isConst) { // 常量优化
            for (int i = 0; i < mulExp.opLexTypeList.size(); i++) {
                var unaryExp = mulExp.unaryExpList.get(1 + i);
                mulExp.operand = switch (mulExp.opLexTypeList.get(i)) {
                    case MULT -> irManager.addMulInst(mulExp.operand, unaryExp.operand);
                    case DIV -> irManager.addSdivInst(mulExp.operand, unaryExp.operand);
                    case MOD -> irManager.addSremInst(mulExp.operand, unaryExp.operand);
                    default -> throw new IllegalStateException("Unexpected value: " + mulExp.opLexTypeList.get(i));
                };
            }
        } else {
            mulExp.operand = new ConstantOperand(value); // 如果能计算出来，那么直接开一个常量操作数，且只需要在addExp开就够了
        }

        return new VisitResult(valueType, isConst, value);
    }

    private VisitResult visit(Number number) {
        if (number == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        if (!irManager.isInGlobal()) {
            number.operand = new ConstantOperand(number.intConst);
        }
        return new VisitResult(new ValueType(ValueTypeEnum.INT, new ArrayList<>()), true, number.intConst);
    }

    private VisitResult visit(PrimaryExp primaryExp) {
        // PrimaryExp → '(' Exp ')' | LVal | Number
        if (primaryExp == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        if (primaryExp.exp != null) {
            // '(' Exp ')'
            var result = visit(primaryExp.exp);
            primaryExp.operand = primaryExp.exp.operand;
            return result;
        } else if (primaryExp.lVal != null) {
            // LVal
            var result = visit(primaryExp.lVal, false, true);
            if (!irManager.isInGlobal()) {
                primaryExp.operand = primaryExp.lVal.operand;
            }
            return result;
        } else if (primaryExp.number != null) {
            // Number
            var result = visit(primaryExp.number);
            primaryExp.operand = primaryExp.number.operand;
            return result;
        }
        return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
    }

    private void visit(RelExp relExp) {
        //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        //改写为 RelExp -> AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        if (relExp == null) {
            return;
        }
        relExp.addExpList.forEach(this::visit);

        relExp.operand = relExp.addExpList.get(0).operand;
        for (int i = 0; i < relExp.opLexTypeList.size(); i++) {
            var addExp = relExp.addExpList.get(1 + i);
            relExp.operand = switch (relExp.opLexTypeList.get(i)) { // 在addIcmpInst中包含了类型转换以处理 1<2<3 连比的情况
                case LSS -> irManager.addIcmpInst(IcmpInst.IcmpCond.SLT, relExp.operand, addExp.operand);
                case LEQ -> irManager.addIcmpInst(IcmpInst.IcmpCond.SLE, relExp.operand, addExp.operand);
                case GRE -> irManager.addIcmpInst(IcmpInst.IcmpCond.SGT, relExp.operand, addExp.operand);
                case GEQ -> irManager.addIcmpInst(IcmpInst.IcmpCond.SGE, relExp.operand, addExp.operand);
                default -> throw new IllegalStateException("Unexpected value: " + relExp.opLexTypeList.get(i));
            };
        }
    }

    private Stack<BasicBlock> forStmt3BasicBlockStack = new Stack<>(); // 给for跳转使用的栈
    private Stack<BasicBlock> endBasicBlockStack = new Stack<>();

    private void visit(Stmt stmt) {
        //Stmt → LVal '=' Exp ';'
        //| [Exp] ';'
        //| Block
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [forStmt] ')' Stmt
        //| 'break' ';' | 'continue' ';'
        //| 'return' [Exp] ';'
        //| LVal '=' 'getint''('')'';'
        //| 'printf''('FormatString{','Exp}')'';'
        if (stmt == null) {
            return;
        }
        if (stmt instanceof StmtBlock stmtBlock) {
            symbolManager.createSymbolTable();
            visit(stmtBlock.block, false);
            symbolManager.traceBack();
        } else if (stmt instanceof StmtBreak stmtBreak) {
            if (endBasicBlockStack.isEmpty()) {
                OutputHelper.addError(ErrorType.BREAK_CONTINUE_ERROR, stmtBreak.token.lineNum(), "break statement not within a loop");
            } else {
                irManager.addBrInst(endBasicBlockStack.peek());
                irManager.setCurrentBasicBlock(new BasicBlock());
            }
        } else if (stmt instanceof StmtContinue stmtContinue) {
            if (forStmt3BasicBlockStack.isEmpty()) {
                OutputHelper.addError(ErrorType.BREAK_CONTINUE_ERROR, stmtContinue.token.lineNum(), "continue statement not within a loop");
            } else {
                irManager.addBrInst(forStmt3BasicBlockStack.peek());
                irManager.setCurrentBasicBlock(new BasicBlock());
            }
        } else if (stmt instanceof StmtFor stmtFor) {
            // 'for' '(' [ForStmt] ';' [Cond] ';' [forStmt] ')' Stmt
            var condBasicBlock = new BasicBlock();
            var stmt1BasicBlock = new BasicBlock();
            var forStmt3BasicBlock = new BasicBlock();
            var endBasicBlock = new BasicBlock();
            stmtFor.cond.lOrExp.condBasicBlock = condBasicBlock;
            stmtFor.cond.lOrExp.stmt1BasicBlock = stmt1BasicBlock;
            stmtFor.cond.lOrExp.stmt3BasicBlock = endBasicBlock;
            visit(stmtFor.forStmt1);

            irManager.addBrInst(condBasicBlock);
            irManager.setCurrentBasicBlock(condBasicBlock);
            visit(stmtFor.cond);

            irManager.setCurrentBasicBlock(forStmt3BasicBlock);
            visit(stmtFor.forStmt3);
            irManager.addBrInst(condBasicBlock);

            irManager.setCurrentBasicBlock(stmt1BasicBlock);
            forStmt3BasicBlockStack.push(forStmt3BasicBlock);
            endBasicBlockStack.push(endBasicBlock);
            visit(stmtFor.stmt);
            endBasicBlockStack.pop();
            forStmt3BasicBlockStack.pop();
            irManager.addBrInst(forStmt3BasicBlock);

            irManager.setCurrentBasicBlock(endBasicBlock);
        } else if (stmt instanceof StmtIf stmtIf) {
            // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            var condBasicBlock = new BasicBlock();
            var stmt1BasicBlock = new BasicBlock();
            var stmt2BasicBlock = stmtIf.elseStmt == null ? null : new BasicBlock();
            var stmt3BasicBlock = new BasicBlock();
            stmtIf.cond.lOrExp.condBasicBlock = condBasicBlock;
            stmtIf.cond.lOrExp.stmt1BasicBlock = stmt1BasicBlock;
            stmtIf.cond.lOrExp.stmt2BasicBlock = stmt2BasicBlock;
            stmtIf.cond.lOrExp.stmt3BasicBlock = stmt3BasicBlock;
            irManager.addBrInst(condBasicBlock);
            irManager.setCurrentBasicBlock(condBasicBlock);
            visit(stmtIf.cond);
            irManager.setCurrentBasicBlock(stmt1BasicBlock);
            visit(stmtIf.stmt);
            irManager.addBrInst(stmt3BasicBlock); // 在trueBasicBlock最后加跳转到endBasicBlock的命令
            if (stmtIf.elseStmt != null) {
                irManager.setCurrentBasicBlock(stmt2BasicBlock);
                visit(stmtIf.elseStmt);
                irManager.addBrInst(stmt3BasicBlock); // 在falseBasicBlock最后加跳转到endBasicBlock的命令
            }
            irManager.setCurrentBasicBlock(stmt3BasicBlock);
        } else if (stmt instanceof StmtLValExp stmtLValExp) {
            // LVal '=' Exp ';'               -> Ident { '[' Exp ']' } '=' Exp ';'
            // | LVal '=' 'getint''('')'';'   -> Ident { '[' Exp ']' } '=' 'getint' '('...
            // | [Exp] ';'                    -> Ident { '[' Exp ']' } '+-*/%' || ';' || '(' Exp ')'... || Number || '+'|'-'|'!'
            switch (stmtLValExp.type) {
                case LVALEXP -> {
                    visit(stmtLValExp.lVal, true, false);
                    visit(stmtLValExp.exp);
                    irManager.addInstruction(new StoreInst(stmtLValExp.exp.operand, stmtLValExp.lVal.operand));
                }
                case GETINT -> {
                    visit(stmtLValExp.lVal, true, false);
                    var tempOperand = irManager.addCallGetIntInst();
                    irManager.addStoreInst(tempOperand, stmtLValExp.lVal.operand);
                }
                case EXP -> {
                    if (stmtLValExp.exp != null) {
                        visit(stmtLValExp.exp);
                    }
                }
            }
        } else if (stmt instanceof StmtPrint stmtPrint) {
            //'printf''('FormatString{','Exp}')'';'
            var expOperandList = new ArrayList<Operand>();
            stmtPrint.expList.forEach(exp -> {
                visit(exp);
                expOperandList.add(exp.operand);
            });
            irManager.addCallPutInst(stmtPrint.formatString.content(), stmtPrint.formatString.indexList(), expOperandList);
        } else if (stmt instanceof StmtReturn stmtReturn) {
            //'return' [Exp] ';'
            if (stmtReturn.exp != null) {
                if (curFuncReturnType == ValueTypeEnum.VOID) {
                    OutputHelper.addError(ErrorType.VOID_RETURN, stmtReturn.returnToken.lineNum(), "'return' with a value, in function returning void");
                }
                visit(stmtReturn.exp);
                irManager.addRetInst(stmtReturn.exp.operand);
                irManager.setCurrentBasicBlock(new BasicBlock());
            }
        }
    }

    private VisitResult visit(UnaryExp unaryExp) {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp //  UnaryOp → '+' | '−' | '!'
        if (unaryExp == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        if (unaryExp.primaryExp != null) {
            var result = visit(unaryExp.primaryExp);
            unaryExp.operand = unaryExp.primaryExp.operand;
            return result;
        } else if (unaryExp.unaryOp != null && unaryExp.unaryExp != null) {
            var op = visit(unaryExp.unaryOp);
            var result = visit(unaryExp.unaryExp);
            // 没有管是常量还是变量
            switch (op) {
                case PLUS -> unaryExp.operand = unaryExp.unaryExp.operand;
                case MINU -> {
                    result.value = -result.value;
                    if (!irManager.isInGlobal()) {
                        if (result.isConst) {
                            unaryExp.operand = new ConstantOperand(result.value);
                        } else {
                            unaryExp.operand = irManager.addSubInst(new ConstantOperand(0), unaryExp.operand);
                        }
                    }
                }
                case NOT -> {
                    if (result.value != 0) {
                        result.value = 0;
                    } else {
                        result.value = 1;
                    }
                    unaryExp.operand = irManager.addIcmpInst(IcmpInst.IcmpCond.EQ, new ConstantOperand(0), unaryExp.unaryExp.operand);
                }
                default -> throw new IllegalStateException("Unexpected value: " + op);
            }
            return result;
        } else if (unaryExp.ident != null) {
            // Ident '(' [FuncRParams] ')'
            // 唯一函数调用
            var identToken = unaryExp.ident.token();
            var funcSymbol = symbolManager.findFuncSymbol(identToken.content());
            if (!symbolManager.isFuncSymbolDefined(identToken.content())) {
                OutputHelper.addError(ErrorType.IDENT_UNDEFINED, identToken.lineNum(), "function '" + identToken.content() + "' undefined");
                funcSymbol = new FuncSymbol(ValueTypeEnum.VOID, new ArrayList<>());
            }

            var funcRParamOperandList = new ArrayList<Operand>();

            if (unaryExp.funcRParams != null) {
                // 有实参
                var resultList = visit(unaryExp.funcRParams, funcRParamOperandList);
                if (funcSymbol.paramVarSymbolList.size() != resultList.size()) {
                    // 参数数量错误
                    OutputHelper.addError(ErrorType.FUNC_PARAM_NUM_ERROR, identToken.lineNum(), "too few or many arguments to function '" + identToken.content() + "'");
                } else {
                    // 参数数量正确
                    for (int i = 0; i < resultList.size(); i++) {
                        var funcFParamValueType = funcSymbol.paramVarSymbolList.get(i).valueType;
                        var funcRParamValueType = resultList.get(i).valueType;
                        if (!funcFParamValueType.isFParamToRParamValid(funcRParamValueType)) { // ValueType.equals()方法重写了，考虑了[][3]的情况
                            // 参数类型错误
                            OutputHelper.addError(ErrorType.FUNC_PARAM_TYPE_ERROR, identToken.lineNum(),
                                    "expected '" + funcFParamValueType.type() + funcFParamValueType.shape() + "' but argument is of type '" + funcRParamValueType.type() + funcRParamValueType.shape() + "'");
                        }
                    }
                }
            }

            if (funcSymbol.valueTypeEnum == ValueTypeEnum.VOID) { // 如果是void就不开寄存器储存返回值了
                irManager.addInstruction(new CallInst(funcSymbol.function, funcRParamOperandList));
            } else {
                var tempOperand = irManager.allocTempOperand(new IRType(new ValueType(funcSymbol.valueTypeEnum, new ArrayList<>())));
                irManager.addInstruction(new CallInst(tempOperand, funcSymbol.function, funcRParamOperandList));
                unaryExp.operand = tempOperand;
            }

            return new VisitResult(new ValueType(funcSymbol.valueTypeEnum, new ArrayList<>()), false, 0);
        }
        return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
    }

    private LexType visit(UnaryOp unaryOp) {
        if (unaryOp == null) {
            return LexType.PLUS;
        }
        return unaryOp.opType();
    }

    private void visit(VarDecl varDecl) {
        //  VarDecl → BType VarDef { ',' VarDef } ';'
        if (varDecl == null) {
            return;
        }
        var valueTypeEnum = visit(varDecl.bType());
        for (var varDef : varDecl.varDefList()) {
            visit(varDef, valueTypeEnum);
        }
    }

    private void visit(VarDef varDef, ValueTypeEnum valueTypeEnum) {
        // VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义
        // | Ident { '[' ConstExp ']' } '=' InitVal
        if (varDef == null) {
            return;
        }
        var shape = new ArrayList<Integer>();
        var values = new ArrayList<Integer>();

        var identToken = varDef.ident().token();
        if (symbolManager.isVarSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "var redefinition of '" + identToken.content() + "'");
            return;
        }

        varDef.constExpList().forEach(constExp -> shape.add(visit(constExp).value));

        VarSymbol varSymbol = new VarSymbol(new ValueType(valueTypeEnum, shape), false, new ArrayList<>());

        if (irManager.isInGlobal()) {
            // 如果在全局定义的位置
            varSymbol.operand = irManager.addGlobalVar(identToken.content(), shape, values);
        } else {
            // 如果不是全局位置
            varSymbol.operand = irManager.addAllocaInst(IRType.IRValueType.I32, shape);
        }

        if (varDef.initVal() != null) { // 如果有初始值，特别是全局变量中，其一定为constInitVal
            visit(varDef.initVal(), shape, values, varSymbol);
        }
        // 过完 initVal 之后再添加符号表，先用再定义，如以下情况： int c = 3; int main(){ int c = c + 1; }
        symbolManager.addVarSymbol(identToken.content(), varSymbol);
    }
}
