package org.example.stepDefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pageObjects.CarValuationComparisonPage;
import org.testng.Assert;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


public class CarValuationComparisonSteps  {
    private final CarValuationComparisonPage carValuationComparisonPage;
    private static final String BASE_URL = "https://www.webuyanycar.com/";
    private List<String> regNumbers = new ArrayList<>();

    public CarValuationComparisonSteps() {
        this.carValuationComparisonPage = new CarValuationComparisonPage();
    }


    @Given("I go to car valuation website")
    public void iGoToCarValuationWebsite() {
        carValuationComparisonPage.goToValuationSite(BASE_URL);
        if (CarValuationComparisonPage.isElementDisplayed(carValuationComparisonPage.cookie)) {
            carValuationComparisonPage.cookie.click();
        }
    }

    @When("the vehicle registration numbers are extracted from {string} file")
    public void theVehicleRegistrationNumbersAreExtractedFromCar_inputTxtFile(String filename)  {
        regNumbers = carValuationComparisonPage.extractCarNumbersFromInputFile(filename, regNumbers);
    }

    @And("I search each extracted car number on the valuation website with random mileage")
    public void iSearchEachExtractedCarNumberOnTheValuationWebsiteWithRandomMileage() {
        for (String regNumber : regNumbers) {
            if(!carValuationComparisonPage.getCurrentURL().equalsIgnoreCase((BASE_URL))){
                carValuationComparisonPage.goToValuationSite(BASE_URL);
            }
            carValuationComparisonPage.enterCarNumber(regNumber);
        }
    }

    @Then("I verify the results with {string} file")
    public void iVerifyTheResultsWithCar_outputTxtFile(String fileName) throws IOException {
        List<List<String>> mismatchedCars = carValuationComparisonPage.compareAndAssert(fileName);
        // Log mismatched cars and fail assertion if there are any mismatches
        if (!mismatchedCars.isEmpty()) {
            System.out.println("Mismatched Car Details: " + mismatchedCars);
            //2 of the car numbers(SG18HT, BW57BOF are not returning car details on the site, getting error as Sorry, we couldn't find your car
             //hence used Assert.fail() method
            Assert.fail("The following car details did not match with expected car output file: " + mismatchedCars);
        }
    }
}
