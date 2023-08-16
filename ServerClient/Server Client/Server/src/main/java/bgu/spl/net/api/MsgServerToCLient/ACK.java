package bgu.spl.net.api.MsgServerToCLient;

public class ACK {
    private short MSgOP;
    private String ACKMsg;

    public ACK(short mSgOP) {
        MSgOP = mSgOP;
    }
}
