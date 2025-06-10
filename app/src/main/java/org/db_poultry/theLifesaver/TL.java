package org.db_poultry.theLifesaver;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static org.db_poultry.db.InitializeKt.cleanTables;
import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

/*
 * dotConfigFile contents:
 * ==========
 * backup_folder_location dbp_backups
 * last_backup_date October-01-2004
 * backup_intervals 3
 * ==========
 * all are separated by a SINGLE space!
 */
public class TL {
    // ================ DOT CONFIG VARIABLES ============
    private static final String DOT_CONFIG_PATH = ".config.dbpoultry";

    // ================ BACKUP VARIABLES ================
    private static final String BACKUP_FOLDER_PATH = "dbp_backups";
    private static final int BACKUP_INTERVALS = 5; // every X days, we create a backup
    private static final String BACKUP_FILE_PREFIX = "dbp_backup_"; // what comes after this is the date
    private static final int BACKUP_FOLDER_CAPACITY = 10; // the number of backups we want to store at a time

    /**
     * gets the date right now in the format required by the TL
     *
     * @return the formatted date
     */
    private static String getDateNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-dd-yyyy");

        return now.format(formatter);
    }

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
            File f = new File(DOT_CONFIG_PATH);
            try (FileWriter fw = new FileWriter(DOT_CONFIG_PATH)) {
                fw.write("backup_folder_location " + BACKUP_FOLDER_PATH + "\n");
                fw.write("last_backup_date " + lastBackupDate + "\n");
                fw.write("backup_interval " + BACKUP_INTERVALS + "\n");
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
        try (BufferedReader br = new BufferedReader(new FileReader(DOT_CONFIG_PATH))) {
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

    /**
     * creates the backup folder of the DBMS, will contain all backups of the database
     */
    public static void TL_makeBackupFolder() {
        try {
            Files.createDirectories(Paths.get(BACKUP_FOLDER_PATH));
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
        HashMap<String, String> config = TL_loadConfig();
        if (config != null) {
            lastBackUpDate = config.get("last_backup_date");
        } else {
            generateErrorMessage("Error at `TL_checkLastBackupDate` in `TL`.", "FATAL. Cannot get last_backup_date", "", null);
            return;
        }

        LocalDate d1 = LocalDate.parse(lastBackUpDate, DateTimeFormatter.ofPattern("MMMM-dd-yyyy"));
        LocalDate d2 = LocalDate.parse(getDateNow(), DateTimeFormatter.ofPattern("MMMM-dd-yyyy"));

        long diff =  ChronoUnit.DAYS.between(d1, d2);

        if (diff >= BACKUP_INTERVALS) {
            // re-write the config file to show that we recently made a backup
            TL_writeConfig(getDateNow());

            // CREATE THE BACKUP
            // todo: add the psql call to create the backup file and put it inside the backup folder

            // Delete the oldest backup in the backup folder
            // todo: we don't want a fuck ton of files inside the backup folder, so only keep a couple
        }
    }

    /**
     * deletes backup folder and the config file, essentially makes it so that the next open of the DBMS is the first
     * open
     */
    public static void TL_cleanAll() {
        // Delete the backup folder recursively
        try {
            Path backupFolderPath = Paths.get(BACKUP_FOLDER_PATH);

            if (Files.exists(backupFolderPath)) {
                Files.walkFileTree(backupFolderPath, new SimpleFileVisitor<>() {
                    @NotNull
                    @Override
                    public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @NotNull
                    @Override
                    public FileVisitResult postVisitDirectory(@NotNull Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println(">>> theLifesaver has deleted the backups folder.");
            } else {
                System.out.println(">>> theLifesaver's backups folder does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_murder` in `TL`", "Failed to delete backup folder.", "", e);
        }

        // Delete the config file
        try {
            Path configFilePath = Paths.get(DOT_CONFIG_PATH);

            if (Files.exists(configFilePath)) {
                Files.delete(configFilePath);
                System.out.println(">>> theLifesaver has deleted the config file.");
            } else {
                System.out.println(">>> theLifesaver's config file does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_murder` in `TL`", "Failed to delete config file.", "", e);
        }

        System.out.println("=================== IMPORTANT! ===================");
        System.out.println("=== IF YOU SEE THIS IN SHIPPED CODE            ===");
        System.out.println("=== THEN SOMEBODY MESSED UP                    ===");
        System.out.println("=== YOU SHOULDN'T BE ABLE TO SEE THIS          ===");
        System.out.println("=== REMEMBER TO DELETE `TL_cleanAll`           ===");
        System.out.println("=== BEFORE SHIPPING                            ===");
        System.out.println("=================== IMPORTANT! ===================");
    }

    /**
     * checks if it is the first open and if it is create the configuration file.
     */
    public static void TL_firstOpen(Connection conn) {
        // the basis of checking for the "first open" of the DBMS is if the `.config.dbpoultry` exists
        if (!Files.exists(Paths.get(DOT_CONFIG_PATH))) {
            // if it doesn't. Make it immediately!
            System.out.println(">>> theLifesaver detects: first open.");
            TL_writeConfig(getDateNow());
        } else {
            return;
        }

        // make the backup folder
        TL_makeBackupFolder();

        // if it is the first open, we will clean all tables
        cleanTables(conn);

        // we also want to set the PostgreSQL privileges
        // TODO: make commandline calls here to give PostgreSQL privilages
        // do we even need that? that's like too complicated i think...
    }
}
