package com.thinking.machines.chess.server;
import com.thinking.machines.chess.server.handler.MoveHandler;
import com.thinking.machines.chess.server.models.Game;
import com.thinking.machines.chess.server.models.KingCastling;
import com.thinking.machines.chess.server.logic.BoardInitializer;
import com.thinking.machines.chess.common.*;
import com.thinking.machines.nframework.server.*;
import com.thinking.machines.nframework.server.annotations.*;
import com.thinking.machines.chess.server.dl.*;
import java.util.*;
@Path("/TMChessServer")
public class TMChessServer
{
static private final long TIMEOUT_DURATION=30*1000; //30 seconds in millisecond
static private Map<String,Member> members;
static private Set<String> loggedInMembers;
static private Set<String> playingMembers;
static private Map<String,List<Message>> inboxes;
static private Map<String,Message> invitationsTimeout;
static private Map<String,List<String>> userExpiredInvitations;
static private Map<String,GameInit> gameInits;
static private Map<String,Game> games;
static private final byte WHITE=1;
static private final byte BLACK=0;
static
{
populateDataStructures();
}
public TMChessServer()
{
}
static private void populateDataStructures()
{
MemberDAO memberDAO=new MemberDAO();
List<MemberDTO> dlMembers=memberDAO.getAll();
Member member;
members=new HashMap<>();
for(MemberDTO memberDTO:dlMembers)
{
member=new Member();
member.username=memberDTO.username;
member.password=memberDTO.password;
members.put(member.username,member);
}
loggedInMembers=new HashSet<>();
playingMembers=new HashSet<>();
inboxes=new HashMap<>();
invitationsTimeout=new HashMap<>();
userExpiredInvitations=new HashMap<>();
gameInits=new HashMap<>();
games=new HashMap<>();
}
@Path("/authenticateMember")
public boolean isMemberAuthentic(String username,String password)
{
Member member=members.get(username);
if(member==null) return false;
boolean b=password.equals(member.password);
if(b)
{
loggedInMembers.add(username);
}
return b;
}
@Path("/logout")
public void logout(String username)
{
loggedInMembers.remove(username);
}
@Path("/getMembers")
public List<MemberInfo> getMembers(String username)
{
List<MemberInfo> membersInfo=new LinkedList<>();
MemberInfo memberInfo;
String u;
//determining the status of each member (by using wisely other two sets[playingMember and loggedInMembers]) and adding into the list

for(var entry:members.entrySet())
{
//getting username from map
u=entry.getKey();

//no need to add that user who asked or called this method getMembers 
//exclude this user
if(u.equals(username)) continue;

memberInfo=new MemberInfo();
memberInfo.member=u;
//player is online
if(loggedInMembers.contains(u)) memberInfo.status=PLAYER_STATUS_TYPE.ONLINE;
//player is online but in game
else if(playingMembers.contains(u)) memberInfo.status=PLAYER_STATUS_TYPE.IN_GAME;
//player is offline
else memberInfo.status=PLAYER_STATUS_TYPE.OFFLINE;
membersInfo.add(memberInfo);
}

for(String u:loggedInMembers)
{
if(playingMembers.contains(u)==false && u.equals(username)==false) availableMembers.add(u);
}
return availableMembers;
}
@Path("/inviteUser")
public void inviteUser(String fromUsername,String toUsername)
{
Message message=new Message();
message.fromUsername=fromUsername;
message.toUsername=toUsername;
message.type=MESSAGE_TYPE.CHALLENGE;
message.inviteTimeStamp=System.currentTimeMillis();
this.invitationsTimeout.put(fromUsername,message);
List<Message> messages=inboxes.get(toUsername);
if(messages==null)
{
messages=new LinkedList<Message>();
inboxes.put(toUsername,messages);
}
messages.add(message);
}
@Path("/invitationReply")
public void invitationReply(Message m)
{
Message message=new Message();
message.fromUsername=m.fromUsername;
message.toUsername=m.toUsername;
message.type=m.type;
List<Message> messages=inboxes.get(message.toUsername);
if(messages==null)
{
messages=new LinkedList<>();
this.inboxes.put(message.toUsername,messages);
}
messages.add(message);
String fromUsername=message.toUsername;
String toUsername=message.fromUsername;
if(message.type==MESSAGE_TYPE.CHALLENGE_ACCEPTED)
{
//and remove from logged in members
loggedInMembers.remove(fromUsername);
loggedInMembers.remove(toUsername);
// add both this player into playingMembers set
playingMembers.add(fromUsername);
playingMembers.add(toUsername);

notifyNonPlayingMembers();

String uuid=UUID.randomUUID().toString();
Random random=new Random();
//decide player color

byte playerColor1=(byte)random.nextInt(2); // Generates 0 or 1 (player [fromUsername])
byte playerColor2=(playerColor1==WHITE?BLACK:WHITE); // (player [toUsername])

//create Game object(for this session of game of this two players)
Game game=new Game();
game.id=uuid;
game.player1=fromUsername;
game.player2=toUsername;
game.board=BoardInitializer.initializeBoard();
//decide which will be the first to play
//since WHITE represent 1 and BLACK represent 0
game.activePlayer=(byte)random.nextInt(2); //Generates 0 or 1 
game.moves=new LinkedList<Move>();

game.whiteKingCastling=new KingCastling();
game.blackKingCastling=new KingCastling();

this.games.put(uuid,game);

GameInit gameInit=new GameInit();
gameInit.gameId=uuid;
gameInit.playerColor=playerColor1;
gameInit.board=BoardInitializer.initializeBoard();
//putting player 1
this.gameInits.put(fromUsername,gameInit);


//player 2
gameInit=new GameInit();
gameInit.gameId=uuid;
gameInit.playerColor=playerColor2;
gameInit.board=BoardInitializer.initializeBoard();
//putting player 2
this.gameInits.put(toUsername,gameInit);
}
message=this.invitationsTimeout.get(fromUsername);
if(message==null) return;
if(message.toUsername.equals(toUsername))
{
this.invitationsTimeout.remove(fromUsername);
}
}
@Path("/getMessages")
public List<Message> getMessages(String username)
{
//System.out.println("get messages called");
List<Message> messages=inboxes.get(username);
if(messages!=null && messages.size()>0)
{
inboxes.put(username,new LinkedList<Message>());
}
//if(messages!=null)System.out.println("returning messages "+messages.size());
//else System.out.println("returning messages ");
return messages;
}
@Path("/expiredInvitations")
public List<String> getExpiredInvitations(String username)
{
List<String> invitationsExpiredOf=this.userExpiredInvitations.get(username);
if(invitationsExpiredOf!=null && invitationsExpiredOf.size()>0)
{
this.userExpiredInvitations.put(username,new LinkedList<>());
}
return invitationsExpiredOf;
}
@Path("/getInvitationStatus")
public Message getInvitationStatus(String fromUsername,String toUsername)
{
Message message=this.invitationsTimeout.get(fromUsername);
if(message!=null)
{
long sentTime=message.inviteTimeStamp;
long currentTime=System.currentTimeMillis();
if(currentTime-sentTime>=TIMEOUT_DURATION)
{
// add the expired invitation to userExpiredInvitation map
List<String> invitationsFrom=userExpiredInvitations.get(toUsername);
if(invitationsFrom==null)
{
invitationsFrom=new LinkedList<>();
this.userExpiredInvitations.put(toUsername,invitationsFrom);
}
invitationsFrom.add(fromUsername);
//player ignored the invitation
//remove the message from invitationsTimeout
this.invitationsTimeout.remove(fromUsername);
message=new Message();
message.fromUsername=toUsername;
message.toUsername=fromUsername;
message.type=MESSAGE_TYPE.CHALLENGE_IGNORED;

return message;
}
}// if the user didn't respond to the invitation then this part of ignored invitation
return null;
}
@Path("/getGameInit")
public GameInit getPlayerIdentity(String username)
{
GameInit gameInit=null;
while(true)
{
gameInit=this.gameInits.get(username);
if(gameInit==null)
{
try
{
Thread.sleep(500);
}catch(Exception e){}
continue;
}
break;
}
if(gameInit!=null)this.gameInits.remove(username);
return gameInit;
}
@Path("/canIPlay")
public boolean canIPlay(String gameId,byte playerColor)
{
Game game=games.get(gameId);
if(game==null) 
{
System.out.println("game is null");
return false;
}

return game.activePlayer==playerColor;
}
@Path("/getPossibleMoves")
public byte[][] getPossibleMoves(String gameId,byte fromX,byte fromY)
{
Game game=games.get(gameId);
if(game==null) return new byte[8][8];
return MoveHandler.getPossibleMoves(game,fromX,fromY);
}
@Path("/submitMove")
public MoveResponse submitMove(Move m,String gameId)
{
Game game=games.get(gameId);
if(game==null) return null;
Move move=new Move();
move.player=m.player;
move.piece=m.piece;
move.fromX=m.fromX;
move.fromY=m.fromY;
move.toX=m.toX;
move.toY=m.toY;
move.isLastMove=m.isLastMove;
move.castlingType=m.castlingType;
move. pawnPromotionTo=m. pawnPromotionTo;
MoveResponse moveResponse=MoveHandler.validateMove(game,move);
if(moveResponse.isValid==0) return moveResponse;
byte isLastMove=MoveHandler.detectCheckmate(game);
System.out.println("IS LAST MOVE : "+isLastMove);
move.isLastMove=isLastMove;
moveResponse.isLastMove=isLastMove;
//update the move in list
game.moves.add(move);
//switch the player
byte playerColor=move.player;
game.activePlayer=(byte)((playerColor==1)?0:1);
return moveResponse;
}
@Path("/getOpponentMove")
public Move getOpponentMove(String gameId,byte playerColor)
{
Game game=games.get(gameId);
if(game==null) return null;
if(game.activePlayer!=playerColor)
{
return null;
}
int size=game.moves.size();
int lastMoveIndex=(size>0)?size-1:0;
return game.moves.get(lastMoveIndex);
}
}
