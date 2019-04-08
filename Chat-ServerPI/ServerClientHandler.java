import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

// ServerClientHandler class 
class ServerClientHandler implements Runnable {
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // Constructor 
    public ServerClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    @Override
    public void run() {

        // Declare Input String
        String received;
        // Initiate Nickname Setter
        this.getNickname();
        
        // While Loop For Future Inputs
        while (true) {
            try {

                // Send User Amount
                sendUserCount();
                
                // Seceive The String 
                received = dis.readUTF();
                // Server Declaration
                System.out.println(this.name + " " + received);
                
                // Handle Break
                if (received.equals("logout") || received.equals("Logout") || received.equals("exit") || received.equals("Exit")) {
                    this.dos.writeUTF("logout");
                    this.handleDisconnection();
                    break;
                }
                

                // Handle Private Message
                if (received.charAt(0) == '@') {
                    String targetUser = received.split("@")[1].split(" ")[0];
                    boolean foundUser = false;
                    for (ServerClientHandler mc: Server.ar) {
                        if (mc.name.equals(targetUser)) {
                            mc.dos.writeUTF(this.name + " => @You:" + received.split(targetUser)[1]);
                            this.dos.writeUTF("Sent Privately To " + targetUser + ".");
                            foundUser = true;
                            break;
                        }
                    }
                    if (foundUser == false) {
                        this.dos.writeUTF("Private Message Failed (Invalid User)");
                    }
                } else {

                    // Loop For ALL Connected Clients
                    for (ServerClientHandler mc: Server.ar) {
                        if (this.s == mc.s) {
                            mc.dos.writeUTF("You: " + received);
                            continue;
                        }
                        mc.dos.writeUTF(this.name + ": " + received);
                    }
                    
                }

            } catch (IOException a) {
                this.handleDisconnection();
                break;
            }

        }
        try {
            // Close Connections
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Declare Disconnect Function
    private void handleDisconnection() {
        try {
            System.out.println("Client " + this.name + " has disconnected.");
            for (int i = 0; i < Server.ar.size();i++) {
                if (this.s == Server.ar.get(i).s) {
                    Server.ar.get(i).dos.writeUTF("Disconnected Successfully");
                    this.isloggedin = false;
                    this.s.close();
                    Server.ar.remove(i);
                    announce(this.name + " has disconnected!");
                    sendUserCount();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Setter For Nickname
    private void getNickname() {
        try {
            for (int i = 0; i < Server.ar.size(); i++){
                if (this.s == Server.ar.get(i).s) {
                    sendUserCount();
                    Server.ar.get(i).dos.writeUTF("Welcome to ChatMe!\n");
                    Server.ar.get(i).dos.writeUTF("Enter A Desired Nickname:");
                    this.name = dis.readUTF();
                }
            }
            this.dos.writeUTF("Welcome To The Server " + this.name + "!");
            announce(this.name + " Has joined the server!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Announce To All Connected Clients
    private void announce(String msg) {
        try {
            for (int i = 0; i < Server.ar.size();i++) {
                // Send User Amount
                Server.ar.get(i).dos.writeUTF("users:" + Server.ar.size());
                // Skip If Self
                if (this.s == Server.ar.get(i).s) {
                    continue;
                }
                // Broadcast The Message
                Server.ar.get(i).dos.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Announce User Count
    private void sendUserCount() {
        try {
            // Send User Amount
            for (int i = 0; i < Server.ar.size();i++) {
                Server.ar.get(i).dos.writeUTF("users:" + Server.ar.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}