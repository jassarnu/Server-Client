
#ifndef READKEYBOARD_H
#define READKEYBOARD_H
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "connectionHandler.h"
using std::string;
using std::vector;
class ReadKeyboard {
private:
    ConnectionHandler &con;
public:
    ReadKeyboard(ConnectionHandler &connectionhandler);
    void act();
    void split(string s,string delimiter,vector<string>& parse);

};
#endif //READKEYBOARD_H
