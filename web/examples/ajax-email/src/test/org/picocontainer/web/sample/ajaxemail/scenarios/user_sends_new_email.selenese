package com.example.tests;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class NewTest extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://change-this-to-the-site-you-are-testing/", "*chrome");
	}
	public void testNew() throws Exception {
		selenium.open("/remoting-ajaxemail-webapp/");
		selenium.click("compose");
		selenium.type("to", "aaa");
		selenium.type("//form[@id='composeMailForm']/table/tbody/tr[2]/td[2]/input", "bbb");
		selenium.type("//form[@id='composeMailForm']/table/tbody/tr[3]/td[2]/textarea", "ccc");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if ("Compose Mail".equals(selenium.getText("//div[@id='composeMessage']/h2"))) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("submitMessage");
		selenium.click("link=Sent");
		selenium.waitForPageToLoad("30000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if ("aaa".equals(selenium.getText("//tr[@id='14']/td[2]"))) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

	}
}
