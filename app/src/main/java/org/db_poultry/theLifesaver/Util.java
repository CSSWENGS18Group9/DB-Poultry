package org.db_poultry.theLifesaver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}
