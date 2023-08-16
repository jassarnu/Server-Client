package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.EncodeDecode;
import bgu.spl.net.api.ClientToServerMsg.WORLD;
import bgu.spl.net.api.bidi.BidiMessagingProtocolmpl;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {
        Server.threadPerClient(Integer.parseInt(args[0]), () -> new BidiMessagingProtocolmpl(new WORLD()), EncodeDecode::new).serve();

    }

}
