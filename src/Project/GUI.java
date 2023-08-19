package Project;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class GUI {
    private JFrame frame;
    private JTextField ipField;
    public JTextArea textArea;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    JScrollPane scrollPane;
    private static DefaultListModel<MusicItem> musicListModel;
    private JList<MusicItem> musicList;
    private static final String MUSIC_LIST_FILE = "music_list.txt";
    private MusicItem NowPlaying;
    public music_player player;

    //private JButton connectButton;
    public GUI() throws IOException {
        musicListModel = new DefaultListModel<>();
        loadMusicList();
        standby();
        getIp();
        frame.setState(Frame.ICONIFIED);

    }

    // dummy method
    private void dummy() {
        System.out.println("dummy");
    }

    private void getIp() {
        String ip = server.getIPAddress();
        ipField.setText(ip);
    }

    public void standby() throws IOException {
        frame = new JFrame("Mohsen's Remote");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

         cardLayout = new CardLayout();
         mainPanel = new JPanel(cardLayout);
        
        ipField = new JTextField(10);
        ipField.setBackground(new Color(33, 33, 33));
        ipField.setForeground(new Color(255, 255, 255));

        textArea = new JTextArea(30, 40);
        textArea.setBackground(new Color(33, 33, 33));
        textArea.setForeground(Color.WHITE);
         scrollPane = new JScrollPane(textArea);

        Image image = ImageIO.read(new File("C:\\Users\\omare\\Downloads\\mobile.png"));
        frame.setIconImage(image);
        frame.getContentPane().setBackground(new Color(39, 39, 39));



        JPanel firstLayout = createFirstLayout();
        JPanel settingLayout = createSettingLayout();

        mainPanel.add(firstLayout, "firstLayout");
        mainPanel.add(settingLayout, "settingLayout");

        frame.add(mainPanel);
        frame.setVisible(true);
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
        }else {
            tray();
        }
    }
    private void tray(){
        PopupMenu popup = new PopupMenu();
        // create a tray icon
        Image image = Toolkit.getDefaultToolkit().getImage("C:\\Users\\omare\\Downloads\\mobile.png");
        TrayIcon trayIcon = new TrayIcon(image, "System Tray Example", popup);
        MenuItem openItem = new MenuItem("Open");

        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    frame.setState(Frame.NORMAL);
                    frame.setAlwaysOnTop(true);
                    frame.toFront();
                    frame.setVisible(true);
                    SystemTray.getSystemTray().remove(trayIcon);
            }
        });
        // create a menu item
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);
        // add the tray icon to the system tray
        SystemTray tray = SystemTray.getSystemTray();
        trayIcon.setImageAutoSize(true);


        // add window state listener to handle the frame minimization
        frame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {

                    if (e.getNewState() == JFrame.ICONIFIED) {
                    try {
                        frame.setVisible(false);
                        tray.add(trayIcon);
                    } catch (AWTException ex) {
                        System.out.println("Unable to add to tray");
                    }
                }
                if (e.getNewState() == 7) {
                    try {
                        frame.setVisible(false);

                        tray.add(trayIcon);
                       // frame.setVisible(false);
                    } catch (AWTException ex) {
                        System.out.println("Unable to add to system tray");
                    }
                    }
                if (e.getNewState() == JFrame.MAXIMIZED_BOTH) {
                    tray.remove(trayIcon);
                    frame.setVisible(true);
                }
                if (e.getNewState() == JFrame.NORMAL) {
                    tray.remove(trayIcon);
                    frame.setVisible(true);
                }
                trayIcon.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            frame.setState(Frame.NORMAL);
                            frame.setAlwaysOnTop(true);
                            frame.toFront();
                            frame.setVisible(true);
                            SystemTray.getSystemTray().remove(trayIcon);

                        }
                    }
                });
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
    private JPanel createFirstLayout() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton showIpButton = new JButton("Show IP");
        JButton callPhoneButton = new JButton("Call Phone");
        JButton switchToSettingButton = new JButton("Settings");

        switchToSettingButton.addActionListener(e -> cardLayout.show(mainPanel, "settingLayout"));

        panel.add(showIpButton, BorderLayout.NORTH);
        panel.add(callPhoneButton, BorderLayout.CENTER);
        panel.add(switchToSettingButton, BorderLayout.SOUTH);
        panel.add(ipField);
        panel.add(scrollPane);
        panel.setLayout(new FlowLayout());

        showIpButton.addActionListener(new ShowIpListener());

        callPhoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    server.say("calling");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return panel;
    }

    private JPanel createSettingLayout() throws IOException {

        player = new music_player();


        JPanel panel = new JPanel(new FlowLayout());
        JLabel settingsLabel = new JLabel("Settings");
        JButton switchToMainButton = new JButton("Main Layout");
        switchToMainButton.addActionListener(e -> cardLayout.show(mainPanel, "firstLayout"));
        panel.add(settingsLabel);
        panel.add(switchToMainButton);
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton addMusicButton = new JButton("Add Music File");
        JButton removeMusicButton = new JButton("Remove Music File");
        JButton playMusicButton = new JButton("Play");
        JButton stopMusicButton = new JButton("Stop");
        JButton shuffleMusicButton = new JButton("Shuffle");
        JButton nextMusicButton = new JButton("Next");
        JButton previousMusicButton = new JButton("Previous");

        addMusicButton.addActionListener(e -> addMusicFile());
        removeMusicButton.addActionListener(e -> removeSelectedMusic());
        playMusicButton.addActionListener(e ->  play());
        stopMusicButton.addActionListener(e -> stopMusic());
        shuffleMusicButton.addActionListener(e -> shuffleMusic());
        nextMusicButton.addActionListener(e -> PlayNext());
        previousMusicButton.addActionListener(e -> playPreviousSong());


        buttonsPanel.add(addMusicButton);
        buttonsPanel.add(removeMusicButton);
        buttonsPanel.add(playMusicButton);
        buttonsPanel.add(stopMusicButton);
        buttonsPanel.add(shuffleMusicButton);
        buttonsPanel.add(nextMusicButton);
        buttonsPanel.add(previousMusicButton);

        // List to display added music files
        musicList = new JList<>(musicListModel);
        JScrollPane listScrollPane = new JScrollPane(musicList);

        panel.add(buttonsPanel, BorderLayout.NORTH);
        panel.add(listScrollPane, BorderLayout.CENTER);

        return panel;
    }
  public static DefaultListModel GetmusicListModel(){
        return musicListModel;
    }

    public void stopMusic(){player.Pause();}
    public void shuffleMusic(){
        player.shuffle();
    }
    private void PlayNext(){
        player.playNextSong();
    }
    private void playPreviousSong(){
        player.playPreviousSong();
    }
    private void play(){
        player.playSong(musicList.getSelectedValue());
    }



    private void addMusicFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Music Files", "mp3", "wav"));

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                String fileName = file.getName();
                String filePath = file.getAbsolutePath();

                try {
                    AudioFile audioFile = AudioFileIO.read(file);
                    int durationInSeconds = audioFile.getAudioHeader().getTrackLength();

                    MusicItem musicItem = new MusicItem(fileName, filePath, durationInSeconds);
                    musicListModel.addElement(musicItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            saveMusicList();
        }
    }


    private void removeSelectedMusic() {
        int selectedIndex = musicList.getSelectedIndex();
        if (selectedIndex != -1) {
            musicListModel.remove(selectedIndex);
            saveMusicList();
        }
    }

    private void loadMusicList() {

        try (BufferedReader reader = new BufferedReader(new FileReader(MUSIC_LIST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("###"); // Use a delimiter to split the line
                if (parts.length == 3) { // Check for three parts (filename, filepath, duration)
                    String fileName = parts[0];
                    String filePath = parts[1];
                    int durationInSeconds = Integer.parseInt(parts[2]); // Parse duration
                    MusicItem musicItem = new MusicItem(fileName, filePath, durationInSeconds);
                    musicListModel.addElement(musicItem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveMusicList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MUSIC_LIST_FILE))) {
            for (int i = 0; i < musicListModel.getSize(); i++) {
                MusicItem musicItem = musicListModel.getElementAt(i);
                String line = musicItem.getFileName() + "###" + musicItem.getFilePath() + "###" + musicItem.getDurationInSeconds();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ShowIpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String ip = server.getIPAddress();
            textArea.append("IP: " + ip + "\n");
            ipField.setText(ip);
        }
    }

}
