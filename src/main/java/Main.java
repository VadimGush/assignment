
public class Main {

    public static void main(String[] args) throws Exception {
        try {
            Benchmarks.measureThroughput();
        } catch (final Exception e) {
            System.err.println("Failed to perform benchmark");
            e.printStackTrace();
        }
    }

}