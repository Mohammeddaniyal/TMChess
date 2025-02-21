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
@Path("/getInvitationStatus")
public Message getInvitationStatus(String fromUsername,String toUsername)
{
//done done
Message message=this.invitationsTimeout.get(fromUsername);
long sentTime=message.inviteTimeStamp;
long currentTime=System.currentTimeMillis();
if(currentTime-sentTime>=TIMEOUT_DURATION)
{
//player ignored the invitation
//remove the message from invitationsTimeout
this.invitationsTimeout.remove(fromUsername);
message=new Message();
message.fromUsername=toUsername;
message.toUsername=fromUsername;
message.type=MESSAGE_TYPE.CHALLENGE_IGNORED;
return message;
}
return null;
}

public String getGameId(String username)
{
return "abc";
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