package game.network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    
    private int port;
    private DatagramSocket socket;
    private CopyOnWriteArrayList<EnemyPlayerData> players;
    private int clientCounter = 0;
    private HashMap<Integer,Long> ping;
    private ArrayList<Integer> killIds;
    
    public static void main(String[] args) {
        Server server = new Server(9999);
        server.start();
    }
    
    public Server(int port) {
        players = new CopyOnWriteArrayList<EnemyPlayerData>();
        ping = new HashMap<Integer,Long>();
        killIds = new ArrayList<Integer>();
        System.out.println("Set port.");
        this.port = port;
    }
    
    public boolean isKillId(int id) {
        return killIds.contains(id);
    }
    
    public void killId(int id) {
        killIds.remove(id);
        ping.remove(id);
    }
    
    public void start() {
        System.out.println("Creating Server.");
        
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Error creating socket: " + e);
        }
        
        System.out.println("Started server.");
        
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = new byte[DataPacket.MAX_SIZE];
                DatagramPacket recvPacket = new DatagramPacket(bytes,bytes.length);
                DataPacket packet;
                int clientId;
                boolean updated = false;
                long iterationNanos = 0;
                long lastNanos = System.nanoTime();
                
                while (true) {
                    long tempNanos = System.nanoTime();
                    iterationNanos = tempNanos - lastNanos;
                    lastNanos = tempNanos;
                    
                    for (Map.Entry<Integer,Long> entry : ping.entrySet()) {
                        Long l = entry.getValue();
                        entry.setValue(l+iterationNanos);
                        if (l > 2000000)
                            killIds.add(entry.getKey());
                    }
                    
                    try {
                        socket.receive(recvPacket);
                    } catch (IOException e) {
                        System.out.println("Unable to recieve data: " + e);
                    }
                                        
                    if (recvPacket.getLength() == 0) {
                        System.out.println("New client! " + clientCounter);
                        try {
                            socket.send(new DatagramPacket(DataPacket.valueOf(clientCounter),
                                    4,recvPacket.getAddress(),recvPacket.getPort()));
                        } catch (IOException e) {
                            System.out.println("Handshake error: " + e);
                        }
                        ping.put(clientCounter,0l);
                        clientCounter++;
                        continue;
                    }
                    
                    packet = new DataPacket(recvPacket.getData());
                    clientId = packet.getClient();
                    
                    updated = false;
                    for (EnemyPlayerData e : players) {
                        if (e.id == clientId) {
                            ping.put(clientId,0l);
                            packet.update(e);
                            updated = true;
                            break;
                        }
                    }
                    
                    if (updated)
                        continue;
                    
                    players.add(new EnemyPlayerData(clientId,packet.get(DataPacket.X),packet.get(DataPacket.Y)));
                    
                    Runnable r = new ServerSendThread(players,socket,Server.this,
                            recvPacket.getAddress(),recvPacket.getPort(),clientId);
                    new Thread(r).start();
                }
            }
        });
        receiveThread.start();
    }
}
