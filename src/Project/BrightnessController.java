package Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BrightnessController {
    private int brightnessLevel;
    BrightnessController() throws IOException {
         brightnessLevel = getBrightnessLevelFromCmd();
    }

    private int getBrightnessLevelFromCmd() throws IOException {
        Process process = Runtime.getRuntime().exec("powershell (Get-CimInstance -Namespace root/WMI -ClassName WmiMonitorBrightness).CurrentBrightness");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String output = reader.readLine();
            if (output != null && output.matches("\\d+")) {
                //convert to output to int
                return Integer.parseInt(output.trim());
            } else {
                throw new IOException("Failed to retrieve brightness information.");
            }
        }
    }

    public void setBrightnessLevel(int brightnessLevel) throws InterruptedException, IOException {
        System.out.println("setting brightness to "+brightnessLevel);
        this.brightnessLevel = brightnessLevel;
        Process process = Runtime.getRuntime().exec("powershell (Get-WmiObject -Namespace root/WMI -Class WmiMonitorBrightnessMethods).WmiSetBrightness(1, "+brightnessLevel+")");
        process.waitFor();

    }

    public String getBrightnessLevel() throws IOException {
        //convert double to string
        return getBrightnessLevelFromCmd()+"%";
    }

    public double increaseBrightness(){
        if(brightnessLevel+5 < 100){
            brightnessLevel += 5;
            editBrightnessLevel();
        }
        return brightnessLevel;
    }
    public double decreaseBrightness(){
        if(brightnessLevel-5 > 0){
            brightnessLevel -= 5;
            editBrightnessLevel();
        }
        return brightnessLevel;
    }
    private void editBrightnessLevel(){
        try {
            if (brightnessLevel > 100) {
                brightnessLevel = 100;
            } else if (brightnessLevel <= 0) {
                brightnessLevel = 1;
            }
            Process process = Runtime.getRuntime().exec("powershell (Get-WmiObject -Namespace root/WMI -Class WmiMonitorBrightnessMethods).WmiSetBrightness(1, "+brightnessLevel+")");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
