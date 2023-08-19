package Project;

//import javax.management.timer.Timer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
public class server{

  private static int mutecounter=0;
  private static ServerSocket serverSocket;
  private static Socket clientSocket;
  private static String message="";
  static DataInputStream dataIn;
  static DataOutputStream dataOut;
  static BrightnessController bc;
  static BroadCaster bcasterd;
  private static final PausePlayer p =new PausePlayer();

  public static String getConnectedIp() {
      return ConnectedIp;
  }

  public static String ConnectedIp ="";



  public static String SystemIp;
  private static GUI gui;
  private static music_player mp;
  private static final String ConnectedIp_File = "ConnectedIp_File.txt";
  public static Thread bcasterThread;
  static {
      try {
          gui = new GUI();
          mp = new music_player();
          bcasterd = new BroadCaster();
          loadConnectedIp();
           bcasterThread  = new Thread(new Runnable() {
              @Override
              public void run() {
                  bcasterd.run();
              }
          });bcasterThread.start();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  server() throws IOException {
      System.out.println("server started");
      SystemIp = getIPAddress();
  }


  static void Sleep() throws IOException {
      Runtime.getRuntime().exec("Rundll32.exe powrprof.dll,SetSuspendState Sleep");
  }
  static void FastShutDown() throws IOException {
      Runtime.getRuntime().exec("shutdown -s -t 0");
  }

  static void FastSleep(){
      CountdownTimer timer = new CountdownTimer(10, "/Sleep");
  }

  static void Shutdown(){
      CountdownTimer timer = new CountdownTimer(15, "/FastShutDown");
  }
//works by getting all the network interfaces and checking if the name contains wlan or wi-fi and gets the ipv4 address from it
public static String getIPAddress() {
  try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
          NetworkInterface networkInterface = interfaces.nextElement();
          String interfaceName = networkInterface.getName().toLowerCase();
          if ( interfaceName.contains("wlan") || interfaceName.contains("wi-fi") ) {
              Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
              while (addresses.hasMoreElements()) {
                  InetAddress address = addresses.nextElement();
                  if (address instanceof Inet4Address) {
                      SystemIp= address.getHostAddress();
                      return address.getHostAddress();
                  }
              }
          }
      }
      return "No Wi-Fi interface found";
  } catch (Exception e) {
      e.printStackTrace();
      return "";
  }
}

  public static void main(String[] args) throws Exception {
       bc = new BrightnessController();

      while (!message.equalsIgnoreCase("over")) {
          try {
              CreateServerSocket();

              clientSocket = serverSocket.accept();
              dataIn = new DataInputStream(clientSocket.getInputStream());
              dataOut = new DataOutputStream(clientSocket.getOutputStream());
              message = dataIn.readUTF();
              ConnectedIp=clientSocket.getInetAddress().toString();
              //remove the / from the ip
              SetConnectedIp(ConnectedIp);
              // printing the message
              gui.textArea.append("Connected to "+ConnectedIp+ "\n");
              MessageHandler(message);

          } catch (IOException ex) {
              System.out.println("Problem in message reading");
          }
      }
  }
  public static void SetConnectedIp(String ip){
      ConnectedIp = ip.substring(1);
      System.out.println("changing ip to  : " + ConnectedIp);
      StoreConnectedIp();
  }
  private static void StoreConnectedIp(){
     //store the ip in a file
          try (BufferedWriter writer = new BufferedWriter(new FileWriter(ConnectedIp_File))) {
                  writer.write(ConnectedIp);
                  writer.newLine();
          } catch (IOException e) {
              e.printStackTrace();
          }

  }
  private static void loadConnectedIp(){
      try (BufferedReader reader = new BufferedReader(new FileReader(ConnectedIp_File))) {
          String line;
          while ((line = reader.readLine()) != null) {
              ConnectedIp=line;
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
  public static void say(String message) throws IOException {
      //check if the connection is closed

      if (clientSocket==null||clientSocket.isClosed()){
          System.out.println("trying to establish connection");
          System.out.println("sending message : "+message);
          try {
              // Connect to the server
              Socket serve = new Socket(ConnectedIp, 4449);
              DataInputStream In = new DataInputStream(serve.getInputStream());
              DataOutputStream Out = new DataOutputStream(serve.getOutputStream());
              System.out.println("Connected to the server");
              // Send messages to the server
              Out.writeUTF(message);
              System.out.println("Message sent to the server");
              // Read response from the server
              String response = In.readUTF();
              System.out.println("Server response: " + response);

              if (response.equals("200OK")) {
                  System.out.println("Message sent successfully");
                  return;
              }
              // Close the streams and socket
              In.close();
              Out.close();
              serve.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }else{
          System.out.println("connection is opened saying .."+message);
          dataOut.writeUTF(message);
      }

  }
  public static void CreateServerSocket(){
      try {
          if (serverSocket==null ||serverSocket.isClosed()){
              System.out.println("trying to establish connection");
              serverSocket = new ServerSocket(4444);
              System.out.println("The IP address of the server is " + SystemIp);
              gui.textArea.append("Server started. Listening to the port 4444"+ "\n");
          }else
              System.out.println("server socket is opened");

      } catch (IOException e) {
          System.out.println("Could not listen on port: 4444");
      }
  }


  public static void MessageHandler(String message) throws IOException, AWTException, InterruptedException {
      System.out.println("message Before processing is "+message);
      if (message.substring(0, 2).equalsIgnoreCase("t ")) {
          HandelTimer(message);
      }else if (message.length()>5){
          System.out.println("good1");
          if (message.substring(0, 4).equalsIgnoreCase("-p-f")){
              HandelMusicPlayer(message.substring(4));
          }else if (message.length()>10){
              if (message.substring(0, 10).equalsIgnoreCase("brightness") ){
                  bc.setBrightnessLevel(Integer.parseInt(message.substring(10)));
              }else if(IsUrl(message)){
                  System.out.println("opening url "+message);
                  gui.textArea.append("opening url "+message+ "\n");
                  OpenLinkInBrowser(message);
              }
              else
                  Switch(message);
          }
          else
              Switch(message);
      } else
          Switch(message);
  }


  private static void HandelTimer(String message){
      String timeANDmetodName = message.substring(2);
      String time = timeANDmetodName.substring(0, 8);
      System.out.println("time is "+time);
      String[] timeArray = time.split(":");
      int hours = Integer.parseInt(timeArray[0]);
      int minutes = Integer.parseInt(timeArray[1]);
      int seconds = Integer.parseInt(timeArray[2]);
      System.out.println("hours "+hours+" minutes "+minutes+" seconds "+seconds);
      int totalSeconds = (hours * 60 * 60) + (minutes * 60) + seconds;
      System.out.println(totalSeconds);
      gui.textArea.append("Timer set to "+time+ "\n");
      CountdownTimer counter= new CountdownTimer(totalSeconds,message);
  }
  private static boolean IsUrl(String url) {
      return (url.substring(0, 8).equalsIgnoreCase("https://") || url.substring(0, 7).equalsIgnoreCase("http://")) && url.length() > 15;
  }
  private static void HandelMusicPlayer(String input) throws IOException {
      System.out.println("handling :"+input);
      if (input.equals("pause")){
          mp.Pause();
          System.out.println("pausing file song");
          PausePlayer.pause();
          say(message);
      }else {
          System.out.println("Not pausing");
          String res = mp.playByName(input);
          if (res == "Playing") {
              say(message);
          } else {
              say(message + "SongDoesNotExist");
          }
      }
  }


  public static void OpenLinkInBrowser(String url) throws IOException {
          if (Desktop.isDesktopSupported()) {
              Desktop desktop = Desktop.getDesktop();
              if (desktop.isSupported(Desktop.Action.BROWSE)) {
                  try {
                      desktop.browse(new URI(url));
                  } catch (IOException | URISyntaxException e) {
                      e.printStackTrace();
                  }
              }
          }
  }


  private static void Switch(String message) throws IOException, AWTException {
      volume vol = new volume();
      System.out.println(SystemIp);
      robot r=new robot();
      if (message!=null){
          switch (message) {
              case "/FastShutDown" : {
                  System.out.println(message);
                  gui.textArea.append("Shuting down fast"+ "\n");
                  FastShutDown();
                  say(message);
                  break;
              }
              case "ShutDown" : {
                  System.out.println(message);
                  gui.textArea.append("Shutting down"+ "\n");
                  Shutdown();
                  say(message);
                  break;
              }
              case "volume_up" : {
                  System.out.println(message);
                  gui.textArea.append("increasing audio volume"+ "\n");
                  say(message);
                  vol.VolUp();
                  break;
              }
              case "volume_down" : {
                  System.out.println(message);
                  gui.textArea.append("decreasing audio volume"+ "\n");
                  vol.VolDown();
                  say(message);
                  break;
              }case "GetBrightness" : {
                  System.out.println(message);
                  gui.textArea.append("getting brightness"+ "\n");
                  say(bc.getBrightnessLevel());
                  break;
              } case "brightness_up" : {
                  System.out.println(message);
                  gui.textArea.append("increasing brightness"+ "\n");
                  bc.increaseBrightness();
                  say(bc.getBrightnessLevel());
                  break;
              }case "brightness_down" : {
                  System.out.println(message);
                  gui.textArea.append("decreasing brightness"+ "\n");
                  bc.decreaseBrightness();
                  say(bc.getBrightnessLevel());
                  break;
              }
              case "Sleep" : {
                  System.out.println(message);
                  gui.textArea.append("going to sleep"+ "\n");
                  say(message);
                  FastSleep();
                  break;
              }
              case "/FastSleep" : {
                  System.out.println(message);
                  gui.textArea.append("going sleep fast"+ "\n");
                  say(message);
                  FastSleep();
                  break;
              }
              case"cancelTimer" : {
                  System.out.println(message);
                  gui.textArea.append("canceling timer"+ "\n");
                  say(message);
                  CountdownTimer.cancelTimer();
                  break;
              }
              case "ShutUp" : {
                  System.out.println(message);
                  gui.textArea.append("pausing " + "\n");
                  PausePlayer.pause();
                  say(message);
                  break;
              }
              case "Next" : {
                  System.out.println(message);
                  gui.textArea.append("playing next song"+ "\n");
                  //dataOut.writeUTF(message);
                  say(message);
                  PausePlayer.nextSong();
                  System.out.println("next song");
                  break;
              }
              case "previous" : {
                  System.out.println(message);
                  gui.textArea.append("playing previous song "+ "\n");
                  say(message);
                  PausePlayer.previousSong();
                  break;
              }
              case "connect" : {
                  System.out.println(message);
                  gui.textArea.append("connected "+ "\n");
                  say("connected");
                  break;
              }
              case "volume0" : {
                  System.out.println(message);
                  if (mutecounter%2==0) {
                      gui.textArea.append("muting" + "\n");
                      mutecounter++;
                  }
                  else {
                      gui.textArea.append("unmuting" + "\n");
                  }
                  say(message);
                  vol.mute();
                  break;
              }
              case"minimize":{
                  System.out.println(message);
                  gui.textArea.append("minimizing"+ "\n");
                  say(message);
                  r.minimize();
                  break;
              }
              case"closeAll":{
                  System.out.println(message);
                  gui.textArea.append("minimizing"+ "\n");
                  say(message);
                  r.closeAll();
                  break;
              } case"GetMusic":{
                  System.out.println(message);
                  System.out.println("getting music");
                  gui.textArea.append("getting music"+ "\n");
                  // convert music list to string array
                  DefaultListModel<MusicItem> musicListModel = GUI.GetmusicListModel();
                  int size =musicListModel.size();
                  String StringMusicList = "";
                  for (int i = 0; i < size; i++) {
                      StringMusicList += musicListModel.get(i).toString()+"\\\\";
                  }
                  System.out.println("sending : "+StringMusicList);
                say(StringMusicList);
                  break;

              }
              default :
                  System.out.println("something is not right message is "+message);
                  gui.textArea.setText("");
                  gui.textArea.append(message+ "\n");
                  break;
          }
      }else {
          say("connected");
          gui.textArea.append("connected"+ "\n");
      }
      dataIn.close();
      dataOut.close();
      clientSocket.close();
  }


}

