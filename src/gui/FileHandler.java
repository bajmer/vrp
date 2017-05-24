package gui;

import org.apache.commons.lang3.StringUtils;
import project.Client;
import project.ClientsHandler;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by mbala on 24.05.17.
 */

public class FileHandler {

    private final String separator = ";";

    public FileHandler() {

    }

    public File chooseFile(MyWindow parentWindow) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        //fileChooser.setFileFilter(new FileNameExtensionFilter(".csv", "csv"));
        //fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
        int result = fileChooser.showOpenDialog(parentWindow);
        File selectedFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("File is not selected.");
        }
        return selectedFile;
    }

    public void readFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] fields = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, separator);
                double lat = Double.parseDouble(fields[0]);
                double lon = Double.parseDouble(fields[1]);
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    System.out.println("Invalid client data in line: " + lineNumber);
                    continue;
                }
                Client client = new Client(lat, lon);
                ClientsHandler.getClientsList().add(client);
                System.out.println("Szer: " + client.getLatitude() + "  Dl: " + client.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
