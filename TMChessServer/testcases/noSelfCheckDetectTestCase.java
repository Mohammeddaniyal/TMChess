import com.thinking.machines.chess.server.logic.*;
class eg3
{
public static void main(String gg[])
{
byte board[][]={
{-4, -2, -3, -5, 0, 0, -2, -4},
{-1, -1, -1, -1, 0, 0, -1, -1},
{0, 0, 0, 0, 0, 0, 0, -6},
{0, 0, 0, 0, 0, 5, 0, 0},
{0, 0, 3, 1, 1, 0, 0, 0},
{0, 0, 0, 0, 0, 0, 0, 0},
{1, 1, 1, 0, 0, 1, 1, 1},
{4, 2, 3, 0, 6, 0, 0, 4}
};

System.out.println("No self rule : "+CheckmateDetector.isMoveValid(board,(byte)1,(byte)3,(byte)2,(byte)3));
System.out.println("No self rule: "+CheckmateDetector.isMoveValid(board,(byte)0,(byte)6,(byte)2,(byte)7));

System.out.println("No self rule: "+CheckmateDetector.isMoveValid(board,(byte)1,(byte)6,(byte)2,(byte)6));
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
System.out.print(board[e][f]+",");
}
System.out.println("");
}
System.out.println("No self rule: "+CheckmateDetector.isMoveValid(board,(byte)1,(byte)6,(byte)3,(byte)6));
for(int e=0;e<8;e++)
{
for(int f=0;f<8;f++)
{
System.out.print(board[e][f]+",");
}
System.out.println("");
}

System.out.println("No self rule: "+CheckmateDetector.isMoveValid(board,(byte)2,(byte)6,(byte)1,(byte)5));


}
}
