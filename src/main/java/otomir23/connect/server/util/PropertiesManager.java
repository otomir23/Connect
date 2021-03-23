package otomir23.connect.server.util;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

public class PropertiesManager {
    private HashMap<String, String> properties;
    private static final Logger LOGGER = new Logger("PropertiesReader");

    public PropertiesManager(File propertiesFile) {
        properties = new HashMap<>();
        properties = readPropertiesFile(propertiesFile);
    }

    public String getProperty(String key) {
        String defaultValue = null;
        try {
            File temp = File.createTempFile("resource", "temp");
            InputStream resource = PropertiesManager.class.getResourceAsStream("/server.properties");

            FileOutputStream fos = new FileOutputStream(temp);
            byte[] buff = new byte[65536];
            int n;
            while ((n = resource.read(buff)) > 0) {
                fos.write(buff, 0, n);
                fos.flush();
            }
            fos.close();
            buff = null;

            HashMap<String, String> defaults = readPropertiesFile(temp);
            defaultValue = defaults.getOrDefault(key, null);
        } catch (IOException e) {
            LOGGER.fatal(e, -1);
        }

        return properties.getOrDefault(key, defaultValue);
    }

    private HashMap<String, String> readPropertiesFile(File propertiesFile) {
        try {
            HashMap<String, String> properties = new HashMap<>();
            if (propertiesFile.exists()) {
                if (propertiesFile.isFile()) {
                    Scanner sc = new Scanner(propertiesFile);
                    String propertiesFileContent = "";
                    while (sc.hasNextLine()) {
                        propertiesFileContent += sc.nextLine();
                    }

                    String[] propertiesFileContent1 = propertiesFileContent.split("\n");

                    for (String line : propertiesFileContent1) {
                        String[] property = line.split(":");
                        if (property.length != 2) {
                            continue;
                        }
                        property[1] = property[1].replaceAll(" ", "");
                        properties.put(property[0], property[1]);
                    }

                    LOGGER.log("Properties initialized.");
                } else {
                    LOGGER.error("Invalid properties file/path!");
                }
            } else {
                LOGGER.warn("Properties file does not exists. Creating a new one.");
                propertiesFile.createNewFile();

                InputStream resource = PropertiesManager.class.getResourceAsStream("/server.properties");
                try {
                    FileOutputStream fos = new FileOutputStream(propertiesFile);
                    byte[] buff = new byte[65536];
                    int n;
                    while ((n = resource.read(buff)) > 0) {
                        fos.write(buff, 0, n);
                        fos.flush();
                    }
                    fos.close();
                    buff = null;
                } catch (Exception e) {
                    LOGGER.fatal(e, -1);
                }

                return readPropertiesFile(propertiesFile);
            }
            return properties;
        } catch (IOException ioe) {
            LOGGER.fatal(ioe.getLocalizedMessage(), -1);
        }
        return new HashMap<>();
    }
}
