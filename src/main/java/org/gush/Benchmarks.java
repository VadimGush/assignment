package org.gush;


import org.gush.database.Database;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Measures performance of the service.
 * <p>
 * Note: I don't like to assume how my code will impact the performance,
 * so I will prefer to have benchmarks in place.
 */
public class Benchmarks {

    /*
     * NOTE: This is not the best way to measure performance of the Java code
     * because the throughput will fluctuate depending on GC behaviour
     * and JIT compiler. But it is good enough for now
     */
    public static void measureThroughput() throws IOException {
        final List<String> testData = generateTestData();

        // Do not include startup time
        final Service service = new Service(new Database("logs.txt"));

        final LocalDateTime start = LocalDateTime.now();

        for (final String query : testData) {
            service.handle(query);
        }
        service.close();

        final LocalDateTime end = LocalDateTime.now();

        final float totalTime = Duration.between(start, end).toMillis() / 1000.f;
        final float throughput = testData.size() / totalTime;

        System.out.println("Total time: " + totalTime + " sec.");
        System.out.println("Throughput: " + Math.round(throughput) + " q/s");
    }

    private static List<String> generateTestData() {
        final List<String> result = new ArrayList<>();

        final Random random = new Random();
        for (int i = 0; i < 100_000; i++) {

            // Generate a random string
            final StringBuilder buffer = new StringBuilder();
            final int stringSize = random.nextInt(5, 15);
            for (int c = 0; c < stringSize; c++) {
                // Alphabet in ASCII table is between 97 and 122
                buffer.append((char) random.nextInt(97, 122));
            }

            result.add(buffer.toString());
        }

        return result;
    }

}
