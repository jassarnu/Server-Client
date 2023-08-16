package bgu.spl.net.api.ClientToServerMsg;
import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private String username;
    private String password;
    private String birthday;
    private long age;
    private LinkedList<User> followings;
    private LinkedList<User> followers;
    private ArrayList<String> posts;
    private ArrayList<String> pms = new ArrayList<>();
    private int numOfPosts;
    private ConcurrentHashMap<User, Integer> connections;
    private ArrayList<User> blocked = new ArrayList<>();
    private int connectionId;
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<String>> sendWhenOnline = new ConcurrentHashMap<>();

    public User(String username, String password, String birthday, int connectionId){
        String[] theUsersBirthday = birthday.split("-");
        int day = Integer.parseInt(theUsersBirthday[0]);
        int month = Integer.parseInt(theUsersBirthday[1]);
        int year = Integer.parseInt(theUsersBirthday[2]);
        LocalDate theBirthday = LocalDate.of(year, month, day);
        age = Period.between(theBirthday, LocalDate.now()).getYears();

        this.username = username;
        this.password = password;
        this.birthday = birthday;
        followers = new LinkedList<>();
        followings = new LinkedList<>();
        posts = new ArrayList<>();
        this.connectionId = connectionId;

    }

    public void addOnSendWhenOnline(String whereTo, String adding){
        sendWhenOnline.get(whereTo).add(adding);
    }

    public LinkedList<User> getFollowers() {
        return followers;
    }

    public LinkedList<User> getFollowings() {
        return followings;
    }

    public long getAge() {
        return age;
    }

    public int getNumOfFollowers(){
        return followers.size();
    }

    public int getNumOfFollowings(){
        return followings.size();
    }

    public int getNumOfPosts(){
        return posts.size();
    }

    public String getPassword() {
        return password;
    }
    public boolean follow(User user){
        if(followings.contains(user))
            return true;
        followings.add(user);
        return false;
    }
    public boolean followedBy(User theFollower) {
        if(followers.contains(theFollower))
            return true;
        followers.add(theFollower);
        return false;
    }

    public boolean unfollow(User theOneToUnfollow) {
        if(!(followings.contains(theOneToUnfollow)))
            return false;
        followings.remove(theOneToUnfollow);
        return true;
    }

    public boolean unfollowedBy(User theUnfollower) {
        if(!(followers.contains(theUnfollower))){
            return false;
        }
        followers.remove(theUnfollower);
        return true;
    }

    public void sendPost(String message) {
        System.out.println("post has been sent");
        posts.add(message);
    }

//    public void sendMsgToFollowers(String message) {
//        for(int i = 0; i < followers.size(); i++){
//            User user = followers.get(i);
//            user.sendPost(message);
//        }
//    }

    public String getUsername() {
        return username;
    }

    public void sentPost() {
        numOfPosts += 1;
    }

    public void sendPM(String content) {
        pms.add(content);
    }

    public boolean isNotFollowing(User user) {
        if(followings.contains(user))
            return false;
        return true;
    }

    public String toString_() {
        String output = "";
        output += age + " " + numOfPosts + " " + followers.size() + " " + followings.size();
        return output;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> getSendWhenOnline() {
        return sendWhenOnline;
    }

    public void setSendWhenOnline(ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> sendWhenOnline) {
        this.sendWhenOnline = sendWhenOnline;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public boolean isBlocked(User user){
        return (this.blocked.contains(user) || user.blocked.contains(this));
    }

    public void block(User userToBlock) {
        blocked.add(userToBlock);
        if(followers.contains(userToBlock))
            followers.remove(userToBlock);
        if(followings.contains(userToBlock))
            followings.remove(userToBlock);
    }
}
