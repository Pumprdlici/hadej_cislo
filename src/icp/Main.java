package icp;

import icp.application.OnlineDetection;
import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;
import icp.gui.SetupDialogContent;
import icp.online.app.OnLineDataProvider;
import icp.online.gui.MainFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class Main {

    public static void main(String[] args) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 150));

        JTextField ipAddrField = new JTextField(Const.DEF_IP_ADDRESS);
        JComboBox portBox = new JComboBox(new DefaultComboBoxModel(Const.DEF_PORTS));
        JLabel error = new JLabel("IP is not valid");
        error.setForeground(Color.red);
        //error.setVisible(false);
        JLabel ipLabel = new JLabel("IP Address");
        JLabel portLabel = new JLabel("Port");
        JLabel connectionLabel = new JLabel("Connection Setup");
        Font f = connectionLabel.getFont();
        connectionLabel.setFont(new Font(f.getName(), Font.BOLD, 14));

        JSeparator separ = new JSeparator(JSeparator.HORIZONTAL);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        panel.add(connectionLabel, c);

        c.gridx = 1;
        c.weightx = 1;
        panel.add(separ, c);

        c.gridx = 2;
        c.weightx = 0.3;
        panel.add(ipLabel, c);

        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.4;
        panel.add(ipAddrField, c);

        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.3;
        panel.add(error, c);

        final JComponent[] inputs = new JComponent[]{
            new JLabel("IP Address"),
            ipAddrField,
            error,
            new JLabel("Port"),
            portBox,};

        SetupDialogContent content = new SetupDialogContent();

        int result;
        boolean isOk = false;
        String recorderIPAddress = null;
        int port = -1;
        while (!isOk) {
            result = JOptionPane.showConfirmDialog(null, content, "Guess the Number: Setup", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION) {
                System.exit(result);
            }

            recorderIPAddress = content.getIP();
            port = content.getPort();
            if (port != -1 && recorderIPAddress != null) {
                isOk = true;
            } else {
                if (port == -1) {
                    JOptionPane.showMessageDialog(null, "Invalid port number!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                if (recorderIPAddress == null) {
                    JOptionPane.showMessageDialog(null, "Invalid IP address!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        IERPClassifier classifier = new MLPClassifier();
        classifier.load("data/classifier.txt");
        IFeatureExtraction fe = new FilterFeatureExtraction();
        classifier.setFeatureExtraction(fe);

        MainFrame gui = new MainFrame();
        OnlineDetection detection = new OnlineDetection(classifier, gui);

        //String recorderIPAddress = "147.228.127.95";
        //int port = 51244;
        OnLineDataProvider odp = new OnLineDataProvider(recorderIPAddress, port, detection);

    }
}
