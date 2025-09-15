package org.db_poultry.theLifesaver;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ENV {

    public static void makeENVfile() {
        try {
            Path envFolderPath = Variables.getENVFilePath();

            if (envFolderPath == null) { // checks .db_poultry exists
                generateErrorMessage(
                        "Error at `makeENVfile` in `ENV`",
                        ".db_poultry folder missing.",
                        "",
                        null
                );
                return;
            }

            Files.createFile(envFolderPath); // creates file in "Username"/.db_poultry
            System.out.println("Created ENV file.");

        } catch (FileAlreadyExistsException e) {
            generateErrorMessage(
                    "Error at `makeENVfile` in `ENV`",
                    "Cannot make ENV file since it exists already.",
                    "",
                    e
            );

        } catch (IOException e) {
            generateErrorMessage(
                    "Error at `makeENVfile` in `ENV`",
                    "FATAL. Cannot make file due to IOException.",
                    "",
                    e
            );

        }
    }

}
