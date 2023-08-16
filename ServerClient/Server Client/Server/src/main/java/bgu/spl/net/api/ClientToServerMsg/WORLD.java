package bgu.spl.net.api.ClientToServerMsg;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WORLD {
    private  static WORLD single_instance = new WORLD();
    public LinkedList<String> postsAndPMs;
    public ConcurrentHashMap<String,User> users;
    public ConcurrentHashMap<Integer,String> onlineUsers;
    public ConcurrentHashMap<String, Integer> connections;
    public ArrayList<String> connect;


    private final String[] filteredWords = {"harassment", "racism", "murder"};
    public WORLD(){
        postsAndPMs = new LinkedList<>();
        users = new ConcurrentHashMap<>();
        onlineUsers = new ConcurrentHashMap<>();
        connections =new ConcurrentHashMap<>();
        connect=new ArrayList<>();
    }

    public static WORLD getInstance(){
        return single_instance;
    }

    public ConcurrentHashMap<Integer,String> getonlineUsers() {
        return onlineUsers;
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    public ConcurrentHashMap<String, Integer> getConnections() {
        return connections;
    }

    public boolean register(String message, int connectionId) {
        String[] reg = message.split(" ");
        String username = reg[0];
        if(users.containsKey(username))
            return false;
        int year = Integer.parseInt(reg[2]);
        if(year > 2021)
            return false;
        User newUser = new User(reg[0], reg[1], reg[2], connectionId);
//        System.out.println("username = " + reg[0] + ", password = " + reg[1] + " birthday = "+ reg[2]);
        users.put(username, newUser);
        connections.put(username, connectionId);
        return true;
    }

    public boolean login(String message, int connectionId) {
        String[] login = message.split(" ");
        String username = login[0];
        String password = login[1];
        String captcha = login[2];
        if((!captcha.equals("0")) && (!onlineUsers.containsKey(connectionId) && (!onlineUsers.containsValue(username)))){
            if(users.containsKey(username)){
                if(users.get(username).getPassword().equals(password)){
                    onlineUsers.put(connectionId , username);
                    return true;
                }
            }
        }
        return false;
    }
    //     if(onlineUsers.containsKey(connectionId)){
    //         System.out.println("already online");
    //         return false;
    //     }
        
    //     if(captcha == "0" || !(users.containsKey(username))){
    //         System.out.println("I don't belong here or captcha is 0");
    //         return false;
    //     }
    //     User thisUser = users.get(username);
    //     if(!(thisUser.getPassword().equals(password))){
    //         System.out.println("the correct password is:" + thisUser.getPassword() + ".");
    //         System.out.println("the wrong password is:" + password + ".");
    //         return false;
    //     }
    //     onlineUsers.put(connectionId, username);
    //     return true;
        
    // }

    public boolean logout(int connectionId) {
        if(onlineUsers.containsKey(connectionId)){
          onlineUsers.remove(connectionId); //check this method
          return true;
        }
        return false;
    }

    public boolean follow(String message, int connectionId) {
        if (!(onlineUsers.containsKey(connectionId))) {
            return false;
        }

        String[] splitted = message.split(" ");
        String followOrNot = splitted[0];
        if (followOrNot.equals("0")) {
            // wants to follow.
            String theFollowerName = onlineUsers.get(connectionId);
            if(!users.containsKey(theFollowerName) || (!users.containsKey(splitted[1])))
                return false;
            User theFollower = users.get(theFollowerName);
            User theOneToFollow = users.get(splitted[1]);
            if(theFollower.getUsername().equals(theOneToFollow.getUsername()))
                return false;
            boolean r1 = !(theFollower.follow(theOneToFollow));
            boolean r2 = !(theOneToFollow.followedBy(theFollower));
            return (r1 && r2);
        }
        else{
            // wants to unfollow.
            String theUnfollowerName = onlineUsers.get(connectionId);
            User theUnfollower = users.get(theUnfollowerName);
            User theOneToUnfollow = users.get(splitted[1]);
            boolean r1 = theUnfollower.unfollow(theOneToUnfollow);
            boolean r2 = theOneToUnfollow.unfollowedBy(theUnfollower);
            return (r1 && r2);
        }
    }

    public boolean post(String message, int connectionId) {
        if(!(onlineUsers.containsKey(connectionId)))
            return false;
        String theSenderUsername = onlineUsers.get(connectionId);
        User theSender = users.get(theSenderUsername);
        postsAndPMs.add(message);
        theSender.sentPost();
        boolean taggedPeople = message.contains("@");
        if(taggedPeople){
            ArrayList<String> splitMsg = split(message);
            for(int i = 0; i < splitMsg.size(); i++){
                String username = splitMsg.get(i);
                if(!users.containsKey(username))
                    return false;
                User user = users.get(username);
                if(theSender.isNotFollowing(user))
                    user.sendPost(message);
            }
        }
        return true;
    }

    public ArrayList<String> split(String message) {
        ArrayList<String> answer = new ArrayList<>();
        boolean iAmOnItNow = false;
        String userName = "";
        for (int i = 0; i < message.length(); i++) {
            if (iAmOnItNow) {
                if (message.charAt(i) == ' ') {
                    iAmOnItNow = false;
                    answer.add(userName);
                    userName = "";
                }
                userName += message.charAt(i);
            }
            if (message.charAt(i) == '@') {
                iAmOnItNow = true;
            }
        }
        return answer;
    }

    public boolean pm(String message, int connectionId) {
        if(!(onlineUsers.containsKey(connectionId)))
            return false;
        String[] splitted = message.split(" ");
        int splittedLength = splitted.length;
        String username = splitted[0];
        User sentTo = users.get(username);
        if(sentTo == null) // reciepent user isn' registed.
            return false;
        String content = "";
        for(int i = 1; i < splittedLength; i++) {
            for (String word: filteredWords){
                if(splitted[i].equals(word))
                    splitted[i] = "<filtered>";
            }
            if(i > 1)
                content += " " + splitted[i] ;
            else
                content+= splitted[i];
        }

        postsAndPMs.add(content);
        sentTo.sendPM(content);
        return true;
    }

    public boolean logstat(int connectionId) {
        if(!(onlineUsers.containsKey(connectionId)))
            return false;
        User me = users.get(onlineUsers.get(connectionId));
        String output = "";
        for (String username : onlineUsers.values() ) {
            User user = users.get(username);
            if(!me.isBlocked(user)) {
                String theUsersInfo = user.toString_();
                output += theUsersInfo + "\n";
            }
        }
        return true;
    }

    public boolean stat(String message, int connectionId) {
        if(!(onlineUsers.containsKey(connectionId))) {
            return false;
        }
        String[] usernames = message.split("\\|");
        for(int i = 0; i < usernames.length; i++){
            if(usernames[i].contains(" ")) {
                String[] theUsername = usernames[i].split(" ");
                usernames[i] = theUsername[0];
            }
            if(!users.containsKey(usernames[i]))
                return false;
            User user = users.get(usernames[i]);
            if(user == null) {
                return false;
            }

        }
        return true;
    }

    public String getSendTo(String message) {
        int firstSpace = message.indexOf(" ");
        return message.substring(0, firstSpace);
    }

    public boolean block(String message, int connectionId) {
        String myUsername = onlineUsers.get(connectionId);
//        System.out.println("(WORLD - BLOCK) << message = " + message);
        User me = users.get(myUsername);
        String[] username = message.split(" ");
        String usernameToBlock = username[0];
        if(!users.containsKey(usernameToBlock)) {
//            System.out.println("he's not a user.");
            return false;
        }
        User userToBlock = users.get(usernameToBlock);
        if(userToBlock.isBlocked(me)) {
//            System.out.println("already blocked.");
            return false;
        }
        userToBlock.block(me);
        me.block(userToBlock);
        return true;
    }

//
//    private boolean loggedIn()
//
//    private void sendNotification(String notification, List<Integer> usersID, List<String> usersNames){
//        if(usersID!=null) {
//            for (int i = 0; i < onlineUsers.size(); i++) {
//                    connect.send(usersID.get(i), "9" + " "+ notification);
//                }
//                else
//                    manager.pushNotification(usersNames.get(i),notification);
//            }
//        }
//    }
//    private void sendNotificationToOne(String notification,int userID ,String userName) {
//        if (manager.loggedIn(userName)) {
//            connections.send(userID, "9" + " " + notification);
//        } else {
//            manager.pushNotification(userName, notification);
//        }
//    }
//    private void sendACK(short opcode,String ACKMess) {
//        connections.send(connectionID, "10" + " " + opcode + ACKMess);
//    }
//
//    private void sendError(short opcode){
//        connections.send(connectionID,"11"+" "+opcode);
//    }

//    public boolean notification(String message, int connectionId) {
//        String[] splitted = message.split(" ");
//        String theSenderName = splitted[1];
//        User sender = users.get(theSenderName);
//        if(splitted[0] == "0"){
//
//        }
//        else if(splitted[0] == "1"){
//            String content = "";
//            for(int i = 0; i < splitted.length)
//            ArrayList<String> taggedPeople = split(message);
//
//            if(taggedPeople.size() == 0)
//                System.out.println("no tags.");
//            else{
//                for(int i = 0; i < taggedPeople.size(); i++){
//                    User sendTo = users.get(taggedPeople.get(i));
//                    if(sendTo.isNotFollowing(sender) || (!sendTo.isNotFollowing(sender) && (!onlineUsers.contains(sendTo)))){
//
//                    }
//                }
//
//            }
//
//        }
//        return false;
//    }
//
//    public void getOnlineUsers() {
//    }
}
