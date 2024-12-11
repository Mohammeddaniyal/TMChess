package com.thinking.machines.chess.server;
import com.thinking.machines.nframework.server.*;
public class Main
{
public static void main(String args[])
{
NFrameworkServer server=new NFrameworkServer();
server.registerClass(TMChessServer.class);
server.start();
}
}