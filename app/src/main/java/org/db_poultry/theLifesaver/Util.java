package org.db_poultry.theLifesaver;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Util {
    /**
     * gets the date right now in the format required by the TL
     *
     * @return the formatted date
     */
    public static String getDateNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-dd-yyyy");

        return now.format(formatter);
    }

    public static void makeSupplyTypeImagesDirectory() {
        try {
            Files.createDirectories(Paths.get(Variables.getSTImageFolderName()));
        } catch (FileAlreadyExistsException e) {
            generateErrorMessage(
                    "Error at `makeSupplyTypeImagesDirectory` in `TL`",
                    "Cannot make Supply Type Images Directory since it exists already.",
                    "",
                    e
            );

        } catch (IOException e) {
            generateErrorMessage(
                    "Error at `makeSupplyTypeImagesDirectory` in `TL`",
                    "FATAL. Cannot Supply Type Images Directory folder, due to IOException.",
                    "",
                    e
            );

        }
    }
}
