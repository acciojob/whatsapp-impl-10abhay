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
    private HashMap<Integer,String> messages;
    HashSet<User> userDb;


    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();

        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
        this.messages = new HashMap<Integer, String>();
        this.userDb = new HashSet<>();

    }
    public String createUser(String name,String mobile) {

        try {
            if (userMobile.contains(mobile)) {
                throw new RuntimeException("User already exists");
            }
            userMobile.add(mobile);
        }
        catch (RuntimeException e) {
            return e.getMessage();
        }
        User user = new User(name,mobile);
        userMobile.add(mobile);
        userDb.add(user);

        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        int n= users.size();
        if(n<2){return null;}
        if(n==2){
            Group group=new Group(users.get(1).getName(),2);
            groupUserMap.put(group,users);
            adminMap.put(group,users.get(1));
            return group;
            }
        customGroupCount++;

        Group group=new Group("Group"+customGroupCount,n);
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));

        return group;
        }

    public int createMessage(String content){
        messageId++;
        messages.put(messageId,content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) {
        if(!groupUserMap.containsKey(group)){return -1;}
        List<User> user=groupUserMap.get(group);
        for(User users:user){
            if(!sender.equals(user)){return -2;}
        }
        List<Message> messag=groupMessageMap.get(message);
        messag.add(message);
        groupMessageMap.put(group,messag);

        messageId++;
        messages.put(messageId,message.getContent());
        return messageId;
    }

    public String changeAdmin(User approver, User user, Group group) {
        if(!groupUserMap.containsKey(group)){return "Group does not exist";}
        if(!adminMap.get(group).equals(approver)){return "Approver does not have rights";}
        List<User> users=groupUserMap.get(group);
        for(User use:users){
            if(use.equals(user)){adminMap.put(group,use);}
            return "SUCCESS";
        }
        return "User is not a participant";
    }

    public int removeUser(User user){
        for (Group group : groupUserMap.keySet()){
            for (User users : groupUserMap.get(group)){
                if (users.equals(user)){
                    for (User admin : adminMap.values()){
                        if (admin.equals(user)){
                            return -2;
                        }
                    }
                    for (Message message : senderMap.keySet()){
                        if (senderMap.get(message).equals(user)){
                            senderMap.remove(message);
                            groupMessageMap.get(group).remove(message);
                            userDb.remove(user);
                        }
                        groupUserMap.get(group).remove(user);
                        group.setNumberOfParticipants(group.getNumberOfParticipants()-1);
                        return messageId + groupMessageMap.get(group).size()+groupUserMap.get(group).size();
                    }
                }
            }
        }
        return -1;
    }
    public String findMessage(Date start, Date end, int K){
        if (messages.size() < K){
            return null;
        }
        return messages.get(K);
    }




    }
