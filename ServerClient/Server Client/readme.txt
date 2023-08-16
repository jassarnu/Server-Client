1.1) 
The command for server:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 10"

The command for client:
in the bin:
./BGSclient 10.0.2.15 7777

2) 
REGISTER SHAHAD 123 05-02-2000
REGISTER WARD 111 15-10-2001
REGISTER YARA 1010 08-04-2008
LOGIN SHAHAD 123 1
LOGOUT
FOLLOW 0 WARD
POST hello!
PM WARD where are you?
LOGSTAT
STAT WARD|YARA
BLOCK WARD


3) in the server, WORLD class in as :private final String[] filteredWords.


