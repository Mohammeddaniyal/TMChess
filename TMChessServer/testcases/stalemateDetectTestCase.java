import com.thinking.machines.chess.server.logic.*;
import com.thinking.machines.chess.server.models.*;
class eg2
{
public static void main(String gg[])
{
byte board[][] = {
    {6, 0, 0, 0, 0, 0, 0, 0},
 {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {1, 0, 0, 0, 0, 5, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, -6}
};

Game game=new Game();
game.board=board;
game.whiteKingCastling=new KingCastling();
game.blackKingCastling=new KingCastling();
System.out.println("Stalemate checkmate : "+StalemateDetector.detectStalemate(game,(byte)0));

}
}