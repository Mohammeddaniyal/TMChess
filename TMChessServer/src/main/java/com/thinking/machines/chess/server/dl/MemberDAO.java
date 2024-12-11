package com.thinking.machines.chess.server.dl;
import java.util.*;
public class MemberDAO
{
public List<MemberDTO> getAll()
{
List<MemberDTO> members=new LinkedList<>();
MemberDTO member=new MemberDTO();
member.username="Azka";
member.password="azka";
members.add(member);
member=new MemberDTO();
member.username="Daniyal";
member.password="daniyal";
members.add(member);
member=new MemberDTO();
member.username="Hasnain";
member.password="hasnain";
members.add(member);
member=new MemberDTO();
member.username="Halima";
member.password="halima";
members.add(member);
member=new MemberDTO();
member.username="Madiha";
member.password="madiha";
members.add(member);
member=new MemberDTO();
member.username="Rehan";
member.password="rehan";
members.add(member);
member=new MemberDTO();
member.username="Izhan";
member.password="izhan";
members.add(member);
member=new MemberDTO();
return members;
}
}