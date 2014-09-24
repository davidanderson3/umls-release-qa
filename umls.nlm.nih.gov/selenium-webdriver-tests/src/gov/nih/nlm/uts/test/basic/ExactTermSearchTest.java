package gov.nih.nlm.uts.test.basic;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.Keys;

public class ExactTermSearchTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "https://utslogin.nlm.nih.gov/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    
  }

  @Test
  public void testExactTermSearch() throws Exception {
    driver.get("https://uts.nlm.nih.gov/metathesaurus.html");
    driver.findElement(By.id("username")).clear();
    driver.findElement(By.id("username")).sendKeys("umlsguest");
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys("umlsguest1!");
    driver.findElement(By.name("submit")).click();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).click();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).clear();
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).sendKeys("pancreas");
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).sendKeys(Keys.RETURN);
    //driver.findElement(By.id("gwt-debug-button-searchButton")).click();
    
    try {
      assertTrue(Pattern.compile("(Search *Results *)+\\(5+[0-9]{3}\\)").matcher(driver.findElement(By.cssSelector("b")).getText()).find());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).click();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).clear();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).sendKeys("otitis");
    driver.findElement(By.id("gwt-debug-button-searchButton")).click();
    try {
      assertTrue(Pattern.compile("(Search *Results *)+\\(5+[0-9]{2}\\)").matcher(driver.findElement(By.cssSelector("b")).getText()).find());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).click();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).clear();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).sendKeys("depressive disorder");
    driver.findElement(By.id("gwt-debug-button-searchButton")).click();
    try {
      assertTrue(Pattern.compile("(Search *Results *)+\\(1+[0-9]{2}\\)").matcher(driver.findElement(By.cssSelector("b")).getText()).find());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).click();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).clear();
    driver.findElement(By.id("gwt-debug-suggestBox-suggestBox")).sendKeys("injury");
    driver.findElement(By.id("gwt-debug-button-searchButton")).click();
    try {
      assertTrue(Pattern.compile("(Search *Results *)+\\(1+[0-9]{4}\\)").matcher(driver.findElement(By.cssSelector("b")).getText()).find());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    driver.findElement(By.xpath("//a[contains(text(),'Sign Out')]")).click();
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
