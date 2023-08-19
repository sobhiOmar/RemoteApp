package Project;

//this class is used to broadcast the message to all the clients it should run till it get a message
//and then sleep
//it will run again if the value of getSystemIp() is change

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class BroadCaster {

 BroadCaster() {
     System.out.println("runing BroadCaster");
 }

 public void run() {
     DatagramSocket socket = null;
     System.out.println("BroadCaster is running");
     try {
         socket = new DatagramSocket(8888);
         byte[] receiveData = new byte[1024];
         System.out.println("Server started on port 8888");
         while (true) {
             DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
             socket.receive(packet);
             String message = new String(packet.getData(), 0, packet.getLength());

             if (message.equals("DiscoveringAndroidApp")) {
                 System.out.println("Discovery request received from Android app.");
                 InetAddress androidAppAddress = packet.getAddress();
                 System.out.println(androidAppAddress);
                 String responseMessage = server.getIPAddress();
                 server.SetConnectedIp(String.valueOf(androidAppAddress));
                 server.say("ip-"+responseMessage);
                 server.bcasterThread.stop();
             }
         }
     } catch (Exception e) {
         e.printStackTrace();
     } finally {
         if (socket != null) {
             socket.close();
         }
     }

 }
}
