/*
@Time    : 2023/9/29 20:32
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Parser.Nodes.Block;
import Compiler.Parser.Nodes.Stmt;

public class StmtBlock extends Stmt {
    public Block block;

    public StmtBlock(Block block) {
        this.block = block;
    }
}
