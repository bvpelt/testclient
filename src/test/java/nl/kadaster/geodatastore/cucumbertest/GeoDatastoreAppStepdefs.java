package nl.kadaster.geodatastore.cucumbertest;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * Created by bvpelt on 10/7/15.
 */
public class GeoDatastoreAppStepdefs {
    private static Logger logger = LoggerFactory.getLogger(GeoDatastoreAppStepdefs.class);

    private static boolean usePdok = false;
    private static Configuration conf = new Configuration(usePdok);
    private static String fullurl = conf.getFullUrl();
    private static String baseUrl = fullurl + "/geonetwork/";

    private WebDriver driver = null;

    @Given("^That I am positioned at geodatastore login page$")
    public void that_I_am_positioned_at_geodatastore_login_page() throws Throwable {
        driver = new FirefoxDriver();
        Assert.assertNotNull(driver);
        driver.navigate().to(baseUrl);
        long timeout = conf.getTimeOut();
        TimeUnit unit = TimeUnit.MILLISECONDS;

        driver.manage().timeouts().implicitlyWait(timeout, unit);
    }

    @When("^I add credentials on login page$")
    public void i_add_credentials_on_login_page() throws Throwable {
        String username = conf.getUserName();
        String password = conf.getPassword();

        driver.findElement(By.id("inputUsername")).sendKeys(username);
        driver.findElement(By.id("inputPassword")).sendKeys(password);
        driver.findElement(By.tagName("BUTTON")).click();
    }

    @Then("^I get the overview screen$")
    public void i_get_the_overview_screen() throws Throwable {
        String text = driver.findElement(By.xpath("//nav[@class='tabs']/ul[@class='nav nav-tabs']")).getText();
        logger.debug("Found text {}", text);
        Assert.assertTrue("Not on expected page", text.startsWith("Mijn publiceerbare datasets"));
    }

    @Then("^I logout from the application$")
    public void i_logout_from_the_application() throws Throwable {
        driver.findElement(By.xpath("//a[@title='Afmelden']")).click();
        driver.close();
    }
}
