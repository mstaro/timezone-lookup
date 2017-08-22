import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class TimeZoneLookupTest implements Runnable {
    private static final int MAX_THREADS = 25;
    private static final int TEST_COUNT = 100000;
    @Override
    public void run() {
        final double rawLat = ThreadLocalRandom.current().nextDouble(-90, 90 + 1);
        final double rawLng = ThreadLocalRandom.current().nextDouble(-180, 180 + 1);

        final double lat = new BigDecimal(rawLat).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        final double lng = new BigDecimal(rawLng).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        final long start = System.currentTimeMillis();
        final String tz = TimeZoneLookup.apply(lat, lng).asNullable();
        final long end = System.currentTimeMillis();
        final long time = end - start;

        System.out.println(String.format("Lookup for geocode (%s, %s) took %d ms: Timezone -> %s", lat, lng, time, tz));
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        for (int i = 0; i < TEST_COUNT; i++) executor.execute(new TimeZoneLookupTest());

//        TimeZoneLookupTest tzlt = new TimeZoneLookupTest();
//        for (int i = 0; i < TEST_COUNT; i++) tzlt.run();
    }
}
