
#ifndef SOCKETREADANDPRINT_H
#define SOCKETREADANDPRINT_H
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "connectionHandler.h"
using namespace std;
class SocketReadAndPrint {
private:
    ConnectionHandler& con;
public:
    SocketReadAndPrint(ConnectionHandler& connectionhandl);
    void act();
    short bytesToShort(char *bytesArr);
};
#endif //SOCKETREADANDPRINT_H
