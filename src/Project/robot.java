package Project;

import java.awt.*;
import java.awt.event.KeyEvent;

public class robot {
    Robot R;
    public robot() throws AWTException {
         R= new Robot();

    }

    public void minimize() {
        try {
            R.keyPress(KeyEvent.VK_WINDOWS);
            R.keyPress(KeyEvent.VK_D);
            R.keyRelease(KeyEvent.VK_WINDOWS);
            R.keyRelease(KeyEvent.VK_D);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    //close all windows
    public void closeAll() {
        try {
            // Press the Alt + F4 keys to close the current window
            R.keyRelease(KeyEvent.VK_ALT);
            R.keyRelease(KeyEvent.VK_F4);
            R.keyRelease(KeyEvent.VK_ALT);
            R.keyRelease(KeyEvent.VK_F4);
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

}
