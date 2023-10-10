/*
@Time    : 2023/10/9 15:18
@Author  : Elaikona
*/
package Compiler.Visitor;

import Compiler.Lexer.LexType;
import Compiler.Parser.Nodes.*;
import Compiler.Parser.Nodes.Number;
import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.ReturnType;
import Compiler.SymbolManager.Symbol.VarSymbol;
import Compiler.SymbolManager.Symbol.VarSymbolVal;
import Compiler.SymbolManager.SymbolManager;
import Utils.Error.ErrorType;
import Utils.OutputHelper;

import java.util.ArrayList;
import java.util.List;

public class Visitor {
    private CompUnit unit;
    private SymbolManager symbolManager;

    public Visitor(CompUnit compUnit) {
        this.unit = compUnit;
        this.symbolManager = new SymbolManager();
    }

    public void run() {
        visit(this.unit);
    }

    private VisitResult visit(AddExp addExp) {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        // 改写为 AddExp -> MulExp { ('+' | '−') MulExp }
        boolean isConst = true;
        int value = 0;
        var result = visit(addExp.mulExpList().get(0));
        if (result.isConst) {
            value = result.value;
        }
        for (int i = 0; i < addExp.opLexTypeList().size(); i++) {
            result = visit(addExp.mulExpList().get(1 + i));
            if (isConst && result.isConst) {
                switch (addExp.opLexTypeList().get(i)) {
                    case PLUS -> value += result.value;
                    case MINU -> value -= result.value;
                    default -> throw new IllegalStateException("Unexpected value: " + addExp.opLexTypeList().get(i));
                }
            } else {
                isConst = false;
            }
        }
        if (isConst) {
            return new VisitResult(ReturnType.INT, true, value);
        }
        return new VisitResult(ReturnType.INT, false);
    }

    private void visit(BlockItem blockItem) {
        //  BlockItem → Decl | Stmt
        if (blockItem.decl() != null) {
            visit(blockItem.decl());
        } else if (blockItem.stmt() != null) {
            visit(blockItem.stmt());
        }
        // error
    }

    private void visit(Block block) {
        // Block → '{' { BlockItem } '}'
        block.blockItemList().forEach(this::visit);
    }

    private void visit(BType bType) {
        // do nothing
    }


    private void visit(CompUnit compUnit) {
        //CompUnit → {Decl} {FuncDef} MainFuncDef
        compUnit.declList().forEach(this::visit);
        compUnit.funcDefList().forEach(this::visit);
        visit(compUnit.mainFuncDef());
    }

    private void visit(Cond cond) {
        // Cond → LOrExp
        visit(cond.lOrExp());
    }

    private void visit(ConstDecl constDecl) {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        visit(constDecl.bType());
        constDecl.constDefList().forEach(this::visit);
    }

    private void visit(ConstDef constDef) {
        // ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        var lenList = new ArrayList<Integer>();

        var identToken = constDef.ident().token();
        if (symbolManager.isVarSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "redefinition of '" + identToken.content() + "'");
            return;
        }

        constDef.constExpList().forEach(constExp -> {
            lenList.add(visit(constExp).value);
        });
        var initVal = visit(constDef.constInitVal());

        if (lenList.size() == initVal.dimension()) {
            if (initVal.dimension() == 0
                    || (initVal.dimension() == 1 && lenList.get(0) == initVal.len1())
                    || (initVal.dimension() == 2 && lenList.get(0) == initVal.len1() && lenList.get(1) == initVal.len2())) {
                symbolManager.addVarSymbol(constDef.ident().token().content(), new VarSymbol(true, initVal));
            }
            // 长度不匹配
        }
        // 维度不匹配
    }

    private VisitResult visit(ConstExp constExp) {
        return visit(constExp.addExp());
    }

    private VarSymbolVal visit(ConstInitVal constInitVal) {
        //  ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if (constInitVal.constExp() != null) {
            var result = visit(constInitVal.constExp());
            return new VarSymbolVal(0, 0, 0, new int[][]{{result.value}});
        } else if (!constInitVal.constInitValList().isEmpty()) {
            var varSymbolValList = new ArrayList<VarSymbolVal>();
            for (var civ : constInitVal.constInitValList()) {
                varSymbolValList.add(visit(civ));
            }
            if (varSymbolValList.get(0).dimension() == 0) {
                int[] val1D = new int[varSymbolValList.size()];
                for (int i = 0; i < varSymbolValList.size(); i++) {
                    val1D[i] = varSymbolValList.get(i).val()[0][0];
                }
                return new VarSymbolVal(1, varSymbolValList.size(), 0, new int[][]{val1D});
            } else if (varSymbolValList.get(0).dimension() == 1) {
                int[][] val2D = new int[varSymbolValList.size()][];
                for (int i = 0; i < varSymbolValList.size(); i++) {
                    val2D[i] = varSymbolValList.get(i).val()[0];
                }
                // 默认第二维长度均相等
                return new VarSymbolVal(2, varSymbolValList.size(), val2D[0].length, val2D);
            }
            // 没有三维及以上
        }
        // error
        return null;
    }

    private void visit(Decl decl) {
        // Decl → ConstDecl | VarDecl
        if (decl.constDecl() != null) {
            visit(decl.constDecl());
        } else if (decl.varDecl() != null) {
            visit(decl.varDecl());
        }
    }

    private void visit(EqExp eqExp) {
        // EqExp → RelExp | { ('==' | '!=') RelExp }
        eqExp.relExpList().forEach(this::visit);
    }

    private VisitResult visit(Exp exp) {
        //  Exp → AddExp
        return visit(exp.addExp());
    }

    private void visit(ForStmt forStmt) {
        // ForStmt → LVal '=' Exp

    }

    private void visit(FuncDef funcDef) {
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        var returnType = visit(funcDef.funcType());

        var identToken = funcDef.ident().token();
        if (symbolManager.isFuncSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "function redefinition of '" + identToken.content() + "'");
        }

        symbolManager.createSymbolTable();

        var varSymbolList = visit(funcDef.funcFParams());
        symbolManager.addFuncSymbol(identToken.content(), new FuncSymbol(returnType, varSymbolList));
        visit(funcDef.block());

        symbolManager.backward();
    }

    private VarSymbol visit(FuncFParam funcFParam) {
        // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        visit(funcFParam.bType());
        var identToken = funcFParam.ident().token();
        VarSymbol varSymbol = null;

        if (symbolManager.isVarSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "redefinition of parameter '" + identToken.content() + "'");
            return null;
        }

        if (!funcFParam.isArray()) {
            // 不是数组
            varSymbol = new VarSymbol(false, new VarSymbolVal(0, 0, 0, new int[][]{{}}));
        } else if (funcFParam.constExpList().isEmpty()) {
            varSymbol = new VarSymbol(false, new VarSymbolVal(1, 0, 0, new int[][]{{}}));
        } else if (funcFParam.constExpList().size() == 1) {
            var result = visit(funcFParam.constExpList().get(0));
            varSymbol = new VarSymbol(false, new VarSymbolVal(2, 0, result.value, new int[][]{{}}));
        }
        // 更多维
        symbolManager.addVarSymbol(identToken.content(), varSymbol);
        return varSymbol;
    }

    private List<VarSymbol> visit(FuncFParams funcFParams) {
        // FuncFParams → FuncFParam { ',' FuncFParam }
        var varSymbolList = new ArrayList<VarSymbol>();
        funcFParams.funcFParamList().forEach(funcFParam -> {
            varSymbolList.add(visit(funcFParam));
        });
        return varSymbolList;
    }

    private void visit(FuncRParams funcRParams) {
    }

    private ReturnType visit(FuncType funcType) {
        if (funcType.type() == LexType.INTTK) {
            return ReturnType.INT;
        } else if (funcType.type() == LexType.VOIDTK) {
            return ReturnType.VOID;
        }
        return ReturnType.VOID;
    }

    private void visit(InitVal initVal) {
    }

    private void visit(LAndExp lAndExp) {
    }

    private void visit(LOrExp lOrExp) {
    }

    private VisitResult visit(LVal lVal) {
        //  LVal → Ident {'[' Exp ']'}
        var identToken = lVal.ident().token();
        var varSymbol = symbolManager.findVarSymbol(identToken.content());
        if (varSymbol == null) {
            OutputHelper.addError(ErrorType.IDENT_UNDEFINED, identToken.lineNum(), "'" + identToken.content() + "' undeclared");
            return new VisitResult(ReturnType.VOID);
        } else if (!varSymbol.isConst()) {
            // 是变量
            lVal.expList().forEach(this::visit);
            return new VisitResult(ReturnType.INT);
        } else {
            // 是常量
            if (lVal.expList().isEmpty() && varSymbol.varSymbolVal().dimension() == 0) {
                //0维
                return new VisitResult(ReturnType.INT, true, varSymbol.varSymbolVal().val()[0][0]);
            } else if (lVal.expList().size() == 1 && varSymbol.varSymbolVal().dimension() == 1) {
                //1维
                var expResult = visit(lVal.expList().get(0));
                // TODO 越界检查
                return new VisitResult(ReturnType.INT, true, varSymbol.varSymbolVal().val()[0][expResult.value]);
            } else if (lVal.expList().size() == 2 && varSymbol.varSymbolVal().dimension() == 2) {
                //2维
                var expResult1 = visit(lVal.expList().get(0));
                var expResult2 = visit(lVal.expList().get(1));
                // TODO 越界检查
                return new VisitResult(ReturnType.INT, true, varSymbol.varSymbolVal().val()[expResult1.value][expResult2.value]);
            }
        }
        return null;
    }

    private void visit(MainFuncDef mainFuncDef) {
    }

    private VisitResult visit(MulExp mulExp) {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //改写为 MulExp -> UnaryExp { ('*' | '/' | '%') UnaryExp }
        boolean isConst = true;
        int value = 0;
        var result = visit(mulExp.unaryExpList().get(0));
        if (result.isConst) {
            value = result.value;
        }
        for (int i = 0; i < mulExp.opLexTypeList().size(); i++) {
            result = visit(mulExp.unaryExpList().get(1 + i));
            if (result.isConst && isConst) {
                switch (mulExp.opLexTypeList().get(i)) {
                    case MULT -> value *= result.value;
                    case DIV -> value /= result.value;
                    case MOD -> value %= result.value;
                    default -> throw new IllegalStateException("Unexpected value: " + mulExp.opLexTypeList().get(i));
                }
            } else {
                isConst = false;
            }
        }
        if (isConst) {
            return new VisitResult(ReturnType.INT, true, value);
        }
        return new VisitResult(ReturnType.INT, false);
    }

    private VisitResult visit(Number number) {
        return new VisitResult(ReturnType.INT, true, number.intConst());
    }

    private VisitResult visit(PrimaryExp primaryExp) {
        // PrimaryExp → '(' Exp ')' | LVal | Number
        if (primaryExp.exp() != null) {
            return visit(primaryExp.exp());
        } else if (primaryExp.lVal() != null) {
            return visit(primaryExp.lVal());
        } else if (primaryExp.number() != null) {
            return visit(primaryExp.number());
        }
        return null;
    }

    private void visit(RelExp relExp) {
    }

    private void visit(Stmt stmt) {
    }

    private VisitResult visit(UnaryExp unaryExp) {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp //  UnaryOp → '+' | '−' | '!'
        if (unaryExp.primaryExp() != null) {
            return visit(unaryExp.primaryExp());
        } else if (unaryExp.unaryOp() != null && unaryExp.unaryExp() != null) {
            var op = visit(unaryExp.unaryOp());
            var result = visit(unaryExp.unaryExp());
            if (result.isConst) {
                switch (op) {
                    case PLUS -> result.value = +result.value;
                    case MINU -> result.value = -result.value;
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
            } else {
                return new VisitResult(ReturnType.INT, false);
            }
        } else if (unaryExp.ident() != null) {
            // Ident '(' [FuncRParams] ')'
            if (unaryExp.funcRParams() != null) {
                visit(unaryExp.funcRParams());
            }
            // TODO 如果Ident函数为void则报错
            return new VisitResult(ReturnType.INT, false);
        }
        return null;
    }

    private LexType visit(UnaryOp unaryOp) {
        return unaryOp.opType();
    }

    private void visit(VarDecl varDecl) {
    }

    private void visit(VarDef varDef) {
        // VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义
        // | Ident { '[' ConstExp ']' } '=' InitVal
        var lenList = new ArrayList<Integer>();

        var identToken = varDef.ident().token();
        if (symbolManager.isVarSymbolDefined(identToken.content())) {
            OutputHelper.addError(ErrorType.IDENT_REDEFINED, identToken.lineNum(), "redeclaration of '" + identToken.content() + "'");
            return;
        }

        varDef.constExpList().forEach(constExp -> {
            lenList.add(visit(constExp).value);
        });

        if (varDef.initVal() != null) {
            visit(varDef.initVal());
        }

        // '=' InitVal 未处理，非常量无需存储值
        symbolManager.addVarSymbol(identToken.content(),
                new VarSymbol(false,
                        new VarSymbolVal(lenList.size(),
                                lenList.size() > 0 ? lenList.get(0) : 0,
                                lenList.size() > 1 ? lenList.get(1) : 0, new int[][]{{}})));
    }

}
