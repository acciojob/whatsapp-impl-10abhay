package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name,String number) throws Exception {
        User user=new User(name,number);
        if(userMobile.contains(number)){
            throw new Exception("User already exists");
        }
        userMobile.add(number);
        return"SUCCESS";
    }

    public Group createGroup(List<User> users) {
        int n=users.size();
        if(n==2){
            Group group=new Group(users.get(1).getName(),n);
            groupUserMap.put(group,users);
            // adminMap.put(group,users.get(0));
            return group;
        }
        customGroupCount+=1;
        Group group=new Group("Group "+customGroupCount,n);
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }

    public int createMessage(String content) {
        messageId+=1;
        Message message=new Message(messageId,content,new Date());
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!groupUserMap.get(group).contains(sender)){
            throw new Exception("You are not allowed to send message");
        }
        senderMap.put(message,sender);
        if(!groupMessageMap.containsKey(group)){
            List<Message> list=new ArrayList<>();
            list.add(message);
            groupMessageMap.put(group,list);
        }
        else {
            List<Message> list=groupMessageMap.get(group);
            list.add(message);
            groupMessageMap.put(group,list);
        }
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!adminMap.get(group).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        if(!groupUserMap.get(group).contains(user)){
            throw new Exception("User is not a participant");
        }
        adminMap.put(group,user);
        return"SUCCESS";
    }
}