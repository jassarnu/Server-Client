#include <mutex>
#include "../include/SocketReadAndPrint.h"
SocketReadAndPrint::SocketReadAndPrint(ConnectionHandler &connectionhandl) :con(connectionhandl){}
short SocketReadAndPrint:: bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
mutex print_mutex;
void SocketReadAndPrint::act() {
    char opcodeBytes[2];
    char msgOpcode[2];
    char pmpub;
    char nn[1];
    char jj[1];
    char mm [1];
    while (con.getconnected()) {
        string postinguser;
        string content;
        string user;
        con.getBytes(opcodeBytes, 2);
        char opcode[] ={opcodeBytes[0], opcodeBytes[1]};;
        short OpCode = bytesToShort(opcode);
        if (OpCode == 9) {
            con.getBytes(&pmpub, 1);
            con.getFrameAscii(postinguser, '\0');
            con.getFrameAscii(content, '\0');
            con.getBytes(mm,1);
            if (pmpub == '0') {
                cout << "NOTIFICATION " << "PM " << postinguser  << " " << content << endl; }
            else {
                cout << content  << endl; }
        }
        if (OpCode == 10) {
            con.getBytes(msgOpcode,2);
            short msgopcode = bytesToShort(msgOpcode);
            if ((msgopcode == 1) || (msgopcode == 2) || (msgopcode == 5) || (msgopcode == 6) || (msgopcode == 12)) {
                cout << "ACK " << msgopcode << endl;
            }
            if(msgopcode == 3){
                con.setconnected();
                cout << "ACK " << msgopcode << endl;
            }
            if (msgopcode == 4) {
                con.getFrameAscii(user, '\0');
//                con.getBytes(cc,1);
//                user = user.substr(0, user.size()-1);
                cout <<  "ACK " << msgopcode << " " << user  << endl;
            }
            if (msgopcode == 7) {//keeemmmmm nokta fasly
                char age[2];
                char numposts[2];
                char numfolowers[2];
                char numfollowing[2];
                con.getBytes(age, 2);
                con.getBytes(numposts, 2);
                con.getBytes(numfolowers, 2);
                con.getBytes(numfollowing, 2);
                cout << "ACK" << " " << msgopcode << " "<< std::to_string(con.bytesToShort(age)) + " " << std::to_string(con.bytesToShort(numposts)) + " "
                     << std::to_string(con.bytesToShort(numfolowers)) + " " << std::to_string(con.bytesToShort(numfollowing)) << endl;
            }
            if (msgopcode == 8) {//keeem nokta faslyyyyy
                char age[2];
                char numposts[2];
                char numfolowers[2];
                char numfollowing[2];
                con.getBytes(age, 2);
                con.getBytes(numposts, 2);
                con.getBytes(numfolowers, 2);
                con.getBytes(numfollowing, 2);
                cout << "ACK " << msgopcode << " "<< std::to_string(con.bytesToShort(age)) + " " << std::to_string(con.bytesToShort(numposts)) + " "
                     << std::to_string(con.bytesToShort(numfolowers)) + " " << std::to_string(con.bytesToShort(numfollowing)) << endl;
            }
            con.getBytes(jj,1);
        }
        if (OpCode == 11) {
            con.getBytes(msgOpcode,2);
            short msgopcode = bytesToShort(msgOpcode);
            con.getBytes(nn,1);
            cout << "ERROR " << msgopcode << endl;
        }
    }
}
