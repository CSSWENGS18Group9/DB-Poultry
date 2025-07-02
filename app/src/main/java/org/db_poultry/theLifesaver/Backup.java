package org.db_poultry.theLifesaver;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Backup {
    /**
     * creates the backup folder of the DBMS, will contain all backups of the database
     */
    public static void TL_makeBackupFolder() {
        try {
            Files.createDirectories(Paths.get(Variables.getBackupFolderPath()));
            System.out.println("~ TL ../ Backup -- Created backup folder.");

        } catch (FileAlreadyExistsException e) {
            generateErrorMessage(
                    "Error at `TL_makeBackupFolder` in `TL`",
                    "Cannot make backup folder since it exists already.",
                    "",
                    e
            );

        } catch (IOException e) {
            generateErrorMessage(
                    "Error at `TL_makeBackupFolder` in `TL`",
                    "FATAL. Cannot make backup folder, due to IOException.",
                    "",
                    e
            );

        }
    }

    /**
     * Checks the date when we last backed up, and determines if we need to make a backup
     */
    public static void TL_checkLastBackupDate(String databaseName, String databasePassword) {
        // read the config file for the last backupdate
        String lastBackUpDate;
        HashMap<String, String> config = Config.TL_loadConfig();
        if (config != null) {
            lastBackUpDate = config.get("last_backup_date");
        } else {
            generateErrorMessage(
                    "Error at `TL_checkLastBackupDate` in `TL`.",
                    "FATAL. Cannot get last_backup_date",
                    "",
                    null
            );

            return;
        }

        LocalDate d1 = LocalDate.parse(lastBackUpDate, DateTimeFormatter.ofPattern("MMMM-dd-yyyy"));
        LocalDate d2 = LocalDate.parse(Util.getDateNow(), DateTimeFormatter.ofPattern("MMMM-dd-yyyy"));

        long diff = ChronoUnit.DAYS.between(d1, d2);

        if (diff >= Variables.getBackupIntervals()) {
            // re-write the config file to show that we recently made a backup
            Config.TL_writeConfig(Util.getDateNow());

            // CREATE THE BACKUP
            TL_createDatabaseBackup(databaseName, databasePassword);

            // Delete the oldest backup in the backup folder
            TL_cleanupOldBackups();
        }

    }

    /**
     * Creates a database backup using pg_dump with custom database configuration
     * @param databaseName The name of the database to backup
     * @param databasePassword The database password  
     */
    public static void TL_createDatabaseBackup(String databaseName, String databasePassword) {
        try {
            
            // Create backup filename with current date
            String backupFileName = Variables.getBackupFilePrefix() + Util.getDateNow() + ".sql";
            Path backupFilePath = Paths.get(Variables.getBackupFolderPath(), backupFileName);
            
            // Ensure backup folder is real
            TL_makeBackupFolder();

            // Build pg_dump command
            ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-h", "localhost",  // host
                "-U", "db_poultry",  // user
                "-d", databaseName,  // database name
                "-f", backupFilePath.toString()  // output file
            );
            
            // Set password via environment variable 
            pb.environment().put("PGPASSWORD", databasePassword);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // // For debugging purposes
            // try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            //     String line;
            //     while ((line = reader.readLine()) != null) {
            //         System.out.println("pg_dump: " + line);
            //     }
            // }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("~ TL ../ Backup -- Database backup created successfully: " + backupFileName);
            } else {
                System.err.println("~ TL ../ Backup -- ERROR: pg_dump failed with exit code: " + exitCode);
                System.err.println("Check if PostgreSQL is running and credentials are correct");
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("~ TL ../ Backup -- ERROR: Failed to create database backup");
            System.err.println("Ensure pg_dump is available in PATH and database is accessible");
            e.printStackTrace();
        }
    }

    /**
     * Cleans up old backup files to maintain the backup folder capacity
     */
    public static void TL_cleanupOldBackups() {
        try {
            Path backupDir = Paths.get(Variables.getBackupFolderPath());
            
            if (!Files.exists(backupDir)) {
                return; // Nothing to clean up
            }
            
            // Get all backup files
            File[] backupFiles = backupDir.toFile().listFiles((dir, name) -> 
                name.startsWith(Variables.getBackupFilePrefix()) && name.endsWith(".sql"));
            
            // Check if cleanup is needed
             // If no files or less than capacity, no cleanup needed
            if (backupFiles == null || backupFiles.length <= Variables.getBackupFolderCapacity()) {
                return; // No cleanup needed
            }

            // Sort files by last modified date (oldest first)
            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));
            
            // Delete oldest files to maintain capacity
            int filesToDelete = backupFiles.length - Variables.getBackupFolderCapacity();
            for (int i = 0; i < filesToDelete; i++) {
                try {
                    Files.delete(backupFiles[i].toPath());
                    System.out.println("~ TL ../ Backup -- Deleted old backup: " + backupFiles[i].getName());
                } catch (IOException e) {
                    System.err.println("~ TL ../ Backup -- ERROR: Failed to delete old backup file: " + backupFiles[i].getName());
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            System.err.println("~ TL ../ Backup -- ERROR: Failed to cleanup old backup files");
            e.printStackTrace();
        }
    }

    /**
     * Restores a database from a backup file by date
     * @param backupDate The date of the backup to restore (format: "MMMM-dd-yyyy" e.g., "June-28-2025")
     * @param databaseName The name of the database to restore to
     * @param databasePassword The database password
     */
    public static void TL_restoreDatabase(String backupDate, String databaseName, String databasePassword) {
        try {
            // Construct the backup filename based on the date
            String backupFileName = Variables.getBackupFilePrefix() + backupDate + ".sql";
            Path backupFilePath = Paths.get(Variables.getBackupFolderPath(), backupFileName);
            
            // Check if the backup file exists
            if (!Files.exists(backupFilePath)) {
                System.err.println("~ TL ../ Backup -- ERROR: Backup file not found: " + backupFileName);
                System.err.println("Available backup files:");
                
                // List available backup files to help user
                Path backupDir = Paths.get(Variables.getBackupFolderPath());
                if (Files.exists(backupDir)) {
                    File[] backupFiles = backupDir.toFile().listFiles((dir, name) -> 
                        name.startsWith(Variables.getBackupFilePrefix()) && name.endsWith(".sql"));
                    
                    if (backupFiles != null && backupFiles.length > 0) {
                        for (File file : backupFiles) {
                            System.err.println("  - " + file.getName());
                        }
                    } else {
                        System.err.println("  No backup files found in backup directory");
                    }
                }
                return;
            }
            
            System.out.println("~ TL ../ Backup -- Restoring database from: " + backupFileName);
            
            // Clear the database before restoration
                    System.out.println("~ TL ../ Backup -- Cleaning existing tables before restore...");
            ProcessBuilder cleanPb = new ProcessBuilder(
                "psql",
                "-h", "localhost",
                "-U", "db_poultry",
                "-d", databaseName,
                "-c", "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
            );
            cleanPb.environment().put("PGPASSWORD", databasePassword);
            Process cleanProcess = cleanPb.start();
            int cleanExitCode = cleanProcess.waitFor();
            
            if (cleanExitCode != 0) {
                System.err.println("~ TL ../ Backup -- WARNING: Failed to clean database before restore");
            }

            // Build psql command for restoration
            ProcessBuilder pb = new ProcessBuilder(
                "psql",
                "-X",  // Don't read startup files
                "--set", "ON_ERROR_STOP=on",  // Stop on errors
                "-h", "localhost",  // host
                "-U", "db_poultry",  // user
                "-d", databaseName,  // database name
                "-f", backupFilePath.toString()  // input file
            );
            
            // Set password via environment variable
            pb.environment().put("PGPASSWORD", databasePassword);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("~ TL ../ Backup -- Database restored successfully from: " + backupFileName);
            } else {
                System.err.println("~ TL ../ Backup -- ERROR: psql failed with exit code: " + exitCode);
                System.err.println("Check if the backup file is valid and database is accessible");
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("~ TL ../ Backup -- ERROR: Failed to restore database from backup");
            System.err.println("Ensure psql is available in PATH and database is accessible");
            e.printStackTrace();
        }
    }

    /**
     * Restores a database from the most recent backup
     * @param databaseName The name of the database to restore to
     * @param databasePassword The database password
     */
    public static void TL_restoreDatabaseFromLatest(String databaseName, String databasePassword) {
        try {
            Path backupDir = Paths.get(Variables.getBackupFolderPath());
            
            if (!Files.exists(backupDir)) {
                System.err.println("~ TL ../ Backup -- ERROR: Backup directory not found: " + Variables.getBackupFolderPath());
                return;
            }
            
            // Get all backup files
            File[] backupFiles = backupDir.toFile().listFiles((dir, name) -> 
                name.startsWith(Variables.getBackupFilePrefix()) && name.endsWith(".sql"));
            
            if (backupFiles == null || backupFiles.length == 0) {
                System.err.println("~ TL ../ Backup -- ERROR: No backup files found in backup directory");
                return;
            }
            
            // Sort files by last modified date (newest first)
            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified).reversed());
            
            // Get the most recent backup file
            File latestBackup = backupFiles[0];
            
            System.out.println("~ TL ../ Backup -- Using latest backup: " + latestBackup.getName());
            
            // Extract date from filename for the main restore method
            String fileName = latestBackup.getName();
            String dateFromFilename = fileName.replace(Variables.getBackupFilePrefix(), "").replace(".sql", "");
            
            // Call the original function with the extracted date
            TL_restoreDatabase(dateFromFilename, databaseName, databasePassword);
            
        } catch (Exception e) {
            System.err.println("~ TL ../ Backup -- ERROR: Failed to find latest backup file");
            e.printStackTrace();
        }
    }
}
