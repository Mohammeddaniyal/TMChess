package com.thinking.machines.chess.server;
import com.thinking.machines.nframework.server.*;
import com.thinking.machines.nframework.server.annotations.*;
import com.thinking.machines.chess.server.dl.*;
import java.util.*;
@Path("/TMChessServer")
public class TMChessServer
{
static private Map<String,Member> members;
static private Set<String> loggedInMembers;
static private Set<String> playingMembers;
static private Map<String,List<Message>> inboxes;
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
public void inviteUser(String fromUsername,String toUsername)
{
Message message=new Message();
message.fromUsername=fromUsername;
message.toUsername=toUsername;
message.type=MESSAGE_TYPE.CHALLENGE;
List<Message> messages=inboxes.get(toUsername);
if(messages==null)
{
messages=new LinkedList<Message>();
inboxes.put(toUsername,messages);
}
messages.add(message);
}
public List<Message> getMessages(String username)
{
List<Message> messages=inboxes.get(username);
if(messages!=null && messages.size()>0)
{
inboxes.put(username,new LinkedList<Message>());
}
return messages;
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