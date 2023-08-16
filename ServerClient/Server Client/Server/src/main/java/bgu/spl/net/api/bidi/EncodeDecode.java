package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class EncodeDecode implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private Short opcode = 0;
    private boolean lineHasEnded = false;
    //private int zeroCounter = 0;

    @Override
    public String decodeNextByte(byte nextByte) {
        if(len != 0 && nextByte == '\0')
            nextByte = ' ';
        System.out.println(nextByte);
        if (nextByte == ';')
            lineHasEnded = true;

        if(lineHasEnded)
           {   String c = popString(); 
               System.out.println(c);
               return c;
           }

        if(nextByte != ';')
            pushByte(nextByte);

        return null; // line didn't complete yet.

    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    @Override
    public byte[] encode(String message) {
        ArrayList<Byte> arr_lst = new ArrayList<>();
        int indexInMsg = 0;
        String[] splitting = message.split(" ");
        String operation = splitting[indexInMsg++];
        if(operation.equals("NOTIFICATION") ){
            byte[] nining = shortToBytes((short) 9);
            arr_lst.add(nining[0]);
            arr_lst.add(nining[1]);
            if(splitting[1].equals("PM") ){
                arr_lst.add((byte)'0');

            }
            else{
                arr_lst.add((byte)'1');
            }
            byte[] username = splitting[2].getBytes();
            for(int i = 0; i < username.length; i++)
                arr_lst.add(username[i]);
            arr_lst.add((byte) '\0');
            for(int i = 3; i < splitting.length; i++){
                byte[] toByte = splitting[i].getBytes();
                    for (int j = 0; j < toByte.length; j++) {
                        arr_lst.add(toByte[j]);
                    }
                arr_lst.add((byte) ' ');
            }
             arr_lst.add((byte)'\0');
        }
        else{
            if(operation.equals("ERROR")) {
                byte[] error = shortToBytes((short) 11);
                arr_lst.add(error[0]);
                arr_lst.add(error[1]);
                short errshort = Short.parseShort(splitting[indexInMsg++]);
                byte[] errbyte = shortToBytes(errshort);
                arr_lst.add(errbyte[0]);
                arr_lst.add(errbyte[1]);
            }
            else{ // it's ACK
                Short opcode = Short.parseShort(splitting[indexInMsg++]);
                byte[] done = shortToBytes((short) 10);
                arr_lst.add(done[0]);
                arr_lst.add(done[1]);
                if(((opcode == 1) || (opcode == 2) || (opcode == 3) || (opcode == 5) || (opcode == 6) || (opcode == 12))){
                    byte[] opc = shortToBytes(opcode);
                    arr_lst.add(opc[0]);
                    arr_lst.add(opc[1]);
                }
                if(opcode == 4){
                    byte[] opc = shortToBytes(opcode);
                    for(int i = 0; i < opc.length; i++) {
                        arr_lst.add(opc[i]);
                    }
                    byte[] username = splitting[2].getBytes();
                    for(int i = 0; i < username.length; i++)
                        arr_lst.add(username[i]);
                    arr_lst.add((byte)'\0');
                }
                if(opcode == 8){
                    String[] splittedByEnetersForStat = message.split("\\n");
                    for(int i = 0; i < splittedByEnetersForStat.length; i++){
                        String[] splittedBySpaces = splittedByEnetersForStat[i].split(" ");
                        if(i != 0) {
                            arr_lst.add(done[0]);
                            arr_lst.add(done[1]);
                        }
                        byte[] eghit = shortToBytes((short) 8);
                        for(int j = 0; j < eghit.length; j++) {
                            arr_lst.add(eghit[j]);
                        }
                        for(int j = 2; j < splittedBySpaces.length; j++){
                            byte[] content = shortToBytes(Short.parseShort(splittedBySpaces[j]));
                            for (int r = 0; r < content.length; r++) {
                                arr_lst.add(content[r]);
                            }
                        }

                    }
                }
                 if((opcode == 7)) {
                    byte[] opc = shortToBytes(opcode);
                    for(int i = 0; i < opc.length; i++) {
                        arr_lst.add(opc[i]);
                    }
                    for(int i = 2; i < splitting.length; i++) {
                        byte[] content = shortToBytes(Short.parseShort(splitting[i]));
//
                        {
                            for (int j = 0; j < content.length; j++) {
                                arr_lst.add(content[j]);
                            }
                        }
                    }

                }
            }
        }


        arr_lst.add((byte)';');
        byte[] ret = new byte[arr_lst.size()];
        for (int i = 0 ; i < arr_lst.size() ; i++){
            ret[i] = (byte)arr_lst.get(i);
        }
    
        return ret;
    
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        if((bytes[1] != (byte)12)) {
            bytes[0] += 48;
            bytes[1] += 48;
        }
        else{
            bytes[0] = '1';
            bytes[1] = '2';
        }
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        lineHasEnded = false;
        bytes = new byte[1 << 10];
        return result;
    }
}
