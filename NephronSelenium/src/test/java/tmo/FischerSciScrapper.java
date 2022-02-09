package tmo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
//import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
//import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import org.openqa.selenium.By;
//import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;

public class FischerSciScrapper {
	
	static WebDriver driver = null;
	static String baseUrl = "https://www.fishersci.com/us/en/home.html";
	static int fsProductID_start = 10000475;
	static int fsProductID_end = 10000480;

	public static void main(String[] args) {		
		
		driver = InstantiateChromeDriver(driver);
		//driver = InstantiateHeadlessChromeDriver(driver);
		ArrayList<FischerSciProduct> myProducts = scrapeFischerSciPrices(driver,fsProductID_start, fsProductID_end);
		driver.close();
		
		String serializedFSProducts = new Gson().toJson(myProducts);
		
		try {
			String muleAPICallResults = postScrapedData(serializedFSProducts);
			System.out.println(muleAPICallResults);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static WebDriver InstantiateHeadlessChromeDriver(WebDriver d) {
		System.setProperty("webdriver.chrome.driver","./src/main/resources/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		d = new ChromeDriver(options);
		return d;
	}
	
	public static WebDriver InstantiateChromeDriver(WebDriver d) {
		System.setProperty("webdriver.chrome.driver","./src/main/resources/chromedriver.exe");
		d = new ChromeDriver();
		return d;
	}
	
	public static ArrayList<FischerSciProduct> scrapeFischerSciPrices(WebDriver d, int startCatalogNum, int endCatalogNum){
		ArrayList<FischerSciProduct> fischerSciProductList = new ArrayList<FischerSciProduct>();
		
		d.get(baseUrl);
		WebElement searchBar = null;
		WebElement searchButton = null;
		

		
		for(int i=startCatalogNum; i <=endCatalogNum; i++) {
			
			searchBar = d.findElement(By.id("searchbar__searchBox__input"));
			searchButton = d.findElement(By.id("searchbar__searchButton"));
			
			System.out.println("Setting web elements");

			d.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
			
			System.out.println("Starting price scrape");
			searchBar.click();
			searchBar.clear();
			searchBar.sendKeys(String.valueOf(i) + Keys.ENTER);
			System.out.println("sending product ID");
			if(i==fsProductID_start) {
				searchButton.click();
			}
			
			System.out.println("Checking for price css element...");
			
			boolean priceExists = cssElementExists(".price", d);
			
			if(priceExists==false) {
				WebElement firstItem = d.findElement(By.id("qa_srch_res_title_1"));
				firstItem.click();
				System.out.println("Title of website is: " + d.getTitle());
			}
			else 
				System.out.println("Title of website is: " + d.getTitle());
			
			
			WebDriverWait w = new WebDriverWait(d,Duration.ofSeconds(5));
			w.until(ExpectedConditions.presenceOfElementLocated(By.id("item_header_text")));
			w.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".price")));
			
			boolean productIDExists = idElementExists("item_header_text", d);
			boolean priceCssExists = cssElementExists(".price", d);
			
			System.out.println("Title id is available: " + productIDExists);
			System.out.println("Price css is available: " + priceCssExists);
			
			String itemName = d.findElement(By.id("item_header_text")).getText();

			String price = d.findElement(By.cssSelector(".price")).getText();
			price = price.replaceAll("[^\\d.]", "");
			
			
			System.out.println("Value for Item Name: " + itemName);
			System.out.println("value for price: " + price);
			
			FischerSciProduct scrapedProduct = new FischerSciProduct();
			scrapedProduct.setProductCatalogNo(String.valueOf(i));
			scrapedProduct.setProductName(itemName);
			scrapedProduct.setScrapeDate(getCurrentUTCDate()+"Z");
			
			if(!price.equals("")) {
				scrapedProduct.setProductPrice(price);
			}
			else 
				scrapedProduct.setProductPrice("0.0");
			
			fischerSciProductList.add(scrapedProduct);			
			
			int nextProduct = i+1;
			System.out.println("Going to next product: " + nextProduct);

		}
		return fischerSciProductList;
	}
	
	public static boolean cssElementExists(String css, WebDriver d) {
	    try {
	        d.findElement(By.cssSelector(css));
	    } catch (NoSuchElementException e) {
	        return false;
	    }
	    return true;
	}
	
	
	public static boolean idElementExists(String id, WebDriver d) {
	    try {
	        d.findElement(By.id(id));
	    } catch (NoSuchElementException e) {
	        return false;
	    }
	    return true;
	}
	
	public static String getCurrentESTDate() {
		
		DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		ZonedDateTime nowInLocalTimeZone = ZonedDateTime.now();
		ZonedDateTime nowInNYC = nowInLocalTimeZone.withZoneSameInstant(ZoneId.of("America/New_York"));
		
		return nowInNYC.format(FOMATTER);
	}
	
	
	public static String getCurrentUTCDate() {
		
		DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		ZonedDateTime nowInLocalTimeZone = ZonedDateTime.now();
		ZonedDateTime nowInNYC = nowInLocalTimeZone.withZoneSameInstant(ZoneId.of("UTC"));
		
		return nowInNYC.format(FOMATTER);
	}

	
	public static String postScrapedData(String jsonString) throws IOException {
		URL url = new URL("http://tmoflows.us-e2.cloudhub.io/api/store-data");
		HttpURLConnection http = (HttpURLConnection)url.openConnection();
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		http.setRequestProperty("Accept", "application/json");
		http.setRequestProperty("Content-Type", "application/json");

		String data = jsonString;

		byte[] out = data.getBytes(StandardCharsets.UTF_8);

		OutputStream stream = http.getOutputStream();
		stream.write(out);
		http.disconnect();

		return "Response Code: " + (http.getResponseCode() + '\n' + " Response Message: " + http.getResponseMessage());
		
	}
	
	
}
