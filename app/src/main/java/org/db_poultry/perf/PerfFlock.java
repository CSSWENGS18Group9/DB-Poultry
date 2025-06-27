package org.db_poultry.perf;

import net.datafaker.Faker;
import org.db_poultry.db.flockDAO.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PerfFlock {
    private final int samples;
    private final Faker faker;
    private final java.sql.Connection conn;
    private final List<java.sql.Date> dates;

    PerfFlock(int samples, Faker faker, java.sql.Connection conn) {
        this.samples = samples;
        this.faker = faker;
        this.conn = conn;
        this.dates = generateDates();
    }

    private List<java.sql.Date> generateDates() {
        List<java.sql.Date> dates = new ArrayList<>();

        for (int i = 0; i < this.samples; i++) {
            // 5 years ≈ 1825 days
            java.util.Date randomPastDate = faker.date().past(1825, TimeUnit.DAYS);
            dates.add(new java.sql.Date(randomPastDate.getTime()));
        }

        return dates;
    }

    public List<Long> perfCreateFlock(){
        List<Long> posteriori = new ArrayList<>();

        for (int i = 0; i < this.samples; i++) {
            long startTime = System.nanoTime();

            // Generate fake data
            int startingCount = this.faker.number().numberBetween(1, 256);

            // Insert
//            System.out.println(this.dates.get(i));
            CreateFlock.createFlock(conn, startingCount, this.dates.get(i));

            long endTime = System.nanoTime();
            posteriori.add(endTime - startTime);
        }

        return posteriori;
    }

    public List<Long> perfFlockFromADate(){
        List<Long> posteriori = new ArrayList<>();

        for (int i = 0; i < samples; i++) {
            long startTime = System.nanoTime();

            // Read
            ReadFlock.getFlockFromADate(this.conn, this.dates.get(i));

            long endTime = System.nanoTime();
            posteriori.add(endTime - startTime);
        }

        return posteriori;
    }

    public List<Long> perfFlocksFromDate(){
        List<Long> posteriori = new ArrayList<>();

        for (int i = 0; i < samples; i++) {
            long startTime = System.nanoTime();

            // Read
            ReadFlock.getFlocksFromDate(this.conn, this.dates.get(i), java.sql.Date.valueOf(LocalDate.now()));

            long endTime = System.nanoTime();
            posteriori.add(endTime - startTime);
        }

        return posteriori;
    }
}
