package org.db_poultry.perf;

import net.datafaker.Faker;

public class Perf {
    protected final int samples;
    protected final Faker faker;
    protected final java.sql.Connection conn;

    public Perf(int samples, Faker faker, java.sql.Connection conn) {
        this.samples = samples;
        this.faker = faker;
        this.conn = conn;
    }
}
