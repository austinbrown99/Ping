import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;

public class ping {
    public static void main(String[] args) throws IOException {
        //Create Window
        JFrame frame = new JFrame("Ping Sweep");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 200);
        JPanel panel = new JPanel();
        panel.setLayout(null);

        //Create objects
        String singleString = "Single Address";
        JRadioButton singleAddress = new JRadioButton(singleString);
        singleAddress.setActionCommand(singleString);
        singleAddress.setBounds(15,15,150,20);

        JTextField singleAddressIP = new JTextField("255.255.255.255");
        singleAddressIP.setBounds(175,15,115,20);
        singleAddressIP.setEnabled(false);

        JTextField rangeAddressIP1 = new JTextField();
        rangeAddressIP1.setBounds(175,40,115,20);
        rangeAddressIP1.setEnabled(false);

        //Add objects to panel
        panel.add(singleAddress);
        panel.add(singleAddressIP);
        panel.add(rangeAddressIP1);

        //Make frame appear
        frame.setContentPane(panel);
        frame.setVisible(true);

        //Button Listeners
        singleAddress.addActionListener(e -> singleAddressIP.setEnabled(!singleAddressIP.isEnabled()));

        String ipAddress = "200.1.25.3";
    }

    public static void sendPingRequest(String ipAddress) throws IOException {
        InetAddress address = InetAddress.getByName(ipAddress);
        System.out.println("Sending Ping Request to " + ipAddress);
        if (address.isReachable(5000)) {
            System.out.println("Host is Reachable");
        } else {
            System.out.println("Sorry! We can't Reach to this host");
        }
    }
}
