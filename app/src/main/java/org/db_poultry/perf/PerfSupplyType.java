package org.db_poultry.perf;

import net.datafaker.Faker;
import org.db_poultry.db.supplyTypeDAO.CreateSupplyType;
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PerfSupplyType extends Perf {
    private HashMap<String, String> supplies = new HashMap<>();

    PerfSupplyType(int samples, Faker faker, java.sql.Connection conn) {
        super(samples, faker, conn);
        this.supplies = generateSupplies();
    }

    HashMap<String, String> generateSupplies() {
        HashMap<String, String> supplies = new HashMap<>();

        for (int i = 0; i < samples; i++) {
            // generate a random int from 12 and 13 to allow for failed unit insertions
            int randomInt = ThreadLocalRandom.current().nextInt(12, 14);
            String unit = faker.lorem().characters(randomInt, false);
            String name = faker.lorem().word();

            supplies.put(name, unit);
        }

        return supplies;
    }

    public List<Long> perfCreateSupply() {
        List<Long> posteriori = new ArrayList<>();

        for (Map.Entry<String, String> entry : supplies.entrySet()) {
            long startTime = System.nanoTime();

            String name = entry.getKey();
            String unit = entry.getValue();

            CreateSupplyType.createSupplyType(conn, name, unit);

            long endTime = System.nanoTime();
            posteriori.add(endTime - startTime);
        }

        return posteriori;
    }

    public Long perfReadAllSupplyTypes(){
        long startTime = System.nanoTime();
        ReadSupplyType.getAllSupplyTypes(conn);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public Long perfReadSupplyTypeByName(String type){
        long startTime = System.nanoTime();
        ReadSupplyType.getSupplyTypeByName(conn, type);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public List<Long> perfReadSupplyTypeById(){
        List<Long> posteriori = new ArrayList<>();

        for (int i = 0; i < samples; i++) {
            long startTime = System.nanoTime();

            ReadSupplyType.getSupplyTypeById(conn, i);

            long endTime = System.nanoTime();
            posteriori.add(endTime - startTime);
        }


        return posteriori;
    }
}
