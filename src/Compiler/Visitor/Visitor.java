/*
@Time    : 2023/10/9 15:18
@Author  : Elaikona
*/
package Compiler.Visitor;

import Compiler.Parser.Nodes.*;
import Compiler.Parser.Nodes.Number;
import Compiler.SymbolManager.Symbol.VarSymbol;
import Compiler.SymbolManager.Symbol.VarSymbolVal;
import Compiler.SymbolManager.SymbolManager;

import java.util.ArrayList;

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

    private int visit(AddExp addExp) {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        // 改写为 AddExp -> MulExp { ('+' | '−') MulExp }
        int ret = visit(addExp.mulExpList().get(0));
        for (int i = 0; i < addExp.opLexTypeList().size(); i++) {
            int mulExpVal = visit(addExp.mulExpList().get(i + 1));
            switch (addExp.opLexTypeList().get(i)) {
                case PLUS -> ret += mulExpVal;
                case MINU -> ret -= mulExpVal;
                default -> throw new IllegalStateException("Unexpected value: " + addExp.opLexTypeList().get(i));
            }
        }
        return ret;
    }

    private void visit(BlockItem blockItem) {
    }

    private void visit(Block block) {
    }

    private void visit(BType bType) {
    }


    private void visit(CompUnit compUnit) {
        //CompUnit → {Decl} {FuncDef} MainFuncDef
        compUnit.declList().forEach(this::visit);
        compUnit.funcDefList().forEach(this::visit);
        visit(compUnit.mainFuncDef());
    }

    private void visit(Cond cond) {
    }

    private void visit(ConstDecl constDecl) {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        visit(constDecl.bType());
        constDecl.constDefList().forEach(this::visit);
    }

    private void visit(ConstDef constDef) {
        // ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        var lenList = new ArrayList<Integer>();
        constDef.constExpList().forEach(constExp -> {
            lenList.add(visit(constExp));
        });
        var initVal = visit(constDef.constInitVal());

        if (lenList.size() == initVal.dimension()) {
            symbolManager.addVarSymbol(constDef.ident().content(), new VarSymbol(true, initVal));
        } else {
            //维度不匹配
        }
    }

    private int visit(ConstExp constExp) {
        return visit(constExp.addExp());
    }

    private VarSymbolVal visit(ConstInitVal constInitVal) {
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
    }

    private void visit(Exp exp) {
    }

    private void visit(ForStmt forStmt) {
    }

    private void visit(FuncDef funcDef) {
    }

    private void visit(FuncFParam funcFParam) {
    }

    private void visit(FuncFParams funcFParams) {
    }

    private void visit(FuncRParams funcRParams) {
    }

    private void visit(FuncType funcType) {
    }

    private void visit(Ident ident) {
    }

    private void visit(InitVal initVal) {
    }

    private void visit(LAndExp lAndExp) {
    }

    private void visit(LOrExp lOrExp) {
    }

    private void visit(LVal lVal) {
    }

    private void visit(MainFuncDef mainFuncDef) {
    }

    private int visit(MulExp mulExp) {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //改写为 MulExp -> UnaryExp { ('*' | '/' | '%') UnaryExp }
        int ret = visit(mulExp.unaryExpList().get(0));
        for (int i = 0; i < mulExp.opLexTypeList().size(); i++) {
            int unaryExpVal = visit(mulExp.unaryExpList().get(i + 1));
            switch (mulExp.opLexTypeList().get(i)) {
                case MULT -> ret *= unaryExpVal;
                case DIV -> ret /= unaryExpVal;
                case MOD -> ret %= unaryExpVal;
                default -> throw new IllegalStateException("Unexpected value: " + mulExp.opLexTypeList().get(i));
            }
        }
        return ret;
    }

    private void visit(Number number) {
    }

    private void visit(PrimaryExp primaryExp) {
    }

    private void visit(RelExp relExp) {
    }

    private void visit(Stmt stmt) {
    }

    private int visit(UnaryExp unaryExp) {
    }

    private void visit(UnaryOp unaryOp) {
    }

    private void visit(VarDecl varDecl) {
    }

    private void visit(VarDef varDef) {
    }

}
