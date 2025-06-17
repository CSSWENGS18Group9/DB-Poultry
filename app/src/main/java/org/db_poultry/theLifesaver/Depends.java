package org.db_poultry.theLifesaver;

public class Depends {
    // the filenames of the dependencies inside the app/installer directory
    // don't add the directory `installer/` as `installer\\` since we will append that
    // in their getter functions
    private static final String postgres = "postgresql-17.5.exe";

    public static String getPostgres() {
        return Paths.get("installer", postgres).toString();
    }
}
