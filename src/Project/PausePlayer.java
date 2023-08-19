package Project;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

interface User32 extends StdCallLibrary {
    User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
    boolean PostMessageA(int hWnd, int msg, int wParam, int lParam);


}

public class PausePlayer {
    public static final int KEYEVENTF_EXTENDEDKEY = 0x0001;
    public static final int KEYEVENTF_KEYUP = 0x0002;
    public static void pause() {
        try {
            User32.INSTANCE.keybd_event((byte) 0xB3, (byte) 0, 0, 0);
            User32.INSTANCE.keybd_event((byte) 0xB3, (byte) 0, 2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void nextSong() {
        User32.INSTANCE.keybd_event((byte) 0xB0, (byte) 0, KEYEVENTF_EXTENDEDKEY, 0);
        User32.INSTANCE.keybd_event((byte) 0xB0, (byte) 0, KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);
    }

    public static void previousSong() {
        User32.INSTANCE.keybd_event((byte) 0xB1, (byte) 0, KEYEVENTF_EXTENDEDKEY, 0);
        User32.INSTANCE.keybd_event((byte) 0xB1, (byte) 0, KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);
    }

}
