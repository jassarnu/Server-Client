package bgu.spl.net.api.bidi;

import bgu.spl.net.api.ClientToServerMsg.User;
import bgu.spl.net.api.ClientToServerMsg.WORLD;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolmpl implements BidiMessagingProtocol<String> {

    private boolean shouldTermiante ;
    private WORLD world ;
    private int connectionId;
    private Connections<String> connections;
    public BidiMessagingProtocolmpl(WORLD world){
        this.world=world;
    }
    
    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
        shouldTermiante = false;
        world =  WORLD.getInstance();

    }

    @Override
    public void process(String message) {
        String[] splitted = message.split(" ", 2);
        Short operation = Short.parseShort(splitted[0]);
        boolean response = false;
        message = splitted[1];
        if(operation == 1){
             response = world.register(message, connectionId);
            if(response)
                SendAckNORMAL(message, (short) 1);
            else
                SendError(message, (short) 1);

        }
        if(operation == 2){
            response = world.login(message, connectionId);
            if(response){
                String username = world.onlineUsers.get(connectionId);
                if(!(world.onlineUsers.containsKey(connectionId)))
                    SendError(message, (short) 2);
                SendAckNORMAL(message, (short) 2);
                User user = world.getUsers().get(username);
                while(!(user.getSendWhenOnline().isEmpty()) && user.getSendWhenOnline().containsKey("PM") && !(user.getSendWhenOnline().get("PM").isEmpty())){
                    String thePm = user.getSendWhenOnline().get("PM").poll();
                    connections.send(connectionId, thePm);
                }
                while((!user.getSendWhenOnline().isEmpty()) && user.getSendWhenOnline().containsKey("PUBLIC") && !user.getSendWhenOnline().get("PUBLIC").isEmpty()){
                    connections.send(connectionId,"NOTIFICATION Public "+ world.onlineUsers.get(connectionId) +" " + user.getSendWhenOnline().get("PUBLIC").poll());
                }
            }
            else{
                SendError(message, (short) 2);
            } 

        }
        if(operation == 3){
             response = world.logout(connectionId);
            if(response) {
                SendAckNORMAL(message, (short) 3);
                shouldTermiante = true;
                connections.disconnect(connectionId);
            
            }
            else
                SendError(message, (short) 3);
        }
        if(operation == 4){
             response = world.follow(message, connectionId);
            if(response) {
                String[] toGetUsername = splitted[1].split(" ");
                SendAckFollow(message, (short) 4, toGetUsername[1]);
            }
            else {
                SendError(message, (short) 4);
            }
        }
        if(operation == 5){
             response = world.post(message, connectionId);
            if(response) {
                User me =  world.users.get(world.onlineUsers.get(connectionId));
                boolean taggedPeople = message.contains("@");
                if(taggedPeople){
                    ArrayList<String> splitMsg = world.split(message);
                    for(int i = 0; i < splitMsg.size(); i++){
                        String username = splitMsg.get(i);
                        if(world.users.containsKey(username)){
                            User theTaggedOne = world.users.get(username);
                            if (me.isNotFollowing(theTaggedOne) && world.onlineUsers.containsValue(username)) {
                                connections.send(theTaggedOne.getConnectionId(), "NOTIFICATION Public " + me.getUsername() + " " + message);
                            }
                            else if ((me.isNotFollowing(theTaggedOne)) && (!world.onlineUsers.contains(theTaggedOne))) {
                                User usernameToSend = world.users.get(theTaggedOne.getUsername());
                                if(usernameToSend == null)
                                    System.out.println("It's null!!");
                                usernameToSend.getSendWhenOnline().putIfAbsent("PUBLIC", new ConcurrentLinkedQueue<>());
                                usernameToSend.addOnSendWhenOnline("PUBLIC", "NOTIFICATION Public " + me.getUsername() + " " + message);
                            }
                        }
                        else {
                            SendError(message, (short) 5);
                        }
                    }
                }
                SendAckNORMAL(message, (short) 5);
                String username = world.onlineUsers.get(connectionId);
                User user = world.getUsers().get(username);
                for(User follower : user.getFollowers()){
                    if(world.getonlineUsers().contains(follower)){
                        connections.send(follower.getConnectionId(), "NOTIFICATION Public "+username+" " + message);
                    }
                    else{
                        follower.getSendWhenOnline().putIfAbsent("PUBLIC", new ConcurrentLinkedQueue<>());
                        follower.addOnSendWhenOnline("PUBLIC","NOTIFICATION Public "+username+" " + message );
                    }

                }
            }
            else
                SendError(message, (short) 5);

        }
        if(operation == 6){
            boolean senError = true;
            response = world.pm(message, connectionId);
            String myUsername = world.onlineUsers.get(connectionId);
            User me = world.users.get(myUsername);
            String content = world.postsAndPMs.getLast();
            String hisUsername = world.getSendTo(message);
            User him = world.users.get(hisUsername);
            content = "NOTIFICATION PM "+ world.onlineUsers.get(connectionId) +" " + content;
            if(response & (!him.isBlocked(me)) & world.onlineUsers.containsValue(hisUsername)){
                connections.send(world.getConnections().get(hisUsername),content);
                senError = false;
            }
            else if (response & (!him.isBlocked(me)) & (!world.onlineUsers.containsValue(hisUsername))){
                User user = world.getUsers().get(hisUsername);
                user.getSendWhenOnline().putIfAbsent("PM" ,new ConcurrentLinkedQueue<>());
                user.addOnSendWhenOnline("PM", content);
                senError = false;
            }
            if(!senError)
                SendAckNORMAL(message, (short) 6);
            else
                SendError(message, (short) 6);
        }
        if(operation == 7){
            response = world.logstat( connectionId);
            if(response) {
                String myUsername = world.onlineUsers.get(connectionId);
                User me = world.users.get(myUsername);
                for (User user : world.users.values()){
                    String username = user.getUsername();
                    if(world.onlineUsers.containsValue(username)){
                        if(user.isBlocked(me))
                            SendError(message, (short) 7);
                        else
                            SendAckLOGSTAT(me.toString_());
                    }
                }
            }
            else
                SendError(message, (short) 7);
        }
        if(operation == 8) {
            response = world.stat(message, connectionId);
            String myUsername = world.onlineUsers.get(connectionId);
            User me = world.users.get(myUsername);
            if(response){
                String[] usernames = message.split("\\|");
                for (int i = 0; i < usernames.length; i++) {
                    if (usernames[i].contains(" ")){
                        String[] theUsername = usernames[i].split(" ");
                        usernames[i] = theUsername[0];
                    }
                    User user = world.getUsers().get(usernames[i]);
                    if (user.isBlocked(me))
                        SendError(message, (short) 8);
                    else {
                        String output = "ACK 8 ";
                        output += user.toString_();
                        SendAckSTAT(output);
                    }
                }

            }
            else
                SendError(message, (short) 8);
        }
        if(operation == 12){
             response = world.block(message, connectionId);
            if(response)
                SendAckNORMAL(message, (short) 12);
            else
                SendError(message, (short) 12);

        }

    }

    private  void SendAckSTAT(String message){
        connections.send(connectionId, message);
    }

    private void SendAckLOGSTAT(String message) {
        String log = "ACK 7 " + message;
        connections.send(connectionId,log);
    }


    private void SendAckFollow(String message, short i, String s) {
        String ack = "ACK " + i +" " + s ;
        connections.send(connectionId , ack);
    }

    private void SendError(String message,short opcode) {
        String err = "ERROR " +opcode;
        connections.send(connectionId,err);
    }

    private void SendAckNORMAL(String msg,short opcode) {
     String ack = "ACK "+ opcode;
     connections.send(connectionId,ack);
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTermiante;
    }


}

