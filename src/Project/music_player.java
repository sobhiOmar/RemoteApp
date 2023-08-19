package Project;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Objects;

/**
 * A simple music player class with playback and control functionality.
 */

public class music_player implements KeyListener {
    private int currentPlayingIndex;
    private DefaultListModel musicListModel;
    private volatile boolean isPaused = false;
    private volatile boolean isPlayingNextSong = false;
    private volatile boolean isPlaying = false;
    private volatile Thread playingThread = null;
    private int durationInSeconds;
    private long remainingTime;
    private long startTime;

    /**
     * Creates a new instance of the music_player class.
     *
     * @throws IOException If there is an issue initializing the musicListModel.
     */
    public music_player() throws IOException {
        musicListModel = GUI.GetmusicListModel();
        if (musicListModel != null) {
            System.out.println("musicListModel size is: " + musicListModel.size());
        } else {
            System.out.println("something wrong with musicListModel");
        }
    }
    /**
     * Plays the song at the specified index.
     *
     * @param index The index of the song to play.
     */
    public void playSong(int index) {
        System.out.println("music player play song: " + index);
        synchronized (this) {
            isPaused = false;
            if (index >= 0 && index < musicListModel.getSize()) {
                MusicItem musicItem = (MusicItem) musicListModel.getElementAt(index);
                musicItem.play();
                currentPlayingIndex = index;
                //wait for 100 ms then call startPlayingThread
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startPlayingThread(musicItem);

            }
        }
    }


    /**
     * Plays a given MusicItem.
     *
     * @param musicItem The MusicItem to play.
     */

    public void playSong(MusicItem musicItem){
        int index = getSongIndex(musicItem);
        playSong(index);
    }


    /**
     * Plays a song by its name.
     *
     * @param name The name of the song to play.
     * @return A status message indicating if the song is playing or not found.
     */

    public String playByName(String name) {
        System.out.println("music player play by name: " + name);
        if (name != null) {
            MusicItem musicItem = FindMusicItemByName(name);
            if (musicItem != null) {
                System.out.println("music player play by name: " + musicItem.getFilePath());
                playSong(musicItem);
                return "Playing";
            } else {
                System.out.println("could not find music item");
            }
        }
        return "NotFound";
    }

/*
* used in the GUi class
*/
    public void playNextSong() {
        int nextIndex = currentPlayingIndex + 1;
        if (nextIndex < musicListModel.getSize()) {
            playSong(nextIndex);
        }
    }
    /*
     * used in the GUi class
     */
    public void playPreviousSong() {
        int previousIndex = currentPlayingIndex - 1;
        if (previousIndex >= 0) {
            playSong(previousIndex);
        }
    }

    /**
     * return the id of next playable song note that its circular
     */
private int getNextPlayableSongIndex() {
    int nextIndex = currentPlayingIndex + 1;
    if (nextIndex < musicListModel.getSize()) {
        return nextIndex;
    }else if (nextIndex > musicListModel.getSize()){
        return 0;
    }
    return -1; // No next playable song
}

    /**
     * @param musicItem the music that's playing now
     * @Description: this method is called when a song is played to play the next song after it finishes
     */

    private void startPlayingThread(MusicItem musicItem) {

        if (playingThread != null && playingThread.isAlive()) {
            System.out.println("Terminating the old instance");
            playingThread.interrupt(); // Terminate the old instance
        }else System.out.println("there is no old instance");

        playingThread = new Thread(() -> {
            isPlaying = true;

            durationInSeconds = musicItem.getDurationInSeconds();
            remainingTime = durationInSeconds * 1000;  // Convert to milliseconds
            startTime = System.currentTimeMillis();

            while (remainingTime > 0 && isPlaying) {
                if (!isPaused) {
                    long currentTime = System.currentTimeMillis();
                    long elapsed = currentTime - startTime;
                    startTime = currentTime; // Update the start time

                    if (remainingTime <= elapsed) {
                        synchronized (this) {
                            isPlaying = false;
                            int nextPlayableIndex = getNextPlayableSongIndex();
                            if (nextPlayableIndex != -1) {
                                playSong(nextPlayableIndex); // Play the next song
                            }
                        }
                    }

                    remainingTime -= elapsed;

                    try {
                        Thread.sleep(100);  // Sleep for a short interval

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        synchronized (this) {
                            this.wait(); // Wait while paused
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        playingThread.start();
    }
    /**
     * @Description: pause the song if it's playing and resume it if it's paused
     * by updating startTime variable
     *
     */

    public void Pause() {
        synchronized (this) {
            System.out.println("should pause " + isPaused);

            isPaused = !isPaused;

            if (!isPaused) {
                long remainingMinutes = remainingTime / 60000; // 1 minute = 60000 milliseconds
                long remainingSeconds = (remainingTime % 60000) / 1000;
                System.out.println("Remaining Time: " + remainingMinutes + " minutes, " + remainingSeconds + " seconds");
                startTime = System.currentTimeMillis(); // Resume playback
            }

            this.notifyAll(); // Notify all waiting threads
        }
    }


    /**
     * Finds a MusicItem by its name.
     *
     * @param name The name of the MusicItem to find.
     * @return The found MusicItem, or null if not found.
     */
    private MusicItem FindMusicItemByName(String name){
        if (musicListModel!=null) {
            for (int i = 0; i < musicListModel.size(); i++) {
                MusicItem item = (MusicItem) musicListModel.get(i);
                if (Objects.equals(item.getFileName(), name)) {
                    System.out.println("found................." + (MusicItem) musicListModel.get(i));
                    return (MusicItem) musicListModel.get(i);
                }
            }
            System.out.println("NotFound");
            System.out.println(musicListModel.size());
            return null;
        }
        System.out.println("musicListModel is null");
        return null;
    }

    /**
     * Gets the index of a MusicItem.
     *
     * @param musicItem The MusicItem to find the index of.
     * @return The index of the MusicItem, or -1 if not found.
     */
    public int getSongIndex(MusicItem musicItem){
         musicListModel = GUI.GetmusicListModel();
        System.out.println("playing next song");
        for (int i = 0; i < musicListModel.size(); i++) {
            if (musicListModel.get(i).equals(musicItem)){
                System.out.println("playing : "+ musicListModel.get(i));
                currentPlayingIndex = i;
                return i;
            }
        }
        return -1;
    }
    /**
     * Shuffles the music playlist and plays a random song.
     */
    public void shuffle(){
        int random = (int) (Math.random() * musicListModel.size());
        playSong(random);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_PAUSE) {
            // Pause button was pressed
            System.out.println("Pause button pressed.");
            // Add your code to handle the pause action here
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
