/*
@Time    : 2023/10/9 15:18
@Author  : Elaikona
*/
package Compiler.Visitor;

import Compiler.LLVMIR.IRManager;
import Compiler.LLVMIR.IRModule;
import Compiler.LLVMIR.Instructions.AllocaInst;
import Compiler.LLVMIR.Instructions.Quadruple.*;
import Compiler.LLVMIR.Instructions.RetInst;
import Compiler.LLVMIR.Operand.ConstantOperand;
import Compiler.LLVMIR.Operand.TempOperand;
import Compiler.LLVMIR.Value;
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
import java.util.List;

public class Visitor {
    private final CompUnit unit;
    private final SymbolManager symbolManager;
    private int loop = 0;
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
        return irManager.getModule();
    }

    private VisitResult visit(AddExp addExp) {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        // 改写为 AddExp -> MulExp { ('+' | '−') MulExp }

        if (addExp == null || addExp.mulExpList.isEmpty()) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }

        boolean isConst = true;
        int value = 0;
        var result = visit(addExp.mulExpList.get(0));
        var valueType = result.valueType;
        if (result.isConst) {
            value = result.value;
        }
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

        if (!isConst) { // 常量优化
            addExp.operand = addExp.mulExpList.get(0).operand;
            for (int i = 0; i < addExp.opLexTypeList.size(); i++) {
                var mulExp = addExp.mulExpList.get(1 + i);
                var tempOperand = irManager.allocTempOperand(Value.IRValueType.I32);
                switch (addExp.opLexTypeList.get(i)) {
                    case PLUS ->
                            irManager.addInstruction(new AddInst(tempOperand, Value.IRValueType.I32, addExp.operand, mulExp.operand));
                    case MINU ->
                            irManager.addInstruction(new SubInst(tempOperand, Value.IRValueType.I32, addExp.operand, mulExp.operand));
                }
                addExp.operand = tempOperand;
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
        visit(cond.lOrExp());
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

        visit(constDef.constInitVal(), shape, values); // 在这里面进行维数及其长度对比，和数值赋值

        symbolManager.addVarSymbol(identToken.content(), new VarSymbol(new ValueType(valueTypeEnum, shape), true, values));
        irManager.addGlobalConst(identToken.content(), shape, values);
    }

    private VisitResult visit(ConstExp constExp) {
        if (constExp == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        return visit(constExp.addExp());
    }

    private void visit(ConstInitVal constInitVal, List<Integer> shape, List<Integer> values) {
        //  ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if (constInitVal == null) {
            return;
        }
        if (constInitVal.constExp() != null) {
            var result = visit(constInitVal.constExp());
            values.add(result.value);
        } else if (!constInitVal.constInitValList().isEmpty()) {
            if (!shape.isEmpty() && shape.get(0) == constInitVal.constInitValList().size()) {
                for (ConstInitVal constInitVal1 : constInitVal.constInitValList()) {
                    var newShape = new ArrayList<>(shape);
                    newShape.remove(0);
                    visit(constInitVal1, newShape, values); // 传入去除当前维度之后的shape
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
        eqExp.relExpList().forEach(this::visit);
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
        visit(forStmt.lVal(), true); // 题目没有要求在这个地方检查是否改变常量
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

        symbolManager.addFuncSymbol(identToken.content(), new FuncSymbol(returnValueType, varSymbolList));
        curFuncReturnType = returnValueType; // 记录当前函数的返回类型，为检查return语句错误做准备

        irManager.addFunctionDecl(curFuncReturnType, identToken.content());

        visit(funcDef.block(), returnValueType != ValueTypeEnum.VOID); // 如果不为void函数则检查最后一个语句是否为return
        if (returnValueType == ValueTypeEnum.VOID) { // 如果函数是void，无论是否有"return;"，均在此添加指令
            irManager.addInstruction(new RetInst(new Value(Value.IRValueType.VOID)));
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
        symbolManager.addVarSymbol(identToken.content(), varSymbol);
        varSymbolList.add(varSymbol);
    }

    private void visit(FuncFParams funcFParams, List<VarSymbol> varSymbolList) {
        // FuncFParams → FuncFParam { ',' FuncFParam }
        funcFParams.funcFParamList().forEach(funcFParam -> visit(funcFParam, varSymbolList));
    }

    private List<VisitResult> visit(FuncRParams funcRParams) {
        //  FuncRParams → Exp { ',' Exp }
        var visitResultList = new ArrayList<VisitResult>();
        if (funcRParams == null) {
            return visitResultList;
        }
        funcRParams.expList().forEach(exp -> visitResultList.add(visit(exp)));
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

    private void visit(InitVal initVal, List<Integer> shape, List<Integer> values) {
//        InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'// 1.表达式初值 2.一维数组初值 3.二维数组初值
        if (initVal == null) {
            return;
        }
        if (initVal.exp() != null) {
            var result = visit(initVal.exp());
            values.add(result.value);
        } else if (!initVal.initValList().isEmpty()) {
            if (!shape.isEmpty() && shape.get(0) == initVal.initValList().size()) {
                for (InitVal initVal1 : initVal.initValList()) {
                    var newShape = new ArrayList<>(shape);
                    newShape.remove(0);
                    visit(initVal1, newShape, values); // same to constInitVal 为了在全局变量初始化时计算出初始值
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
        lAndExp.eqExpList().forEach(this::visit);
    }

    private void visit(LOrExp lOrExp) {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        //改写为 LOrExp -> LAndExp { '||' LAndExp }
        if (lOrExp == null) {
            return;
        }
        lOrExp.lAndExpList().forEach(this::visit);
    }

    private VisitResult visit(LVal lVal, boolean checkConst) {
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

        irManager.addFunctionDecl(curFuncReturnType, "main");

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
        boolean isConst = true;
        int value = 0;
        var result = visit(mulExp.unaryExpList.get(0));
        var valueType = result.valueType;
        if (result.isConst) {
            value = result.value;
        }
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

        if (!isConst) { // 常量优化
            mulExp.operand = mulExp.unaryExpList.get(0).operand;
            for (int i = 0; i < mulExp.opLexTypeList.size(); i++) {
                var unaryExp = mulExp.unaryExpList.get(1 + i);
                var tempOperand = irManager.allocTempOperand(Value.IRValueType.I32);
                switch (mulExp.opLexTypeList.get(i)) {
                    case MULT ->
                            irManager.addInstruction(new MulInst(tempOperand, Value.IRValueType.I32, mulExp.operand, unaryExp.operand));
                    case DIV ->
                            irManager.addInstruction(new SdivInst(tempOperand, Value.IRValueType.I32, mulExp.operand, unaryExp.operand));
                    case MOD ->
                            irManager.addInstruction(new SremInst(tempOperand, Value.IRValueType.I32, mulExp.operand, unaryExp.operand));
                }
                mulExp.operand = tempOperand;
            }
        }

        return new VisitResult(valueType, isConst, value);
    }

    private VisitResult visit(Number number) {
        if (number == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        number.operand = new ConstantOperand(number.intConst);
        return new VisitResult(new ValueType(ValueTypeEnum.INT, new ArrayList<>()), true, number.intConst);
    }

    private VisitResult visit(PrimaryExp primaryExp) {
        // PrimaryExp → '(' Exp ')' | LVal | Number
        if (primaryExp == null) {
            return new VisitResult(new ValueType(ValueTypeEnum.VOID, new ArrayList<>()), false, 0);
        }
        if (primaryExp.exp != null) {
            var result = visit(primaryExp.exp);
            primaryExp.operand = primaryExp.exp.operand;
            return result;
        } else if (primaryExp.lVal != null) {
            return visit(primaryExp.lVal, false);
        } else if (primaryExp.number != null) {
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
        relExp.addExpList().forEach(this::visit);
    }

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
            if (loop == 0) {
                OutputHelper.addError(ErrorType.BREAK_CONTINUE_ERROR, stmtBreak.token.lineNum(), "break statement not within a loop");
            }
        } else if (stmt instanceof StmtContinue stmtContinue) {
            if (loop == 0) {
                OutputHelper.addError(ErrorType.BREAK_CONTINUE_ERROR, stmtContinue.token.lineNum(), "continue statement not within a loop");
            }
        } else if (stmt instanceof StmtFor stmtFor) {
            // 'for' '(' [ForStmt] ';' [Cond] ';' [forStmt] ')' Stmt
            visit(stmtFor.forStmt1);
            visit(stmtFor.cond);
            visit(stmtFor.forStmt3);
            loop++;
            visit(stmtFor.stmt);
            loop--;
        } else if (stmt instanceof StmtIf stmtIf) {
            // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            visit(stmtIf.cond);
            visit(stmtIf.stmt);
            if (stmtIf.elseStmt != null) {
                visit(stmtIf.elseStmt);
            }
        } else if (stmt instanceof StmtLValExp stmtLValExp) {
            // LVal '=' Exp ';'               -> Ident { '[' Exp ']' } '=' Exp ';'
            // | LVal '=' 'getint''('')'';'   -> Ident { '[' Exp ']' } '=' 'getint' '('...
            // | [Exp] ';'                    -> Ident { '[' Exp ']' } '+-*/%' || ';' || '(' Exp ')'... || Number || '+'|'-'|'!'
            switch (stmtLValExp.type) {
                case LVALEXP -> {
                    visit(stmtLValExp.lVal, true);
                    visit(stmtLValExp.exp);
                }
                case GETINT -> visit(stmtLValExp.lVal, true);
                case EXP -> {
                    if (stmtLValExp.exp != null) {
                        visit(stmtLValExp.exp);
                    }
                }
            }
        } else if (stmt instanceof StmtPrint stmtPrint) {
            //'printf''('FormatString{','Exp}')'';'
            stmtPrint.expList.forEach(this::visit);
        } else if (stmt instanceof StmtReturn stmtReturn) {
            //'return' [Exp] ';'
            if (stmtReturn.exp != null) {
                if (curFuncReturnType == ValueTypeEnum.VOID) {
                    OutputHelper.addError(ErrorType.VOID_RETURN, stmtReturn.returnToken.lineNum(), "'return' with a value, in function returning void");
                }
                visit(stmtReturn.exp);
                irManager.addInstruction(new RetInst(stmtReturn.exp.operand));
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
                case PLUS -> {
                    unaryExp.operand = unaryExp.unaryExp.operand;
                }
                case MINU -> {
                    result.value = -result.value;
                    unaryExp.operand = irManager.allocTempOperand(Value.IRValueType.I32);
                    if (!result.isConst) { // 常量优化
                        irManager.addInstruction(new SubInst(unaryExp.operand, Value.IRValueType.I32, new ConstantOperand(0), unaryExp.unaryExp.operand));
                    }
                }
                case NOT -> {
                    if (result.value != 0) {
                        result.value = 0;
                    } else {
                        result.value = 1;
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + op);
            }
            return result;
        } else if (unaryExp.ident != null) {
            // Ident '(' [FuncRParams] ')'
            var identToken = unaryExp.ident.token();
            var funcSymbol = symbolManager.findFuncSymbol(identToken.content());
            if (!symbolManager.isFuncSymbolDefined(identToken.content())) {
                OutputHelper.addError(ErrorType.IDENT_UNDEFINED, identToken.lineNum(), "function '" + identToken.content() + "' undefined");
                funcSymbol = new FuncSymbol(ValueTypeEnum.VOID, new ArrayList<>());
            }

            if (unaryExp.funcRParams != null) {
                var resultList = visit(unaryExp.funcRParams);
                if (funcSymbol.paramVarSymbolList().size() != resultList.size()) {
                    // 参数数量错误
                    OutputHelper.addError(ErrorType.FUNC_PARAM_NUM_ERROR, identToken.lineNum(), "too few or many arguments to function '" + identToken.content() + "'");
                } else {
                    // 参数数量正确
                    for (int i = 0; i < resultList.size(); i++) {
                        var funcFParamValueType = funcSymbol.paramVarSymbolList().get(i).valueType;
                        var funcRParamValueType = resultList.get(i).valueType;
                        if (!funcFParamValueType.isFParamToRParamValid(funcRParamValueType)) { // ValueType.equals()方法重写了，考虑了[][3]的情况
                            // 参数类型错误
                            OutputHelper.addError(ErrorType.FUNC_PARAM_TYPE_ERROR, identToken.lineNum(),
                                    "expected '" + funcFParamValueType.type() + funcFParamValueType.shape() + "' but argument is of type '" + funcRParamValueType.type() + funcRParamValueType.shape() + "'");
                        }
                    }
                }
            }
            return new VisitResult(new ValueType(funcSymbol.valueTypeEnum(), new ArrayList<>()), false, 0);
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

        VisitResult initValResult;
        if (varDef.initVal() != null) { // 如果有初始值，特别是全局变量中，其一定为constinitval
            visit(varDef.initVal(), shape, values);
        }

        var valueType = new ValueType(valueTypeEnum, shape);
        VarSymbol varSymbol = new VarSymbol(valueType, false, new ArrayList<>());
        symbolManager.addVarSymbol(identToken.content(), varSymbol);
        if (irManager.isInGlobal()) {
            irManager.addGlobalVar(identToken.content(), shape, values);
        } else {
            varSymbol.operand = irManager.allocTempOperand(Value.IRValueType.I32);
            irManager.addInstruction(new AllocaInst(varSymbol.operand, valueType));

        }
    }
}
