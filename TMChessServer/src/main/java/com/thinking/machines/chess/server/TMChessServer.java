package com.thinking.machines.chess.server;
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
static private Map<String,PlayerIdentity> playerIdentities;
static private Map<String,Game> games;
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
playerIdentities=new HashMap<>();
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
public List<String> getAvailableMembers(String username)
{
List<String> availableMembers=new LinkedList<>();
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
String uuid=UUID.randomUUID().toString();
PlayerIdentity playerIdentity=new PlayerIdentity();
playerIdentity.gameId=uuid;
boolean isWhite=new Random().nextBoolean();
String playerColor=isWhite?"White":"Black";
playerIdentity.playerColor=playerColor;
//player 1
this.playerIdentities.put(fromUsername,playerIdentity);
playerIdentity=new PlayerIdentity();
playerIdentity.gameId=uuid;
playerIdentity.playerColor=isWhite?"Black":"White";
//player 2
this.playerIdentities.put(toUsername,playerIdentity);
}
message=this.invitationsTimeout.get(fromUsername);
if(message==null) return;
if(message.toUsername==toUsername)
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
@Path("/getPlayerIdentity")
public PlayerIdentity getPlayerIdentity(String username)
{
PlayerIdentity playerIdentity=this.playerIdentities.get(username);
if(playerIdentity!=null)this.playerIdentities.remove(username);
return playerIdentity;
}
public boolean canIPlay(String gameId,String username)
{
return false;
}
public void submitMove(String byUsername,byte piece,int fromX,int fromY,int toX,int toY)
{
}
public Move getOpponentsMove(String username)
{
return null;
}
}