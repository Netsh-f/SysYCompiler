package Compiler.Parser;

import Compiler.Parser.Nodes.*;
import Compiler.Lexer.Token;
import Compiler.Parser.Nodes.Number;
import Compiler.Parser.Nodes.StmtNode.*;
import Enums.LexType;
import Enums.StmtLValExpType;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    private void next() {
        if (pos < tokens.size() - 1) {
            pos++;
        }
    }

    private LexType getLexType(int i) {
        if (pos + i < tokens.size()) {
            return tokens.get(pos + i).lexType();
        } else {
            return LexType.LEXER_END;
        }
    }

    private LexType getLexType() {
        if (pos < tokens.size()) {
            return tokens.get(pos).lexType();
        } else {
            return LexType.LEXER_END;
        }
    }

    private String getString() {
        if (pos < tokens.size()) {
            return tokens.get(pos).content();
        } else {
            return "";
        }
    }

    private AddExp addExp() {
        List<MulExp> mulExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        mulExpList.add(mulExp());
        while (getLexType() == LexType.PLUS || getLexType() == LexType.MINU) {
            opLexTypeList.add(getLexType());
            next();
            mulExpList.add(mulExp());
        }
        return new AddExp(mulExpList, opLexTypeList);
    }

    private BlockItem blockItem() {
        Decl decl = null;
        Stmt stmt = null;
        if (getLexType() == LexType.CONSTTK || getLexType() == LexType.INTTK) {
            decl = decl();
        } else {
            stmt = stmt();
        }
        return new BlockItem(decl, stmt);
    }

    private Block block() {
        List<BlockItem> blockItemList = new ArrayList<>();
        if (getLexType() == LexType.LBRACE) {
            next();
        }
        while (getLexType() != LexType.RBRACE) {
            blockItemList.add(blockItem());
        }
        if (getLexType() == LexType.RBRACE) {
            next();
        }
        return new Block(blockItemList);
    }

    private BType bType() {
        if (getLexType() == LexType.INTTK) {
            next();
        }
        return new BType();
    }

    private CompUnit compUnit() {
        List<Decl> declList = new ArrayList<>();
        List<FuncDef> funcDefList = new ArrayList<>();
        MainFuncDef mainFuncDef = null;
        while (getLexType() == LexType.VOIDTK ||
                (getLexType() == LexType.INTTK && getLexType(2) != LexType.LPARENT)) {
            declList.add(decl());
        }
        while (getLexType(2) == LexType.LPARENT) {
            funcDefList.add(funcDef());
        }
        if (getLexType(1) == LexType.MAINTK) {
            mainFuncDef = mainFuncDef();
        }
        return new CompUnit(declList, funcDefList, mainFuncDef);
    }

    private Cond cond() {
        return new Cond(lOrExp());
    }

    private ConstDecl constDecl() {
        BType bType = null;
        List<ConstDef> constDefList = new ArrayList<>();
        if (getLexType() == LexType.CONSTTK) {
            next();
        }
        bType = bType();
        constDefList.add(constDef());
        while (getLexType() == LexType.COMMA) {
            next();
            constDefList.add(constDef());
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        return new ConstDecl(bType, constDefList);
    }

    private ConstDef constDef() {
        Ident ident = null;
        List<ConstExp> constExpList = new ArrayList<>();
        ConstInitVal constInitVal = null;
        while (getLexType() == LexType.LBRACK) {
            next();
            constExpList.add(constExp());
            if (getLexType() == LexType.RBRACK) {
                next();
            }
        }
        if (getLexType() == LexType.ASSIGN) {
            next();
        }
        constInitVal = constInitVal();
        return new ConstDef(ident, constExpList, constInitVal);
    }

    private ConstExp constExp() {
        return new ConstExp(addExp());
    }

    private ConstInitVal constInitVal() {
        ConstExp constExp = null;
        List<ConstInitVal> constInitValList = new ArrayList<>();
        if (getLexType() != LexType.LBRACE) {
            constExp = constExp();
        } else {
            next();
            if (getLexType() != LexType.RBRACE) {
                constInitValList.add(constInitVal());
                while (getLexType() == LexType.COMMA) {
                    next();
                    constInitValList.add(constInitVal());
                }
            }
            if (getLexType() == LexType.RBRACE) {
                next();
            }
        }
        return new ConstInitVal(constExp, constInitValList);
    }

    private Decl decl() {
        ConstDecl constDecl = null;
        VarDecl varDecl = null;
        if (getLexType() == LexType.CONSTTK) {
            constDecl = constDecl();
        } else if (getLexType() == LexType.INTTK) {
            varDecl = varDecl();
        }
        return new Decl(constDecl, varDecl);
    }

    private EqExp eqExp() {
        List<RelExp> relExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        relExpList.add(relExp());
        while (getLexType() == LexType.EQL || getLexType() == LexType.NEQ) {
            opLexTypeList.add(getLexType());
            next();
            relExpList.add(relExp());
        }
        return new EqExp(relExpList, opLexTypeList);
    }

    private Exp exp() {
        return new Exp(addExp());
    }

    private ForStmt forStmt() {
        LVal lVal = lVal();
        if (getLexType() == LexType.ASSIGN) {
            next();
        }
        Exp exp = exp();
        return new ForStmt(lVal, exp);
    }

    private FuncDef funcDef() {
        FuncType funcType = funcType();
        Ident ident = ident();
        FuncFParams funcFParams = null;
        if (getLexType() == LexType.LPARENT) {
            next();
        }
        if (getLexType() != LexType.RPARENT) {
            funcFParams = funcFParams();
        }
        if (getLexType() == LexType.RPARENT) {
            next();
        }
        Block block = block();
        return new FuncDef(funcType, ident, funcFParams, block);
    }

    private FuncFParam funcFParam() {
        //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        BType bType = bType();
        Ident ident = ident();
        boolean isArray = false;
        List<ConstExp> constExpList = new ArrayList<>();
        if (getLexType() == LexType.LBRACK) {
            // ['[' ']' { '[' ConstExp ']' }] 存在
            isArray = true;
            next();
            if (getLexType() == LexType.RBRACK) {
                next();
            }
            while (getLexType() == LexType.LBRACK) {
                constExpList.add(constExp());
                if (getLexType() == LexType.RBRACK) {
                    next();
                }
            }
            if (getLexType() == LexType.RBRACK) {
                next();
            }
        }
        return new FuncFParam(bType, ident, isArray, constExpList);
    }

    private FuncFParams funcFParams() {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        List<FuncFParam> funcFParamList = new ArrayList<>();
        funcFParamList.add(funcFParam());
        while (getLexType() == LexType.COMMA) {
            next();
            funcFParamList.add(funcFParam());
        }
        return new FuncFParams(funcFParamList);
    }

    private FuncRParams funcRParams() {
        //FuncRParams → Exp { ',' Exp }
        List<Exp> expList = new ArrayList<>();
        expList.add(exp());
        while (getLexType() == LexType.COMMA) {
            next();
            expList.add(exp());
        }
        return new FuncRParams(expList);
    }

    private FuncType funcType() {
        //FuncType → 'void' | 'int'
        LexType lexType = null;
        if (getLexType() == LexType.VOIDTK || getLexType() == LexType.INTTK) {
            lexType = getLexType();
        }
        return new FuncType(lexType);
    }

    private Ident ident() {
        String content = null;
        if (getLexType() == LexType.IDENFR) {
            content = getString();
            next();
        }
        return new Ident(content);
    }

    private InitVal initVal() {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        Exp exp = null;
        List<InitVal> initValList = new ArrayList<>();
        if (getLexType() != LexType.LBRACE) {
            exp = exp();
        } else {
            initValList.add(initVal());
            while (getLexType() == LexType.COMMA) {
                next();
                initValList.add(initVal());
            }
        }
        return new InitVal(exp, initValList);
    }

    private LAndExp lAndExp() {
        //LAndExp → EqExp | LAndExp '&&' EqExp
        //改写为 LAndExp -> EqExp { '&&' EqExp }
        List<EqExp> eqExpList = new ArrayList<>();
        eqExpList.add(eqExp());
        while (getLexType() == LexType.AND) {
            next();
            eqExpList.add(eqExp());
        }
        return new LAndExp(eqExpList);
    }

    private LOrExp lOrExp() {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        //改写为 LOrExp -> LAndExp { '||' LAndExp }
        List<LAndExp> lAndExpList = new ArrayList<>();
        lAndExpList.add(lAndExp());
        while (getLexType() == LexType.OR) {
            next();
            lAndExpList.add(lAndExp());
        }
        return new LOrExp(lAndExpList);
    }

    private LVal lVal() {
        // LVal → Ident {'[' Exp ']'}
        Ident ident = ident();
        List<Exp> expList = new ArrayList<>();
        while (getLexType() == LexType.LBRACK) {
            next();
            expList.add(exp());
            if (getLexType() == LexType.RBRACK) {
                next();
            }
        }
        return new LVal(ident, expList);
    }

    private MainFuncDef mainFuncDef() {
        if (getLexType() == LexType.INTTK) {
            next();
        }
        if (getLexType() == LexType.MAINTK) {
            next();
        }
        if (getLexType() == LexType.LPARENT) {
            next();
        }
        if (getLexType() == LexType.RPARENT) {
            next();
        }
        return new MainFuncDef(block());
    }

    private MulExp mulExp() {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //改写为 MulExp -> UnaryExp { ('*' | '/' | '%') UnaryExp } 遍历时要改成原语法树的形状
        List<UnaryExp> unaryExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        unaryExpList.add(unaryExp());
        while (getLexType() == LexType.MULT || getLexType() == LexType.DIV || getLexType() == LexType.MOD) {
            opLexTypeList.add(getLexType());
            next();
            unaryExpList.add(unaryExp());
        }
        return new MulExp(unaryExpList, opLexTypeList);
    }

    private Number number() {
        //Number → IntConst
        int num = 0;
        if (getLexType() == LexType.INTCON) {
            try {
                num = Integer.parseInt(getString());
            } catch (NumberFormatException e) {
                num = 0;
            }
        }
        return new Number(num);
    }

    private PrimaryExp primaryExp() {
        //PrimaryExp → '(' Exp ')' | LVal | Number
        Exp exp = null;
        LVal lVal = null;
        Number number = null;
        if (getLexType() == LexType.LPARENT) {
            next();
            exp = exp();
            if (getLexType() == LexType.RPARENT) {
                next();
            }
        } else if (getLexType() == LexType.INTCON) {
            number = number();
        } else {
            lVal = lVal();
        }
        return new PrimaryExp(exp, lVal, number);
    }

    private RelExp relExp() {
        //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        //改写为 RelExp -> AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        List<AddExp> addExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        addExpList.add(addExp());
        while (getLexType() == LexType.LSS || getLexType() == LexType.LEQ ||
                getLexType() == LexType.GRE || getLexType() == LexType.GEQ) {
            opLexTypeList.add(getLexType());
            next();
            addExpList.add(addExp());
        }
        return new RelExp(addExpList, opLexTypeList);
    }

    private Stmt stmt() {
        //Stmt → LVal '=' Exp ';'
        //| [Exp] ';'
        //| Block
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [forStmt] ')' Stmt
        //| 'break' ';' | 'continue' ';'
        //| 'return' [Exp] ';'
        //| LVal '=' 'getint''('')'';'
        //| 'printf''('FormatString{','Exp}')'';'
        if (getLexType() == LexType.LBRACE) {
            return stmtBlock();
        } else if (getLexType() == LexType.IFTK) {
            return stmtIf();
        } else if (getLexType() == LexType.FORTK) {
            return stmtFor();
        } else if (getLexType() == LexType.BREAKTK) {
            return stmtBreak();
        } else if (getLexType() == LexType.CONTINUETK) {
            return stmtContinue();
        } else if (getLexType() == LexType.RETURNTK) {
            return stmtReturn();
        } else if (getLexType() == LexType.PRINTFTK) {
            return stmtPrint();
        } else {
            return stmtLValExp();
        }
    }

    private StmtBlock stmtBlock() {
        return new StmtBlock(block());
    }

    private StmtIf stmtIf() {
        // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        Stmt elseStmt = null;
        if (getLexType() == LexType.IFTK) {
            next();
        }
        if (getLexType() == LexType.LPARENT) {
            next();
        }
        Cond cond = cond();
        if (getLexType() == LexType.RPARENT) {
            next();
        }
        Stmt stmt = stmt();
        if (getLexType() == LexType.ELSETK) {
            // [ 'else' Stmt ]存在
            next();
            elseStmt = stmt();
        }
        return new StmtIf(cond, stmt, elseStmt);
    }

    private StmtFor stmtFor() {
        // 'for' '(' [ForStmt] ';' [Cond] ';' [forStmt] ')' Stmt
        ForStmt forStmt1 = null;
        Cond cond = null;
        ForStmt forStmt3 = null;
        if (getLexType() == LexType.FORTK) {
            next();
        }
        if (getLexType() == LexType.LPARENT) {
            next();
        }
        if (getLexType() != LexType.SEMICN) {
            //[ForStmt1]存在
            forStmt1 = forStmt();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        if (getLexType() != LexType.SEMICN) {
            //[Cond]存在
            cond = cond();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        if (getLexType() != LexType.RPARENT) {
            //[ForStmt3]存在
            forStmt3 = forStmt();
        }
        if (getLexType() == LexType.RPARENT) {
            next();
        }
        Stmt stmt = stmt();
        return new StmtFor(forStmt1, cond, forStmt3, stmt);
    }

    private StmtBreak stmtBreak() {
        if (getLexType() == LexType.BREAKTK) {
            next();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        return new StmtBreak();
    }

    private StmtContinue stmtContinue() {
        if (getLexType() == LexType.CONTINUETK) {
            next();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        return new StmtContinue();
    }

    private StmtReturn stmtReturn() {
        //'return' [Exp] ';'
        Exp exp = null;
        if (getLexType() == LexType.RETURNTK) {
            next();
        }
        if (getLexType() != LexType.SEMICN) {
            exp = exp();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        return new StmtReturn(exp);
    }

    private StmtPrint stmtPrint() {
        //'printf''('FormatString{','Exp}')'';'
        String formatString = null;
        List<Exp> expList = new ArrayList<>();
        if (getLexType() == LexType.PRINTFTK) {
            next();
        }
        if (getLexType() == LexType.LPARENT) {
            next();
        }
        if (getLexType() == LexType.STRCON) {
            formatString = getString();
            next();
        }
        while (getLexType() == LexType.COMMA) {
            next();
            expList.add(exp());
        }
        if (getLexType() == LexType.RPARENT) {
            next();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        return new StmtPrint(formatString, expList);
    }

    private StmtLValExp stmtLValExp() {
        // LVal '=' Exp ';'               -> Ident { '[' Exp ']' } '=' Exp ';'
        // | LVal '=' 'getint''('')'';'   -> Ident { '[' Exp ']' } '=' 'getint' '('...
        // | [Exp] ';'                    -> Ident { '[' Exp ']' } '+-*/%' || ';' || '(' Exp ')'... || Number || '+'|'-'|'!'
        StmtLValExpType type = StmtLValExpType.LVALEXP;
        Exp exp = null;
        LVal lVal = null;
        if (getLexType() == LexType.IDENFR) {
            //三种可能
            boolean isExp = true;
            // 在下一个分号之前找'='，如果有，则为前两种情况
            for (int i = 1; getLexType(i) != LexType.SEMICN && getLexType(i) != LexType.LEXER_END; i++) {
                if (getLexType(i) == LexType.ASSIGN) {
                    isExp = false;
                    break;
                }
            }
            if (isExp) {
                // 是 [Exp] ';' 且 Exp 存在
                type = StmtLValExpType.EXP;
                exp = exp();
                if (getLexType() == LexType.SEMICN) {
                    next();
                }
            } else {
                // LVal '=' Exp ';' 或者 LVal '=' 'getint''('')'';'
                lVal = lVal();
                if (getLexType() == LexType.ASSIGN) {
                    next();
                }
                if (getLexType() == LexType.GETINTTK) {
                    // 是 LVal '=' 'getint''('')'';'
                    type = StmtLValExpType.GETINT;
                    next();
                    if (getLexType() == LexType.LPARENT) {
                        next();
                    }
                    if (getLexType() == LexType.RPARENT) {
                        next();
                    }
                    if (getLexType() == LexType.SEMICN) {
                        next();
                    }
                } else {
                    // 是 LVal '=' Exp ';'
                    type = StmtLValExpType.LVALEXP;
                    exp = exp();
                    if (getLexType() == LexType.SEMICN) {
                        next();
                    }
                }
            }
        } else {
            //只可能是 [Exp] ';'
            type = StmtLValExpType.EXP;
            if (getLexType() != LexType.SEMICN) {
                //[Exp]存在
                exp = exp();
            }
            if (getLexType() == LexType.SEMICN) {
                next();
            }
        }
        return new StmtLValExp(type, lVal, exp);
    }

    private UnaryExp unaryExp() {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'
        // | UnaryOp UnaryExp
        Ident ident = null;
        FuncRParams funcRParams = null;
        UnaryOp unaryOp = null;
        UnaryExp unaryExp = null;
        PrimaryExp primaryExp = null;
        if (getLexType(1) == LexType.LPARENT) {
            //UnaryExp → Ident '(' [FuncRParams] ')'
            ident = ident();
            if (getLexType() == LexType.LPARENT) {
                next();
            }
            if (getLexType() != LexType.RPARENT) {
                //[FuncRParams]存在
                funcRParams = funcRParams();
            }
            if (getLexType() == LexType.RPARENT) {
                next();
            }
        } else if (getLexType() == LexType.PLUS || getLexType() == LexType.MINU || getLexType() == LexType.NOT) {
            //UnaryExp → UnaryOp UnaryExp
            unaryOp = unaryOp();
            unaryExp = unaryExp();
        } else {
            //UnaryExp → PrimaryExp
            primaryExp = primaryExp();
        }
        return new UnaryExp(primaryExp, ident, funcRParams, unaryOp, unaryExp);
    }

    private UnaryOp unaryOp() {
        LexType type = LexType.LEXER_ERROR;
        if (getLexType() == LexType.PLUS || getLexType() == LexType.MINU || getLexType() == LexType.NOT) {
            type = getLexType();
            next();
        }
        return new UnaryOp(type);
    }

    private VarDecl varDecl() {
        //VarDecl → BType VarDef { ',' VarDef } ';'
        BType bType = bType();
        List<VarDef> varDefList = new ArrayList<>();
        varDefList.add(varDef());
        while (getLexType() == LexType.COMMA) {
            next();
            varDefList.add(varDef());
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        }
        return new VarDecl(bType, varDefList);
    }

    private VarDef varDef() {
        //VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        Ident ident = ident();
        List<ConstExp> constExpList = new ArrayList<>();
        InitVal initVal = null;
        while (getLexType() == LexType.LBRACK) {
            next();
            constExpList.add(constExp());
            if (getLexType() == LexType.RBRACK) {
                next();
            }
        }
        if (getLexType() == LexType.ASSIGN) {
            next();
            initVal = initVal();
        }
        return new VarDef(ident, constExpList, initVal);
    }


}
