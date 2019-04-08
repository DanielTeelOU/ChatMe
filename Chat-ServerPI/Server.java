// Java implementation of Server side 
// It contains two classes : Server and ServerClientHandler 
// Save file as Server.java 

import java.io.*;
import java.util.*;
import java.net.*;

// Server class 
public class Server {

    // Vector For Nodes / Clients
    static Vector < ServerClientHandler > ar = new Vector < > ();

    // Track Connection Count
    static int i = 0;

    public static void main(String[] args) throws IOException {
        // Variables
        ServerSocket ss = new ServerSocket(4567);

        Socket s;
        
        System.out.println("Server Initiated... Listening To Connections");

        // Infinite Loop To Listen To Requests
        while (true) {
            // Accept the incoming request 
            s = ss.accept();
            
            System.out.println("New Client Connection => " + s);

            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            
            System.out.println("Adding Client To Node...");

            // Create a new handler object for handling this request. 
            ServerClientHandler mtch = new ServerClientHandler(s, "" + i, dis, dos);

            // Create a new Thread with this object. 
            Thread t = new Thread(mtch);

            System.out.println("Node / Thread Created, Appending Client To Vector...");

            // add this client to active clients list 
            ar.add(mtch);

            // start the thread. 
            t.start();
            
            // Start Thread
            System.out.println("Initiate Thread...");

            // increment i for new client. 
            // i is used for naming only, and can be replaced 
            // by any naming scheme 
            i++;
            System.out.println("Thread Ready! Client Successfully Connected.");

        }
    }
}