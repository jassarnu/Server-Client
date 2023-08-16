
#include "../include/ReadKeyboard.h"
ReadKeyboard::ReadKeyboard(ConnectionHandler &connectionhandler) :con(connectionhandler){}
string convertToString(char* a, int size)
{
    int i;
    string s = "";
    for (i = 0; i < size; i++) {
        s = s + a[i];
    }
    return s;
}
void ReadKeyboard::act() {
    while(con.getconnected()) {
        const short bufsize = 1024;
        const char *nm = new char(';');
        char buf[bufsize] ;
        std::cin.getline(buf, bufsize);
        std::vector<std::string> command;
        string line(buf);
        split(line, " ", command);
        char delimiter[1] = {'\0'};
        if (command[0] == "REGISTER") {
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(1, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
            con.sendFrameAscii(command[1], '\0');
            con.sendFrameAscii(command[2], '\0');
            con.sendFrameAscii(command[3], '\0');
            con.sendBytes(nm, 1);
            free(opcode);
        }
        if (command[0] == "LOGIN") {
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(2, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
            con.sendFrameAscii(command[1], '\0');
            con.sendFrameAscii(command[2], '\0');
//            std::cout << "SENDING STRING : " << command[3] << "WITH LEN: " << (command[3]).length() << std::endl;
            con.sendFrameAscii(command[3],';');
            free(opcode);
            //con.sendBytes(nm, 1);
        }
        if (command[0] == "LOGOUT") {
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(3, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
            con.sendBytes(nm, 1);
            free(opcode);
            sleep(2);
        }
        if (command[0] == "FOLLOW") { // FOLLOW/UNFOLLOW and capatcha how to send
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(4, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
//            char *ch = new char((char) std::stoi(command[1]));
            std::cout << "Command[1] = " << command[1] << std::endl;
            con.sendFrameAscii(command[1], '\0');
            con.sendFrameAscii(command[2], '\0');
            con.sendBytes(nm, 1); //(;)
            free(opcode);
        }
        if (command[0] == "POST") {
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(5, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
            for(size_t i = 1; i < command.size(); i++)
                con.sendFrameAscii(command[i], '\0');
            con.sendBytes(nm, 1);
            free(opcode);
        }
        if (command[0] == "PM") {
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(6, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
            for(size_t i = 1; i < command.size(); i++)
                con.sendFrameAscii(command[i], '\0');
            con.sendBytes(nm, 1);
            free(opcode);
        }
        if (command[0] == "LOGSTAT") {
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(7, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
            con.sendBytes(nm, 1);
            free(opcode);
        }
        if (command[0] == "STAT") {
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(8, opcode);
            con.sendBytes(opcode, 2);
            con.sendBytes(delimiter, 1);
            con.sendFrameAscii(command[1], '\0');
            con.sendBytes(nm, 1);
            free(opcode);
        }
        if(command[0] == "BLOCK"){
            char  * opcode = (char *)(malloc(2));
            con.shortToBytes(12,opcode);
            con.sendBytes(opcode,2);
            con.sendBytes(delimiter, 1);
            con.sendFrameAscii(command[1],'\0');
            con.sendBytes(nm, 1);
            free(opcode);
        }
        command.clear();
    }

}


void ReadKeyboard::split(string s, string delimiter, vector<string> &command) {
    size_t pos = 0;
    std::string token;
    int counter = 0;
    while ((pos = s.find(delimiter)) != std::string::npos) {
        token = s.substr(0, pos);
        command.push_back(token);
        s = s.erase(0, pos + delimiter.length());
        counter = counter + 1;
    }
    command.push_back(s);
}

