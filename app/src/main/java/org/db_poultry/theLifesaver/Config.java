package org.db_poultry.theLifesaver;

import java.io.*;
import java.util.HashMap;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Config {
    /**
     * writes the configuration file for the DBMS, contains important information such as the backup folder path,
     * the date of the last backup, and others
     * <p>
     * the existence of the configuration file also determines if it is the first open of the DBMS. If not exists, then
     * it is the first open.
     *
     * @param lastBackupDate the last date of backup
     */
    public static void TL_writeConfig(String lastBackupDate) {
        try {
            File f = new File(Variables.getDotConfigPath());
            try (FileWriter fw = new FileWriter(Variables.getDotConfigPath())) {
                fw.write("backup_folder_location " + Variables.getBackupFolderPath() + "\n");
                fw.write("last_backup_date " + lastBackupDate + "\n");
                fw.write("backup_interval " + Variables.getBackupIntervals() + "\n");
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_writeDotConfig` in `TL`", "FATAL. Cannot config file, due to IOException.", "", e);
        }
    }

    /**
     * loads the configuration file and returns a hashmap of each data in the config and its value.
     *
     * @return the hashmap of config data
     */
    public static HashMap<String, String> TL_loadConfig() {
        HashMap<String, String> config = new HashMap<>();

        // read the configuration file line-by-line
        try (BufferedReader br = new BufferedReader(new FileReader(Variables.getDotConfigPath()))) {
            String line;

            while ((line = br.readLine()) != null) {
                // since each line is split by a space, split the line per space and store as an array
                String[] split = line.split(" ");

                // usually the line (number of elements in split) is two, but the if statement below serves as a flexibility
                // if in the future the configuration will include a line that needs more than two "parts"
                if (split.length == 2) {
                    config.put(split[0], split[1]);
                } // add the else clause here if needed
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_loadConfig` in `TL`.", "FATAL. Cannot load file, due to IOException.", "", e);
            return null;
        }

        return config;
    }
}
