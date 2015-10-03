package nl.kadaster.geodatastore.cucumbertest;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by bvpelt on 10/3/15.
 */

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "json:target/cucumber.json" })
public class CucumberRunner {
}
