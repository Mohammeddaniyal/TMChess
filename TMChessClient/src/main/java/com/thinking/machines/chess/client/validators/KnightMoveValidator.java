package com.thinking.machines.chess.client.validators;
public class KnightMoveValidator
{
public static boolean validateMove(int startRowIndex,int startColumnIndex,int destinationRowIndex,int destinationColumnIndex)
{
//mandatoring knight to move in L-shape movement
int d1=(startRowIndex<destinationRowIndex)?destinationRowIndex-startRowIndex:startRowIndex-destinationRowIndex;
int d2=(startColumnIndex<destinationColumnIndex)?destinationColumnIndex-startColumnIndex:startColumnIndex-destinationColumnIndex;
if(d1!=1 && d1!=2) return false;
if(d1==1)//two square right or left and one square bottom or top
{
if(d2!=2) return false;
}else //two square top or bottom and one square left or right
if(d1==2)
{
if(d2!=1) return false;
}
return true;
}
}
