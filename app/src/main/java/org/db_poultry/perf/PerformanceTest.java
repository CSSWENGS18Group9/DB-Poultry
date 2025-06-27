package org.db_poultry.perf;

import net.datafaker.Faker;
import org.db_poultry.App;
import org.db_poultry.db.InitializeKt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PerformanceTest {
    private static void makeCSV(List<Long> posteriori) throws IOException {
        File csv = new File("posteriori.csv");

        try (PrintWriter pw = new PrintWriter(csv)) {
            pw.println("index, elapsed_time");
            for (int i = 0; i < posteriori.size(); i++) {
                pw.println(i + "," + posteriori.get(i));
            }

            System.out.println("Posteriori csv generated");
        }
    }

    public static void runTest(App app) throws IOException {
        InitializeKt.cleanTables(app.getConnection());

        Faker faker = new Faker();
        int samples = 500;

        PerfFlock pf = new PerfFlock(samples, faker, app.getConnection());
        makeCSV(pf.perfCreateFlock());
    }
}
