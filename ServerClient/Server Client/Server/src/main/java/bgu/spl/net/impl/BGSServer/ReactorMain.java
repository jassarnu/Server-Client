package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.ClientToServerMsg.WORLD;
import bgu.spl.net.api.bidi.BidiMessagingProtocolmpl;
import bgu.spl.net.api.bidi.EncodeDecode;
import bgu.spl.net.srv.Reactor;

public class ReactorMain {

    public static void main(String[] args) {
        Reactor<String> myserver = new Reactor<String>(Integer.parseInt(args[1]), Integer.parseInt(args[0]),()->  new BidiMessagingProtocolmpl(new WORLD()),()->new EncodeDecode());
        myserver.serve();

    }

}
