package Project;

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CountdownTimer {
    private String name;
    private int timeRemaining;
    private static Timer timer;
    private static boolean cancelled;
    private String methodName;
    private static String status;
    private static JFrame frame = new JFrame("Mohsen's Remote");

    public CountdownTimer(int seconds, String message) {

        status="live";
        String[] messageParts = message.split("/");
        System.out.println("in countdown timer with method " + messageParts[1]+" and time " + seconds);
        setName(messageParts[1]);
        if (messageParts.length != 2) {
            throw new IllegalArgumentException("Invalid message format.");
        }
        this.methodName = messageParts[1];
        this.timeRemaining = seconds;
        this.cancelled = false;

        if (seconds <= 0) {
            executeMethod(methodName);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel countdownLabel = new JLabel("", JLabel.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(countdownLabel, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                frame.dispose(); // Close the GUI when the Cancel button is pressed
            }
        });
        frame.add(cancelButton, BorderLayout.SOUTH);

        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                countdownLabel.setText("Time remaining: " + timeRemaining + " seconds");

                if (timeRemaining == 0 || cancelled) {
                    timer.stop();
                    frame.dispose();
                    if (!cancelled) {
                        executeMethod(methodName); // Call the method if not cancelled
                    }
                }
            }
        });
        timer.start();
    }



    public void setName(String name) {
    this.name = name;
    frame.setTitle(name+" Timer");
}
public String getName() {
        return name;
    }
    public String getStatus() {
        return status;
    }
    public static void cancelTimer() {
        if (CountdownTimer.timer != null) {
        status = "cancelled";
        cancelled = true;
        timer.stop();
        frame.dispose();
    }
    }
    private void executeMethod(String methodName) {
        try {
            //reset the gui
            status = "live";
            cancelled = false;
            timeRemaining = 0;
            frame.dispose();
            //execute the method

            Class<?> myClass = server.class;
            Method method = myClass.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(myClass.newInstance());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }


}
