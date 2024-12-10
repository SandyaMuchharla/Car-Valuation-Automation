package org.example.pageObjects;

import org.example.utils.DriverBase;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarValuationComparisonPage extends DriverBase {

    private WebDriver driver;
    private static final String info = "//section[contains(@class,'primary-section')]//div[contains(@class,'details-vrm ng-star-inserted')] | //section[contains(@class,'primary-section')]//div[@class = 'd-table']//div[@class='d-table-cell value']";
    public final List<String> carDetails = new ArrayList<>();
    public final  List<List<String>> actualCarDetails = new ArrayList<>();

    public CarValuationComparisonPage() {
        WebDriver driverBase = getDriver();
        PageFactory.initElements(driverBase, this);
        this.driver = driverBase;
    }

    @FindBy(id = "onetrust-accept-btn-handler")
    public WebElement cookie;

    @FindBy(xpath = "//input[@name='vehicleReg']")
    private WebElement vehicleReg;

    @FindBy(xpath = "//input[@name='Mileage']")
    private WebElement mileage;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement submit;

    @FindBy(xpath = "//h1[@class= 'text-focus ng-star-inserted']")
    private WebElement errorMessage;

    @FindBy(xpath = "//section[contains(@class,'primary-section')]//div[contains(@class,'details-vrm ng-star-inserted')]")
    private WebElement carNumber;

    @FindBy(xpath = info)
    private List<WebElement> carInfo;

    public void goToValuationSite(String BASE_URL) {
        driver.get(BASE_URL);
    }
    public String getCurrentURL(){
        return driver.getCurrentUrl();
    }

    public List<String> extractCarNumbersFromInputFile(String filename, List<String> regNumbers){
        String carNumberPattern = "\\b[A-Z]{2}\\d{2}\\s?[A-Z]{3}\\b";
        Pattern pattern = Pattern.compile(carNumberPattern);
        try (BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/" + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    regNumbers.add(matcher.group().replaceAll("\\s+",""));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Extracted Car Numbers from input file: "  + regNumbers);
        return regNumbers;

    }

    public void enterCarNumber(String regNumber) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(this.vehicleReg));
        vehicleReg.sendKeys(regNumber);
        mileage.sendKeys("40000");
        submit.click();
        if(!isElementDisplayed(errorMessage)){
            for (int i = 0; i < carInfo.size(); i++) {
                if (i == 4) {
                    break;
                }
                carDetails.add(carInfo.get(i).getText());
            }

        } else {
            carDetails.add(regNumber + ", " + errorMessage.getText());
            actualCarDetails.add(new ArrayList<>(carDetails));
            carDetails.clear();
            return;
        }
        actualCarDetails.add(new ArrayList<>(carDetails));
        carDetails.clear();
    }
    public List<List<String>> compareAndAssert(String fileName) throws IOException {
        List<List<String>> expectedCarDetails = parseCsv("src/test/resources/" + fileName);
        System.out.println("Actual Registered Numbers: " + actualCarDetails);
        System.out.println("Expected Registered Numbers: " + expectedCarDetails);
        List<List<String>> mismatchedCars = new ArrayList<>();

        /* Compare and assert(code to compare actual against expected
        for (List<String> actualCar : allCarDetails) {
            boolean matchFound = false;

            for (List<String> expectedCar : expectedCarDetails) {
                if (actualCar.equals(expectedCar)) {
                    matchFound = true;
                    System.out.println("Match found: " + actualCar);
                    System.out.println("expectedCar: " + expectedCar);
                    System.out.println("actualCar: " + actualCar);
                    break;
                }
            }
            Assert.assertTrue(matchFound, "No match found for: " + actualCar);
        }*/

        // Iterate through expected car details(2 of the car numbers(SG18HT, BW57BOF are not returning car details on the site, getting error as Sorry, we couldn't find your car
        for (List<String> expectedCar : expectedCarDetails) {
            boolean matchFound = false;
            for (List<String> actualCar : actualCarDetails) {
                if (expectedCar.equals(actualCar)) {
                    matchFound = true;
                    System.out.println("Match found: " + expectedCar);
                    break;
                }
            }
            if (!matchFound) {
                mismatchedCars.add(expectedCar);
            }
        }

        return mismatchedCars;
    }
    public boolean isElementDisplayed(WebElement cookie) {
        try {
            return cookie.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    public List<List<String>> parseCsv(String filePath) throws IOException {
        List<List<String>> csvData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                csvData.add(Arrays.asList(values));
            }
        }
        return csvData;
    }
}

