package otomir23.connect.server;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;

public class PropertiesReader {
    private HashMap<String, String> properties;
    private static final Logger LOGGER = new Logger("PropertiesReader");

    PropertiesReader(File propertiesFile) {
        properties = new HashMap<>();
        readPropertiesFile(propertiesFile);
    }

    public String getProperty(String key) {
        InputStream lang = PropertiesReader.class.getResourceAsStream("/server.properties");
        String s = "";
        String defaultValue = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[65536];
            int n;
            while ((n = lang.read(buff)) > 0) {
                baos.write(buff, 0, n);
                baos.flush();
            }
            baos.close();
            buff = null;
            s = baos.toString();
        } catch (Exception e) {
            LOGGER.fatal(e, -1);
        }

        String[] propertiesFileContent1 = s.split("\n");

        for (String line : propertiesFileContent1) {
            String[] property = line.split(":");
            if (property.length != 2) {
                continue;
            }
            property[1] = property[1].replaceAll("\\s+", "");
            if(property[0].equalsIgnoreCase(key)) defaultValue = property[1];
        }

        return properties.getOrDefault(key, defaultValue);
    }

    private void readPropertiesFile(File propertiesFile) {
        try {
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
                        property[1] = property[1].replaceAll("\\s+", "");
                        properties.put(property[0], property[1]);
                    }

                    LOGGER.log("Properties initialized.");
                } else {
                    LOGGER.error("Invalid properties file/path!");
                }
            } else {
                LOGGER.warn("Properties file does not exists. Creating a new one.");
                propertiesFile.createNewFile();

                InputStream lang = PropertiesReader.class.getResourceAsStream("/server.properties");
                try {
                    FileOutputStream fos = new FileOutputStream(propertiesFile);
                    byte[] buff = new byte[65536];
                    int n;
                    while ((n = lang.read(buff)) > 0) {
                        fos.write(buff, 0, n);
                        fos.flush();
                    }
                    fos.close();
                    buff = null;
                } catch (Exception e) {
                    LOGGER.fatal(e, -1);
                }

                readPropertiesFile(propertiesFile);
            }
        } catch (IOException ioe) {
            LOGGER.fatal(ioe.getLocalizedMessage(), -1);
        }
    }
}
