package Utils.Error;

public enum ErrorType {
    FORMAT_STRING_ERROR,
    //<FormatString> → ‘“‘{<Char>}’”

    IDENT_REDEFINED,
    //<ConstDef>→<Ident> …
    //<VarDef>→<Ident> … <Ident> …
    //<FuncDef>→<FuncType><Ident> …
    //<FuncFParam> → <BType> <Ident> …

    IDENT_UNDEFINED,
    //<LVal>→<Ident> …
    //<UnaryExp>→<Ident> …

    FUNC_PARAM_NUM_ERROR,
    //<UnaryExp>→<Ident>‘(’[<FuncRParams>]‘)’

    FUNC_PARAM_TYPE_ERROR,
    //<UnaryExp>→<Ident>‘(’[<FuncRParams>]‘)’

    VOID_RETURN,
    //<Stmt>→‘return’ {‘[’<Exp>’]’}‘;’

    NO_RETURN,
    //<FuncDef> → <FuncType> <Ident> ‘(’ [<FuncFParams>] ‘)’ <Block>
    //<MainFuncDef> → ‘int’ ‘main’ ‘(’ ‘)’ <Block>

    MODIFY_CONST,
    //<Stmt>→<LVal>‘=’ <Exp>‘;’
    //<Stmt>→<LVal>‘=’ ‘getint’ ‘(’ ‘)’ ‘;’

    MISSING_SEMICN,
    //<Stmt>,<ConstDecl>及<VarDecl>中的’;’

    MISSING_RPARENT,
    //函数调用(<UnaryExp>)、函数定义(<FuncDef>)及<Stmt>中的’)’

    MISSING_RBRACK,
    //数组定义(<ConstDef>,<VarDef>,<FuncFParam>)和使用(<LVal>)中的’]’

    PRINTF_EXP_NUM_ERROR,
    //<Stmt> →‘printf’‘(’<FormatString>{,<Exp>}’)’‘;’

    BREAK_CONTINUE_ERROR,
    //<Stmt>→‘break’‘;’
    //<Stmt>→‘continue’‘;’
}
