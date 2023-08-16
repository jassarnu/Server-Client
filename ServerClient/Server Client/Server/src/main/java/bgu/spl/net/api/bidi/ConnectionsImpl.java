package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T>{
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> acceptConnect = new ConcurrentHashMap<>();

    @Override
    public boolean send(int connectionId, T msg) {
        if(acceptConnect.containsKey(connectionId)){
            acceptConnect.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler<T> client: acceptConnect.values()) {
            client.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if (acceptConnect.containsKey(connectionId)){
            acceptConnect.remove(connectionId);
        }
    }
    public void AddClient(int connectionId,ConnectionHandler<T> client){acceptConnect.put(connectionId,client);};
}
