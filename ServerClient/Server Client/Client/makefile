CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGSclient
BGSclient: bin/echoClient.o bin/connectionHandler.o bin/ReadKeyboard.o bin/SocketReadAndPrint.o
	@echo "Building target: BGSclient"
	g++ -o bin/BGSclient bin/echoClient.o bin/connectionHandler.o bin/ReadKeyboard.o bin/SocketReadAndPrint.o $(LDFLAGS)
	@echo "finished Linking"

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/ReadKeyboard.o: src/ReadKeyboard.cpp
	g++ $(CFLAGS) -o bin/ReadKeyboard.o src/ReadKeyboard.cpp

bin/SocketReadAndPrint.o: src/SocketReadAndPrint.cpp
	g++ $(CFLAGS) -o bin/SocketReadAndPrint.o src/SocketReadAndPrint.cpp

.PHONY: clean
clean:
	rm -f bin/*
