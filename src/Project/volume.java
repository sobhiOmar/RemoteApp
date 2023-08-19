package Project;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class volume {
    int counter=0;
public void VolUp(){
    try {
        Process process = Runtime.getRuntime().exec("nircmd.exe changesysvolume 5000");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        in.readLine();
        process.waitFor();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
public void VolDown(){
    try {
        System.out.println("volume down by 5% ");
        Process process = Runtime.getRuntime().exec("nircmd.exe changesysvolume -5000");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        in.readLine();
        process.waitFor();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void mute(){
    if (counter%2!=0){
        try {
            Process process = Runtime.getRuntime().exec("nircmd.exe mutesysvolume 0");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            in.readLine();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }else {
        try {
            Process process = Runtime.getRuntime().exec("nircmd.exe mutesysvolume 1");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            in.readLine();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        counter++;
    }
    public static void nextSong() {
        try {
            Process process = Runtime.getRuntime().exec("nircmd.exe sendskeypress ctrl+right");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            in.readLine();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void previousSong() {
        try {
            Process process = Runtime.getRuntime().exec("nircmd.exe sendskeypress ctrl+left");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            in.readLine();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

}


}



