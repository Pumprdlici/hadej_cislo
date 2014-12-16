package icp;

import icp.application.OnlineDetection;
import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;
import icp.gui.SetupDialogContent;
import icp.online.app.OnLineDataProvider;
import icp.online.gui.MainFrame;
import javax.swing.JOptionPane;

/**
 * Hlavni spousteci trida aplikace
 */
public class Main {

    public static void main(String[] args) {
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

        OnLineDataProvider odp = new OnLineDataProvider(recorderIPAddress, port, detection);

    }
}
