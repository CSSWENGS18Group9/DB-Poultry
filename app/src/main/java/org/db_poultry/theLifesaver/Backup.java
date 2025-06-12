package org.db_poultry.theLifesaver;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Backup {
    /**
     * creates the backup folder of the DBMS, will contain all backups of the database
     */
    public static void TL_makeBackupFolder() {
        try {
            Files.createDirectories(Paths.get(Variables.getBackupFolderPath()));
            System.out.println(">>> theLifesaver has created the backups folder.");
        } catch (FileAlreadyExistsException e) {
            generateErrorMessage("Error at `TL_makeBackupFolder` in `TL`", "Cannot make backup folder since it exists already.", "", e);
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_makeBackupFolder` in `TL`", "FATAL. Cannot make backup folder, due to IOException.", "", e);
        }
    }

    /**
     * Checks the date when we last backed up, and determines if we need to make a backup
     */
    public static void TL_checkLastBackupDate() {
        // read the config file for the last backupdate
        String lastBackUpDate;
        HashMap<String, String> config = Config.TL_loadConfig();
        if (config != null) {
            lastBackUpDate = config.get("last_backup_date");
        } else {
            generateErrorMessage("Error at `TL_checkLastBackupDate` in `TL`.", "FATAL. Cannot get last_backup_date", "", null);
            return;
        }

        LocalDate d1 = LocalDate.parse(lastBackUpDate, DateTimeFormatter.ofPattern("MMMM-dd-yyyy"));
        LocalDate d2 = LocalDate.parse(Util.getDateNow(), DateTimeFormatter.ofPattern("MMMM-dd-yyyy"));

        long diff = ChronoUnit.DAYS.between(d1, d2);

        if (diff >= Variables.getBackupIntervals()) {
            // re-write the config file to show that we recently made a backup
            Config.TL_writeConfig(Util.getDateNow());

            // CREATE THE BACKUP
            // todo: add the psql call to create the backup file and put it inside the backup folder

            // Delete the oldest backup in the backup folder
            // todo: we don't want a fuck ton of files inside the backup folder, so only keep a couple
        }
    }
}
