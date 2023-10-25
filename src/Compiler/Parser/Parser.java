package Compiler.Parser;

import Compiler.Parser.Nodes.*;
import Compiler.Lexer.Token;
import Compiler.Parser.Nodes.Number;
import Compiler.Parser.Nodes.StmtNode.*;
import Compiler.Lexer.LexType;
import Compiler.Parser.Nodes.StmtNode.StmtLValExpType;
import Compiler.SymbolManager.Symbol.ValueTypeEnum;
import Utils.Error.ErrorType;
import Utils.OutputHelper;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public CompUnit run() {
        return compUnit();
    }

    private void next() {
        OutputHelper.addParserOutput(tokens.get(pos).toString());
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

    private Token getToken() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return new Token(LexType.LEXER_END, 0, "");
    }

    private Token getPreToken() {
        if (pos -1 >= 0 && pos -1 < tokens.size()) {
            return tokens.get(pos -1);
        }
        return new Token(LexType.LEXER_END, 0, "");
    }

    private AddExp addExp() {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        // 改写为 AddExp -> MulExp { ('+' | '−') MulExp } 遍历时要改成原来的语法书形状
        List<MulExp> mulExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        var firstMulExp = mulExp();
        if(firstMulExp == null){
            return null;
        }
        mulExpList.add(firstMulExp);
        while (getLexType() == LexType.PLUS || getLexType() == LexType.MINU) {
            opLexTypeList.add(getLexType());
            OutputHelper.addParserOutput("<AddExp>");
            next();
            mulExpList.add(mulExp());
        }
        OutputHelper.addParserOutput("<AddExp>");
        return new AddExp(mulExpList, opLexTypeList);
    }

    private BlockItem blockItem() {
        //  BlockItem → Decl | Stmt
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
        // Block → '{' { BlockItem } '}'
        Token rBraceToken = null;
        List<BlockItem> blockItemList = new ArrayList<>();
        if (getLexType() == LexType.LBRACE) {
            next();
        }
        while (getLexType() != LexType.RBRACE) {
            blockItemList.add(blockItem());
        }
        if (getLexType() == LexType.RBRACE) {
            rBraceToken = getToken();
            next();
        }
        OutputHelper.addParserOutput("<Block>");
        return new Block(blockItemList, rBraceToken);
    }

    private BType bType() {
        //BType → 'int'
        if (getLexType() == LexType.INTTK) {
            next();
            return new BType(ValueTypeEnum.INT);
        }
        return new BType(ValueTypeEnum.VOID);
    }

    private CompUnit compUnit() {
        //CompUnit → {Decl} {FuncDef} MainFuncDef
        List<Decl> declList = new ArrayList<>();
        List<FuncDef> funcDefList = new ArrayList<>();
        MainFuncDef mainFuncDef = null;
        while (getLexType() == LexType.CONSTTK || (getLexType() == LexType.INTTK && getLexType(2) != LexType.LPARENT)) {
            declList.add(decl());
        }
        while (getLexType() == LexType.VOIDTK || (getLexType() == LexType.INTTK && getLexType(1) != LexType.MAINTK && getLexType(2) == LexType.LPARENT)) {
            funcDefList.add(funcDef());
        }
        if (getLexType(1) == LexType.MAINTK) {
            mainFuncDef = mainFuncDef();
        }
        OutputHelper.addParserOutput("<CompUnit>");
        return new CompUnit(declList, funcDefList, mainFuncDef);
    }

    private Cond cond() {
        var lOrExp = lOrExp();
        OutputHelper.addParserOutput("<Cond>");
        return new Cond(lOrExp);
    }

    private ConstDecl constDecl() {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        BType bType;
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
        } else {
            OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
        }
        OutputHelper.addParserOutput("<ConstDecl>");
        return new ConstDecl(bType, constDefList);
    }

    private ConstDef constDef() {
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        Ident ident;
        List<ConstExp> constExpList = new ArrayList<>();
        ConstInitVal constInitVal;
        ident = ident();
        while (getLexType() == LexType.LBRACK) {
            next();
            constExpList.add(constExp());
            if (getLexType() == LexType.RBRACK) {
                next();
            } else {
                OutputHelper.addError(ErrorType.MISSING_RBRACK, getPreToken().lineNum(), "expected ']'");
            }
        }
        if (getLexType() == LexType.ASSIGN) {
            next();
        }
        constInitVal = constInitVal();
        OutputHelper.addParserOutput("<ConstDef>");
        return new ConstDef(ident, constExpList, constInitVal);
    }

    private ConstExp constExp() {
        var addExp = addExp();
        OutputHelper.addParserOutput("<ConstExp>");
        return new ConstExp(addExp);
    }

    private ConstInitVal constInitVal() {
        // ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
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
        OutputHelper.addParserOutput("<ConstInitVal>");
        return new ConstInitVal(constExp, constInitValList);
    }

    private Decl decl() {
        // Decl → ConstDecl | VarDecl
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
        //  EqExp → RelExp | EqExp ('==' | '!=') RelExp
        List<RelExp> relExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        relExpList.add(relExp());
        while (getLexType() == LexType.EQL || getLexType() == LexType.NEQ) {
            opLexTypeList.add(getLexType());
            OutputHelper.addParserOutput("<EqExp>");
            next();
            relExpList.add(relExp());
        }
        OutputHelper.addParserOutput("<EqExp>");
        return new EqExp(relExpList, opLexTypeList);
    }

    private Exp exp() {
        var addExp = addExp();
        if(addExp == null){
            return null;
        }
        OutputHelper.addParserOutput("<Exp>");
        return new Exp(addExp);
    }

    private ForStmt forStmt() {
        LVal lVal = lVal();
        if (getLexType() == LexType.ASSIGN) {
            next();
        }
        Exp exp = exp();
        OutputHelper.addParserOutput("<ForStmt>");
        return new ForStmt(lVal, exp);
    }

    private FuncDef funcDef() {
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        FuncType funcType = funcType();
        Ident ident = ident();
        FuncFParams funcFParams = null;
        if (getLexType() == LexType.LPARENT) {
            next();
        }
        if (getLexType() != LexType.RPARENT && getLexType() != LexType.LBRACE) { // 可能没有右小括号，但Block的左大括号一定有，所以用这个来判断是否有形参
            funcFParams = funcFParams();
        }
        if (getLexType() == LexType.RPARENT) {
            next();
        } else {
            OutputHelper.addError(ErrorType.MISSING_RPARENT, getPreToken().lineNum(), "expected ')'");
        }
        Block block = block();
        OutputHelper.addParserOutput("<FuncDef>");
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
            } else {
                OutputHelper.addError(ErrorType.MISSING_RBRACK, getPreToken().lineNum(), "expected ']'");
            }
            while (getLexType() == LexType.LBRACK) {
                next();
                constExpList.add(constExp());
                if (getLexType() == LexType.RBRACK) {
                    next();
                } else {
                    OutputHelper.addError(ErrorType.MISSING_RBRACK, getPreToken().lineNum(), "expected ']'");
                }
            }
        }
        OutputHelper.addParserOutput("<FuncFParam>");
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
        OutputHelper.addParserOutput("<FuncFParams>");
        return new FuncFParams(funcFParamList);
    }

    private FuncRParams funcRParams() {
        //FuncRParams → Exp { ',' Exp }
        List<Exp> expList = new ArrayList<>();
        var firstExp = exp();
        if(firstExp == null){
            return null;
        }
        expList.add(firstExp);
        while (getLexType() == LexType.COMMA) {
            next();
            expList.add(exp());
        }
        OutputHelper.addParserOutput("<FuncRParams>");
        return new FuncRParams(expList);
    }

    private FuncType funcType() {
        //FuncType → 'void' | 'int'
        LexType lexType = null;
        if (getLexType() == LexType.VOIDTK || getLexType() == LexType.INTTK) {
            lexType = getLexType();
            next();
        }
        OutputHelper.addParserOutput("<FuncType>");
        return new FuncType(lexType);
    }

    private Ident ident() {
        if (getLexType() == LexType.IDENFR) {
            var token = getToken();
            next();
            return new Ident(token);
        } else {
            return null;
        }
    }

    private InitVal initVal() {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        Exp exp = null;
        List<InitVal> initValList = new ArrayList<>();
        if (getLexType() != LexType.LBRACE) {
            exp = exp();
        } else {
            next();
            if (getLexType() != LexType.RBRACE) {
                // [ InitVal { ',' InitVal } ]存在
                initValList.add(initVal());
                while (getLexType() == LexType.COMMA) {
                    next();
                    initValList.add(initVal());
                }
            }
            if (getLexType() == LexType.RBRACE) {
                next();
            }
        }
        OutputHelper.addParserOutput("<InitVal>");
        return new InitVal(exp, initValList);
    }

    private LAndExp lAndExp() {
        //LAndExp → EqExp | LAndExp '&&' EqExp
        //改写为 LAndExp -> EqExp { '&&' EqExp }
        List<EqExp> eqExpList = new ArrayList<>();
        eqExpList.add(eqExp());
        while (getLexType() == LexType.AND) {
            OutputHelper.addParserOutput("<LAndExp>");
            next();
            eqExpList.add(eqExp());
        }
        OutputHelper.addParserOutput("<LAndExp>");
        return new LAndExp(eqExpList);
    }

    private LOrExp lOrExp() {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        //改写为 LOrExp -> LAndExp { '||' LAndExp }
        List<LAndExp> lAndExpList = new ArrayList<>();
        lAndExpList.add(lAndExp());
        while (getLexType() == LexType.OR) {
            OutputHelper.addParserOutput("<LOrExp>");
            next();
            lAndExpList.add(lAndExp());
        }
        OutputHelper.addParserOutput("<LOrExp>");
        return new LOrExp(lAndExpList);
    }

    private LVal lVal() {
        // LVal → Ident {'[' Exp ']'}
        Ident ident = ident();
        if (ident == null) {
            return null;
        }
        List<Exp> expList = new ArrayList<>();
        while (getLexType() == LexType.LBRACK) {
            next();
            expList.add(exp());
            if (getLexType() == LexType.RBRACK) {
                next();
            } else {
                OutputHelper.addError(ErrorType.MISSING_RBRACK, getPreToken().lineNum(), "expected ']'");
            }
        }
        OutputHelper.addParserOutput("<LVal>");
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
        var block = block();
        OutputHelper.addParserOutput("<MainFuncDef>");
        return new MainFuncDef(block);
    }

    private MulExp mulExp() {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //改写为 MulExp -> UnaryExp { ('*' | '/' | '%') UnaryExp } 遍历时要改成原语法树的形状
        List<UnaryExp> unaryExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        var firstUnaryExp = unaryExp();
        if(firstUnaryExp == null){
            return null;
        }
        unaryExpList.add(firstUnaryExp);
        while (getLexType() == LexType.MULT || getLexType() == LexType.DIV || getLexType() == LexType.MOD) {
            opLexTypeList.add(getLexType());
            OutputHelper.addParserOutput("<MulExp>");
            next();
            unaryExpList.add(unaryExp());
        }
        OutputHelper.addParserOutput("<MulExp>");
        return new MulExp(unaryExpList, opLexTypeList);
    }

    private Number number() {
        //Number → IntConst
        int num = 0;
        if (getLexType() == LexType.INTCON) {
            try {
                num = Integer.parseInt(getString());
            } catch (NumberFormatException ignored) {
            } finally {
                next();
            }
        }
        OutputHelper.addParserOutput("<Number>");
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
            if(lVal == null){
                return null;
            }
        }
        OutputHelper.addParserOutput("<PrimaryExp>");
        return new PrimaryExp(exp, lVal, number);
    }

    private RelExp relExp() {
        //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        //改写为 RelExp -> AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        List<AddExp> addExpList = new ArrayList<>();
        List<LexType> opLexTypeList = new ArrayList<>();
        addExpList.add(addExp());
        while (getLexType() == LexType.LSS || getLexType() == LexType.LEQ || getLexType() == LexType.GRE || getLexType() == LexType.GEQ) {
            opLexTypeList.add(getLexType());
            OutputHelper.addParserOutput("<RelExp>");
            next();
            addExpList.add(addExp());
        }
        OutputHelper.addParserOutput("<RelExp>");
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
        var block = block();
        OutputHelper.addParserOutput("<Stmt>");
        return new StmtBlock(block);
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
        } else {
            OutputHelper.addError(ErrorType.MISSING_RPARENT, getPreToken().lineNum(), "expected ')'");
        }
        Stmt stmt = stmt();
        if (getLexType() == LexType.ELSETK) {
            // [ 'else' Stmt ]存在
            next();
            elseStmt = stmt();
        }
        OutputHelper.addParserOutput("<Stmt>");
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
        if (getLexType() != LexType.SEMICN) { // 按照题意来说，for内不会缺少;分号
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
        if (getLexType() == LexType.RPARENT) { // 题意表示，for不会缺少右括号
            next();
        }
        Stmt stmt = stmt();
        OutputHelper.addParserOutput("<Stmt>");
        return new StmtFor(forStmt1, cond, forStmt3, stmt);
    }

    private StmtBreak stmtBreak() {
        Token breakToken = null;
        if (getLexType() == LexType.BREAKTK) {
            breakToken = getToken();
            next();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        } else {
            OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
        }
        OutputHelper.addParserOutput("<Stmt>");
        return new StmtBreak(breakToken);
    }

    private StmtContinue stmtContinue() {
        Token continueToken = null;
        if (getLexType() == LexType.CONTINUETK) {
            continueToken = getToken();
            next();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        } else {
            OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
        }
        OutputHelper.addParserOutput("<Stmt>");
        return new StmtContinue(continueToken);
    }

    private StmtReturn stmtReturn() {
        //'return' [Exp] ';'
        Token returnToken = null;
        Exp exp = null;
        if (getLexType() == LexType.RETURNTK) {
            returnToken = getToken();
            next();
        }
        if (getLexType() != LexType.SEMICN) {
            exp = exp();
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        } else {
            OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
        }
        OutputHelper.addParserOutput("<Stmt>");
        return new StmtReturn(returnToken, exp);
    }

    private StmtPrint stmtPrint() {
        //'printf''('FormatString{','Exp}')'';'
        Token printfToken = new Token(LexType.LEXER_END, 0, "");
        String formatStringContent = null;
        var indexList = new ArrayList<Integer>();
        List<Exp> expList = new ArrayList<>();
        if (getLexType() == LexType.PRINTFTK) {
            printfToken = getToken();
            next();
        }
        if (getLexType() == LexType.LPARENT) {
            next();
        }
        if (getLexType() == LexType.STRCON) {
            formatStringContent = getString();
            for (int i = 0; i < formatStringContent.length() - 1; i++) {
                if (formatStringContent.charAt(i) == '%' && formatStringContent.charAt(i + 1) == 'd') {
                    indexList.add(i);
                }
            }
            next();
        }
        while (getLexType() == LexType.COMMA) {
            next();
            expList.add(exp());
        }
        if (indexList.size() != expList.size()) {
            OutputHelper.addError(ErrorType.PRINTF_EXP_NUM_ERROR, printfToken.lineNum(), "Format characters in printf do not match the number of expressions");
        }
        if (getLexType() == LexType.RPARENT) {
            next();
        } else {
            OutputHelper.addError(ErrorType.MISSING_RPARENT, getPreToken().lineNum(), "expected ')'");
        }
        if (getLexType() == LexType.SEMICN) {
            next();
        } else {
            OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
        }
        OutputHelper.addParserOutput("<Stmt>");
        return new StmtPrint(printfToken, new FormatString(formatStringContent, indexList), expList);
    }

    private StmtLValExp stmtLValExp() {
        // LVal '=' Exp ';'               -> Ident { '[' Exp ']' } '=' Exp ';'
        // | LVal '=' 'getint''('')'';'   -> Ident { '[' Exp ']' } '=' 'getint' '(' ')' ';'
        // | [Exp] ';'                    -> Ident { '[' Exp ']' } '+-*/%' || ';' || '(' Exp ')'... || Number || '+'|'-'|'!'
        StmtLValExpType type;
        Exp exp = null;
        LVal lVal = null;

        int curPos = pos;
        OutputHelper.parserOutputSwitch = false;
        OutputHelper.errorOutputSwitch = false;
        ident();
        while (getLexType() == LexType.LBRACK) {
            next();
            exp();
            if (getLexType() == LexType.RBRACK) {
                next();
            }
        }
        if (getLexType() == LexType.ASSIGN) {
            if (getLexType(1) == LexType.GETINTTK) {
                type = StmtLValExpType.GETINT;
            } else {
                type = StmtLValExpType.LVALEXP;
            }
        } else {
            type = StmtLValExpType.EXP;
        }
        pos = curPos;
        OutputHelper.parserOutputSwitch = true;
        OutputHelper.errorOutputSwitch = true;
        // 上面是预读取，其实进行了回溯

        switch (type) {
            case LVALEXP -> {
                lVal = lVal();
                if (getLexType() == LexType.ASSIGN) {
                    next();
                }
                exp = exp();
                if (getLexType() == LexType.SEMICN) {
                    next();
                } else {
                    OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
                }
            }
            case GETINT -> {
                lVal = lVal();
                if (getLexType() == LexType.ASSIGN) {
                    next();
                }
                if (getLexType() == LexType.GETINTTK) {
                    next();
                }
                if (getLexType() == LexType.LPARENT) {
                    next();
                }
                if (getLexType() == LexType.RPARENT) {
                    next();
                } else {
                    OutputHelper.addError(ErrorType.MISSING_RPARENT, getPreToken().lineNum(), "expected ')'");
                }
                if (getLexType() == LexType.SEMICN) {
                    next();
                } else {
                    OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
                }
            }
            case EXP -> {
                if (getLexType() != LexType.SEMICN) {
                    exp = exp();
                }
                if (getLexType() == LexType.SEMICN) {
                    next();
                } else {
                    OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
                }
            }
        }

        OutputHelper.addParserOutput("<Stmt>");
        return new StmtLValExp(type, lVal, exp);
    }

    private UnaryExp unaryExp() {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        Ident ident = null;
        FuncRParams funcRParams = null;
        UnaryOp unaryOp = null;
        UnaryExp unaryExp = null;
        PrimaryExp primaryExp = null;
        if (getLexType() == LexType.PLUS || getLexType() == LexType.MINU || getLexType() == LexType.NOT) {
            //UnaryExp → UnaryOp UnaryExp
            unaryOp = unaryOp();
            unaryExp = unaryExp();
        } else if (getLexType() == LexType.IDENFR && getLexType(1) == LexType.LPARENT) {
            //UnaryExp → Ident '(' [FuncRParams] ')'
            ident = ident();
            if (getLexType() == LexType.LPARENT) {
                next();
            }
            if (getLexType() != LexType.RPARENT) {
                //[FuncRParams]可能存在
                funcRParams = funcRParams(); //如果匹配不上就返回了一个null
            }
            if (getLexType() == LexType.RPARENT) {
                next();
            } else {
                OutputHelper.addError(ErrorType.MISSING_RPARENT, getPreToken().lineNum(), "expected ')'");
            }
        } else {
            //UnaryExp → PrimaryExp
            primaryExp = primaryExp();
            if(primaryExp == null){
                return null;
            }
        }
        OutputHelper.addParserOutput("<UnaryExp>");
        return new UnaryExp(primaryExp, ident, funcRParams, unaryOp, unaryExp);
    }

    private UnaryOp unaryOp() {
        LexType type = LexType.LEXER_ERROR;
        if (getLexType() == LexType.PLUS || getLexType() == LexType.MINU || getLexType() == LexType.NOT) {
            type = getLexType();
            next();
        }
        OutputHelper.addParserOutput("<UnaryOp>");
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
        } else {
            OutputHelper.addError(ErrorType.MISSING_SEMICN, getPreToken().lineNum(), "expected ';'");
        }
        OutputHelper.addParserOutput("<VarDecl>");
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
            } else {
                OutputHelper.addError(ErrorType.MISSING_RBRACK, getPreToken().lineNum(), "expected ']'");
            }
        }
        if (getLexType() == LexType.ASSIGN) {
            next();
            initVal = initVal();
        }
        OutputHelper.addParserOutput("<VarDef>");
        return new VarDef(ident, constExpList, initVal);
    }
}
