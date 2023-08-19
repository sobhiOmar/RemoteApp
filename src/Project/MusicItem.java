package Project;

import java.awt.*;
import java.io.File;
import java.io.IOException;

class MusicItem {
    private String fileName;
    private String filePath;
    private int durationInSeconds;

    public MusicItem(String fileName, String filePath, int durationInSeconds) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.durationInSeconds = durationInSeconds;
    }

    public String getFileName() {
        return fileName;
    }
    public void play() {
        try {
            File audioFile = new File(filePath);
            Desktop.getDesktop().open(audioFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    @Override
    public String toString() {
        return fileName+"/"+durationInSeconds;
    }

}
