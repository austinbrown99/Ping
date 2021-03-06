import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

public class ping {
    private static File selectedFolder;
    private static final ArrayList<String> pingResults = new ArrayList<>();
    private static long failedPings = 0;

    public static void main(String[] args) {
        //Create Window
        JFrame frame = new JFrame("Ping Sweep V1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(475, 210);
        JPanel panel = new JPanel();
        panel.setLayout(null);

        //Create objects
        JRadioButton singleAddress = new JRadioButton("Single Address");
        singleAddress.setBounds(15, 15, 150, 20);

        JTextField singleAddressIP = new JTextField("127.0.0.1");
        singleAddressIP.setBounds(175, 15, 115, 20);
        singleAddressIP.setEnabled(false);

        JRadioButton rangeAddress = new JRadioButton("Address Range");
        rangeAddress.setBounds(15, 40, 150, 20);

        JTextField rangeAddressIP1 = new JTextField();
        rangeAddressIP1.setBounds(175, 40, 115, 20);
        rangeAddressIP1.setEnabled(false);

        JTextField rangeAddressIP2 = new JTextField();
        rangeAddressIP2.setBounds(320, 40, 115, 20);
        rangeAddressIP2.setEnabled(false);

        JLabel dashLabel = new JLabel("-");
        dashLabel.setBounds(305, 40, 10, 20);

        JRadioButton subnetAddress = new JRadioButton("Subnet Address");
        subnetAddress.setBounds(15, 65, 150, 20);

        JTextField subnetAddressIP = new JTextField();
        subnetAddressIP.setBounds(175, 65, 115, 20);
        subnetAddressIP.setEnabled(false);

        JLabel maskLabel = new JLabel("/");
        maskLabel.setBounds(305, 65, 10, 20);

        JTextField subnetMask = new JTextField();
        subnetMask.setBounds(320, 65, 30, 20);
        subnetMask.setDocument(new JTextFieldLimit(2));
        subnetMask.setEnabled(false);

        JRadioButton fileAddress = new JRadioButton("From File");
        fileAddress.setBounds(15, 90, 150, 20);

        JButton fileAddressIP = new JButton("Choose File");
        fileAddressIP.setBounds(175, 90, 115, 20);
        fileAddressIP.setEnabled(false);

        JLabel folderLabel = new JLabel();
        folderLabel.setText("Folder Selected: ");
        folderLabel.setBounds(300, 90, 500, 20);

        JButton startButton = new JButton("Start Pinging");
        startButton.setBounds(15, 115, 115, 40);

        //Add objects to panel
        panel.add(singleAddress);
        panel.add(singleAddressIP);
        panel.add(rangeAddress);
        panel.add(rangeAddressIP1);
        panel.add(rangeAddressIP2);
        panel.add(dashLabel);
        panel.add(subnetAddress);
        panel.add(maskLabel);
        panel.add(subnetAddressIP);
        panel.add(subnetMask);
        panel.add(fileAddress);
        panel.add(fileAddressIP);
        panel.add(folderLabel);
        panel.add(startButton);

        //Make frame appear
        frame.setContentPane(panel);
        frame.setVisible(true);

        //Button Listeners
        singleAddress.addActionListener(e -> {
            singleAddressIP.setEnabled(!singleAddressIP.isEnabled());
            rangeAddress.setSelected(false);
            rangeAddressIP1.setEnabled(false);
            rangeAddressIP2.setEnabled(false);
            subnetAddress.setSelected(false);
            subnetAddressIP.setEnabled(false);
            subnetMask.setEnabled(false);
            fileAddress.setSelected(false);
            fileAddressIP.setEnabled(false);
        });
        rangeAddress.addActionListener(e -> {
            singleAddressIP.setEnabled(false);
            singleAddress.setSelected(false);
            rangeAddressIP1.setEnabled(!rangeAddressIP1.isEnabled());
            rangeAddressIP2.setEnabled(!rangeAddressIP2.isEnabled());
            subnetAddress.setSelected(false);
            subnetAddressIP.setEnabled(false);
            subnetMask.setEnabled(false);
            fileAddress.setSelected(false);
            fileAddressIP.setEnabled(false);
        });
        subnetAddress.addActionListener(e -> {
            singleAddressIP.setEnabled(false);
            singleAddress.setSelected(false);
            rangeAddressIP1.setEnabled(false);
            rangeAddressIP2.setEnabled(false);
            rangeAddress.setSelected(false);
            subnetAddressIP.setEnabled(!subnetAddressIP.isEnabled());
            subnetMask.setEnabled(!subnetMask.isEnabled());
            fileAddress.setSelected(false);
            fileAddressIP.setEnabled(false);
        });
        fileAddress.addActionListener(e -> {
            singleAddressIP.setEnabled(false);
            singleAddress.setSelected(false);
            rangeAddressIP1.setEnabled(false);
            rangeAddressIP2.setEnabled(false);
            rangeAddress.setSelected(false);
            subnetAddress.setSelected(false);
            subnetAddressIP.setEnabled(false);
            subnetMask.setEnabled(false);
            fileAddressIP.setEnabled(!fileAddressIP.isEnabled());
        });
        fileAddressIP.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFolder = fileChooser.getSelectedFile();
                folderLabel.setText("Folder Selected: " + selectedFolder);
            }
        });
        startButton.addActionListener(e -> {
            pingResults.clear();
            failedPings = 0;

            try {
                if (singleAddress.isSelected()) {
                    sendPingRequest(singleAddressIP.getText());
                } else if (rangeAddress.isSelected()) {
                    ipRange(rangeAddressIP1.getText(), rangeAddressIP2.getText());
                } else if (subnetAddress.isSelected()) {
                    subnet(subnetAddressIP.getText(), subnetMask.getText());
                } else if (fileAddress.isSelected()) {
                    if (selectedFolder == null) {
                        JOptionPane.showMessageDialog(frame, "Please select a Folder", "Folder Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    fileAddresses(selectedFolder);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an option", "Folder Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            //Create Window
            JFrame resultFrame = new JFrame("Ping Result");
            resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            resultFrame.setSize(735, 350);
            JPanel resultPanel = new JPanel();
            resultPanel.setLayout(null);
            resultFrame.setContentPane(resultPanel);

            //Total Scanned Label
            JLabel totalLabel = new JLabel();
            totalLabel.setText(pingResults.size() + " IPs Pinged");
            totalLabel.setBounds(200, 220, 150, 37);
            totalLabel.setVerticalAlignment(SwingConstants.CENTER);
            totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

            //Total Problems
            JLabel totalProblems = new JLabel();
            totalProblems.setText(failedPings + " failed");
            totalProblems.setBounds(200, 257, 150, 37);
            totalProblems.setVerticalAlignment(SwingConstants.CENTER);
            totalProblems.setHorizontalAlignment(SwingConstants.CENTER);

            //Copy Button
            StringBuilder problemCopyString = new StringBuilder();
            for (String i : pingResults) {
                problemCopyString.append("\n").append(i);
            }
            JButton copyButton = new JButton("Copy");
            copyButton.setBounds(560, 220, 150, 75);
            String finalProblemCopyString = problemCopyString.toString();
            copyButton.addActionListener(e1 -> {
                StringSelection stringSelection = new StringSelection(finalProblemCopyString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            });

            //Create File Button
            JButton createFile = new JButton("Create \n File");
            createFile.setBounds(400, 220, 150, 75);
            createFile.addActionListener(e1 -> {
                //Select location for file
                File selectedResultsFolder = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    selectedResultsFolder = fileChooser.getSelectedFile();
                }

                if (selectedResultsFolder == null) {
                    JOptionPane.showMessageDialog(frame, "Please select a Folder", "Folder Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                File resultFile = new File(selectedResultsFolder + "\\result.txt");
                try {
                    boolean result = resultFile.createNewFile();
                    FileWriter writer = new FileWriter(resultFile);
                    if (result) { //Do something if the file doesn't exist when creating it
                        for (String str : pingResults) {
                            writer.write(str + System.lineSeparator());
                        }
                    } else { //Do something if the file already exists when creating it
                        //I know this is the same as above, but I don't care to make it better. It works this way
                        for (String str : pingResults) {
                            writer.write(str + System.lineSeparator());
                        }
                    }
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            //List of Pings
            JList<String> displayList = new JList<>(pingResults.toArray(new String[0]));
            JScrollPane scrollPane = new JScrollPane(displayList);
            scrollPane.setBounds(10, 10, 700, 200);

            //Close Button
            JButton resultsClose = new JButton("Close");
            resultsClose.setBounds(10, 220, 150, 75);
            resultsClose.addActionListener(e1 -> {
                resultFrame.getContentPane().remove(scrollPane);
                resultFrame.getContentPane().remove(resultFrame);
                resultFrame.dispose();
            });

            resultFrame.add(resultsClose);
            resultFrame.add(scrollPane);
            resultFrame.add(totalLabel);
            resultFrame.add(totalProblems);
            resultFrame.add(copyButton);
            resultFrame.add(createFile);
            resultFrame.setVisible(true);
        });
    }

    public static void sendPingRequest(String ipAddress) throws IOException {
        InetAddress address = InetAddress.getByName(ipAddress);
        System.out.println("Sending Ping Request to " + ipAddress);
        if (address.isReachable(3000)) {
            System.out.println("Host is Reachable");
            pingResults.add(ipAddress + " is Reachable");
        } else {
            System.out.println("test");
            if (!address.isReachable(3000)) {
                System.out.println("Sorry! We can't Reach to this host");
                pingResults.add(ipAddress + " We can't reach this host");
                failedPings++;
            }
        }
    }

    public static void ipRange(String firstAddress, String secondAddress) throws IOException {
        IPAddress start = new IPAddressString(firstAddress).getAddress();
        IPAddress end = new IPAddressString(secondAddress).getAddress();
        IPAddressSeqRange ipRange = start.toSequentialRange(end);

        Iterator<? extends IPAddress> iterator = ipRange.stream().iterator();
        while (iterator.hasNext()) {
            sendPingRequest(String.valueOf(iterator.next()));
        }
    }

    public static void subnet(String address, String mask) throws IOException {
        IPAddress ipa = new IPAddressString(address).getAddress().setPrefixLength(Integer.parseInt(mask));

        Iterator<? extends IPAddress> iterator = ipa.stream().iterator();
        while (iterator.hasNext()) {
            String ipAddress = String.valueOf(iterator.next());
            sendPingRequest(ipAddress.substring(0, ipAddress.indexOf("/")));
        }
    }

    public static void fileAddresses(File selectedFolder) throws IOException {
        FileReader input = new FileReader(selectedFolder);
        BufferedReader bufRead = new BufferedReader(input);
        String myLine;

        while ((myLine = bufRead.readLine()) != null) {
            sendPingRequest(myLine);
        }
    }
}
