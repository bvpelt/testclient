package nl.kadaster.geodatastore.cucumbertest;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by bvpelt on 10/3/15.
 */
public class PDokViewerStepdef {

    private WebDriver driver = null;

    @Given("^I am on the pdok viewer site$")
    public void i_am_on_the_pdok_viewer_site() throws Throwable {
        driver = new FirefoxDriver();
        driver.navigate().to("http://pdokviewer.pdok.nl/");
        long timeout = 10000; //ms
        driver.wait(timeout);
    }

    @When("^I click on bag layer$")
    public void i_click_on_bag_layer() throws Throwable {
       driver.findElement(By.id("extdd-28")).click();
    }

    @When("^I search for ,Apeldoorn,Apeldoorn,Gelderland in the geocoder$")
    public void i_search_for_Apeldoorn_Apeldoorn_Gelderland_in_the_geocoder() throws Throwable {
        driver.findElement(By.id("pdoksearchcombo")).sendKeys(",Apeldoorn,Apeldoorn,Gelderland");
    }

    @When("^I click on BAG layer$")
    public void i_click_on_BAG_layer() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I get a picture with background and bag objects$")
    public void i_get_a_picture_with_background_and_bag_objects() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }


}
