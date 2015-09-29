package nl.kadaster.geodatastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);
    private int failures = 0;
    private int tests = 0;

    public static void main(String[] args) {

        App app = new App();

        app.doTests();
    }

    private void doTests() {
        logger.info("Start tests");

        TestClient03 tc = new TestClient03();

        failures += tc.Test01();
        tests++;

        failures += tc.Test02();
        tests++;

        failures += tc.Test03();
        tests++;

        logger.info("End   tests, executed: {} with {} failures", tests, failures);
    }
}