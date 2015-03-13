package com.globant.training.tests;



import java.util.concurrent.TimeUnit;
import java.net.URL;
import java.net.MalformedURLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Configuration;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.globant.training.pages.CustomizePage;
import com.globant.training.pages.FlyPage;
import com.globant.training.pages.HomePage;
import com.globant.training.pages.HotelConfirmation;
import com.globant.training.pages.HotelPage;
import com.globant.training.pages.LogoutPage;
import com.globant.training.pages.ResultPage;
import com.globant.training.pages.SignInPage;
import com.globant.training.pages.TravelerPage;

public class Tests {
	WebDriver driver;
	HomePage homePage;
	
	public void pausa(int s) {
		 try {
		 	Thread.sleep(s*1000);
		 } catch (InterruptedException e) {
		 	
		 	e.printStackTrace();
		 }
		}
	public void wait(WebDriver driver){
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
	}
	
	@BeforeMethod
	@Parameters("browser")
	public void before(String browser) throws MalformedURLException{
		System.out.println(browser);
		if(browser.equals("FF")){
			DesiredCapabilities capability = DesiredCapabilities.firefox();
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);
//			driver = new FirefoxDriver();
		}
		if(browser.equals("CH")){
			System.setProperty("webdriver.chrome.driver", "C:\\Users\\matias.caruso\\Desktop\\eclipse\\selenium\\chromedriver.exe");
			DesiredCapabilities capability = DesiredCapabilities.chrome();
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);
			
//			driver = new ChromeDriver();
		}
		if(browser.equals("IE")){
			DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
			capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);   
		}
//		System.setProperty("webdriver.chrome.driver", "C:\\Users\\matias.caruso\\Downloads\\chromedriver.exe");
//		driver = new ChromeDriver();
//		driver = new FirefoxDriver();
//		driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);
		wait(driver);
		homePage = PageFactory.initElements(driver, HomePage.class);
		homePage.go(driver);
		driver.manage().window().maximize();
		Reporter.log("Se Entro a la Pagina Correctamente\n");
		
	}
	
	@AfterMethod
	public void after(){
		driver.quit();
	}
	
	@Test(description="ID=001, loguea al user y verifica que se encuentre logueado")
	public void signIn() {
		pausa(10);
		homePage.signIn();
		pausa(7);
		wait(driver);
		SignInPage signPage = PageFactory.initElements(driver, SignInPage.class);
		wait(driver);
		signPage.signIn("m.villarruel.test@gmail.com", "Automation");
		
		Assert.assertEquals(homePage.welcome(driver), true);
		
	}
	
	@Test(description="ID=002, loguea user y contraseña incorrecto y verifica el error")
	public void errorSignIn(){
		homePage.signIn();
		pausa(7);
		wait(driver);
		SignInPage signPage = PageFactory.initElements(driver, SignInPage.class);
		signPage.signIn("banana@gmail.com", "Automation");
		Assert.assertEquals(signPage.errorSignIn(),true);
	}
	
	@Test(description="ID=003, desloguea al user y verifica que se encuentre deslogueado")
	public void logout(){
		homePage.signIn();
		pausa(7);
		wait(driver);
		SignInPage signPage = PageFactory.initElements(driver, SignInPage.class);
		wait(driver);
		signPage.signIn("m.villarruel.test@gmail.com", "Automation");
		wait(driver);
		homePage.signout();
		pausa(7);
		LogoutPage logout= PageFactory.initElements(driver, LogoutPage.class);
		Assert.assertEquals(logout.logout(),true);
	}
	@Test(description="ID=004, busca y selecciona vuelo, agrega la informacion del pasajero")
	public void search(){
		pausa(7);
		homePage.searchAir();
		wait(driver);
		homePage.setFromAndTo("LAS","LAX");
		wait(driver);
		homePage.dates();
		ResultPage resultPage = PageFactory.initElements(driver, ResultPage.class);
		pausa(25);
		wait(driver);
		Assert.assertEquals(resultPage.validation(),true);
		resultPage.searchList();
		pausa(10);
		wait(driver);
		Assert.assertEquals(resultPage.getFly(),true);
		pausa(5);
		wait(driver);
		FlyPage flyPage = PageFactory.initElements(driver, FlyPage.class);
		Assert.assertEquals(flyPage.validation(),true);
		flyPage.click();
		pausa(5);
		wait(driver);
		CustomizePage customizePage = PageFactory.initElements(driver, CustomizePage.class);
		customizePage.click();
		pausa(5);
		wait(driver);
		TravelerPage travelerPage =PageFactory.initElements(driver, TravelerPage.class);
		Assert.assertEquals(travelerPage.completeData("Banana", "Puyrredon", "123545353","m.villarruel.test@gmail.com"),true);
		
	}
	@Test(description="ID=005, busqueda de vuelo negativa")
	public void falseSearch(){
		homePage.searchAir();
		wait(driver);
		homePage.setFromAndTo("La Comarca","Mordor");
		wait(driver);
		homePage.dates();
		pausa(5);
		Assert.assertEquals(homePage.validarError(),true);
		
	}
	@Test(description="ID=006, busqueda de vuelo positiva")
	public void trueSearch(){
		homePage.searchAir();
		wait(driver);
		homePage.setFromAndTo("Miami","JFK");
		wait(driver);
		homePage.dates();
		pausa(5);
		ResultPage resultPage = PageFactory.initElements(driver, ResultPage.class);
		pausa(4);
		wait(driver);
		Assert.assertEquals(resultPage.validation(),true);
		
	}
	
	@Test(description="ID=007, busca y selecciona vuelo y se loguea en la pagina de completar información del pasajero")
	public void searchSignIn(){
		homePage.searchAir();
		wait(driver);
		homePage.setFromAndTo("LAS","LAX");
		wait(driver);
		homePage.dates();
		ResultPage resultPage = PageFactory.initElements(driver, ResultPage.class);
		pausa(7);
		wait(driver);
		Assert.assertEquals(resultPage.validation(),true);
		resultPage.searchList();
		pausa(7);
		wait(driver);
		Assert.assertEquals(resultPage.getFly(),true);
		pausa(7);
		wait(driver);
		FlyPage flyPage = PageFactory.initElements(driver, FlyPage.class);
		flyPage.click();
		pausa(7);
		wait(driver);
		CustomizePage customizePage = PageFactory.initElements(driver, CustomizePage.class);
		customizePage.click();
		pausa(7);
		wait(driver);
		TravelerPage travelerPage =PageFactory.initElements(driver, TravelerPage.class);
		travelerPage.signIn("m.villarruel.test@gmail.com", "Automation");
	}
	@Test(description="ID=008, busca y selecciona hotel y vuelo")
	public void hotel(){
		homePage.hotelForm("LAS", "LAX");
		pausa(10);
		wait(driver);
		HotelPage hotelPage =PageFactory.initElements(driver, HotelPage.class);
		Assert.assertEquals(hotelPage.validation(),true);
		hotelPage.getStars();
		pausa(10);
		wait(driver);
		Assert.assertEquals(hotelPage.validaStars(),true);
		hotelPage.selectHotel();
		pausa(10);
		wait(driver);
		HotelConfirmation hotelConfirmation =PageFactory.initElements(driver, HotelConfirmation.class);
		Assert.assertEquals(hotelConfirmation.validation(), true);
	}
		
}
	
	
	

