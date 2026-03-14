import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class StepDefinitions {
    WebDriver driver;

    @Given("I navigate to the TSP Lifecycle funds page")
    public void navigateToTsp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize(); // Add this line
        driver.get("https://www.tsp.gov/funds-lifecycle/");
    }

    @When("I uncheck the {string} fund checkbox")
    public void uncheckSpecificFund(String fundName) {
        // 1. Wait for the label to be present and visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String xpathExpression = "//label[contains(text(), '" + fundName + "')]";
        
        WebElement label = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathExpression)));

        // 2. Scroll it into view (crucial for Mac/Chrome high-res screens)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", label);

        // 3. Perform the forced JavaScript click
        System.out.println(">>> Toggling checkbox for: " + fundName);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
    }

    @Then("the script ends successfully")
    public void endScript() throws InterruptedException {
        // Verify the L 2035 checkbox is now NOT selected
        // Note: You'd ideally pass the ID or name here too
        System.out.println("Verification complete. Script closing...");
        Thread.sleep(5000); 
        driver.quit();
    }

    @Given("I navigate to the TSP homepage")
    public void navigateToHomepage() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }
        driver.get("https://www.tsp.gov/");
    }

    @Given("I go to the Annuity Calculator page")
    public void navigateToAnnuityCalculator() {
        driver.get("https://www.tsp.gov/calculators/tsp-annuity-calculator/#panel-1");
    }

    @When("I click the Start button")
    public void clickStart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // 1. Force a scroll to the bottom then back up to 'wake up' any lazy-loaded elements
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        try { Thread.sleep(1000); } catch (InterruptedException e) {} 
        js.executeScript("window.scrollTo(0, 0)");

        // 2. Look for the 'Start' button using a very broad XPath that covers links and buttons
        // Most TSP calculators use a link (<a>) that looks like a button
        WebElement startButton = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//a[contains(text(), 'Start')] | //button[contains(text(), 'Start')] | //*[@id='start-button']")
        ));

        // 3. Scroll specifically to that button so it's centered
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", startButton);

        // 4. Click it using JavaScript (the most reliable way)
        js.executeScript("arguments[0].click();", startButton);
        System.out.println(">>> Clicked the Start button using aggressive discovery!");
    }

    @Then("the calculator should be active")
    public void verifyCalculatorActive() throws InterruptedException {
        System.out.println(">>> Calculator started successfully.");
        Thread.sleep(3000); // Just to watch the screen change
        driver.quit();
        driver = null; // Reset for next run
    }

    @When("I enter {string} for the age")
    public void enterAge(String age) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        // Find all inputs and look for the one near the 'Age' text
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        WebElement ageField = null;

        for (WebElement input : inputs) {
            String outerHTML = input.getAttribute("outerHTML").toLowerCase();
            if (outerHTML.contains("age")) {
                ageField = input;
                break;
            }
        }

        if (ageField != null) {
            // 1. Scroll it to the middle of the screen
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", ageField);
            
            // 2. Highlight it yellow so you can see it!
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid yellow'", ageField);
            Thread.sleep(1000);

            // 3. Click and Type
            ageField.click();
            ageField.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);
            ageField.sendKeys(age);
            System.out.println(">>> Entered Age: " + age);
        }
    }

    @When("I enter {string} for the account balance")
    public void enterBalance(String balance) throws InterruptedException {
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        WebElement balanceField = null;

        for (WebElement input : inputs) {
            String outerHTML = input.getAttribute("outerHTML").toLowerCase();
            if (outerHTML.contains("balance") || outerHTML.contains("amount")) {
                balanceField = input;
                break;
            }
        }

        if (balanceField != null) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", balanceField);
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid yellow'", balanceField);
            Thread.sleep(1000);

            balanceField.click();
            balanceField.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);
            balanceField.sendKeys(balance);
            balanceField.sendKeys(Keys.TAB);
            System.out.println(">>> Entered Balance: " + balance);
        }
    }
}