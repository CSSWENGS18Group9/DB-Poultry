package org.db_poultry.theLifesaver;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ENV {

    public static void makeENVfile() {
        try {
            Files.createFile(Paths.get(Variables.getENVFilePath())); // creates file in
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
                    "FATAL. Cannot make backup folder, due to IOException.",
                    "",
                    e
            );

        }
    }

}
