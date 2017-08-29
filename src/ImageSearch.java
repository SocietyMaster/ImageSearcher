import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.io.BufferedReader;    
import java.io.FileReader;
import java.io.BufferedWriter;    
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;    
import java.io.FileWriter;    
import java.io.IOException;
import java.io.InputStreamReader;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;



public class ImageSearch  {

	public static void main(String[] args) {
		
		List<String> proxyList = new ArrayList<String>();
		try{
			File proxyListFile = new File("proxy_list.txt"); // File contains the proxy server address list, updates needed when use
        	InputStreamReader proxyReader = new InputStreamReader(new FileInputStream(proxyListFile));
        	BufferedReader pbr = new BufferedReader(proxyReader);
        	String proxy = "1";
        	while (proxy != null) {  
        		proxy = pbr.readLine();
        		proxyList.add(proxy);
        	}
		} catch (Exception e) {  
            e.printStackTrace();  
        }
		
    	String imageDir = "";
        String imageName = "";
        
    	try {  
            String pathname = "input.txt"; // All the image file and path stored in this file
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();  
            while (line != null) {  
                String[] imgInfo = line.split("/");
                imageDir = imgInfo[0];
                imageName = imgInfo[1];
                int flag = 0;
                
                //Generate Random number
                double d = Math.random();
                int i = (int)(d*14);
                String selectedProxy = proxyList.get(i);
                String proxyAddress = selectedProxy.split(":")[0];
                int proxyPort = Integer.valueOf(selectedProxy.split(":")[1]);
                
                //Proxy Setting by Firefox Profile
                FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("network.proxy.type", 1);
                profile.setPreference("network.proxy.http", proxyAddress);
                profile.setPreference("network.proxy.http_port", proxyPort);
                
                // Initialize a webdriver and execute the search request
                WebDriver driver;
            	System.setProperty("webdriver.gecko.driver", "Gecko Driver Path here");
            	String getRequest = "https://www.google.com/searchbyimage?site=search&sa=X&image_url=http://lotus.cs.northwestern.edu/icons/images/" + imageDir + "/" + imageName;
            	driver = new FirefoxDriver(profile);
            	try{
            		driver.get(getRequest);
            	}
            	catch(Exception e2){
            		System.err.println("Open page failed: " + imageDir + "/" + imageName);
            		flag = 1;
            	    throw(e2);
            	}
            	
            	if (flag == 1){
            		continue;
            	}
            	
            	// Capture the search result by CSS element
            	List<WebElement> elem;
            	try{
            		elem = driver.findElements(By.className("_gUb"));
            	}
            	catch (Exception e1)
            	{
            	    System.err.println("Element not Found");
            	    throw(e1);
            	}
                
            	// Output the results to txt file
                for(WebElement eachResult : elem){
                	String meaning = eachResult.getText();
                	System.out.println("Result: " + imageDir + " | " + imageName + " | " + meaning);
                	
                	try {
            			FileWriter fileWriter = new FileWriter("Output File and Path here", true);
            			fileWriter.write(imageDir + ".apk | " + imageName + " | " + meaning + "\n");
            			fileWriter.close();
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
                }
                
                driver.close();
                
                // Hold the automation for a short time in case of being blocked by Google
                try{
                    Thread thread = Thread.currentThread();
                    int random=(int)(Math.random()*5);
                    thread.sleep(random*60000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                line = br.readLine();
            }

        } catch (Exception e) {  
            e.printStackTrace();  
        }
    }
}