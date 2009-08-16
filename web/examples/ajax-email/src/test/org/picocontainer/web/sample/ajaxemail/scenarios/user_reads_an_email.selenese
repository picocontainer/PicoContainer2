package com.example.tests;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class NewTest extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://change-this-to-the-site-you-are-testing/", "*chrome");
	}
	public void testNew() throws Exception {
		selenium.open("/remoting-ajaxemail-webapp/");
		selenium.click("//tr[@id='1']/td[4]");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if ("Nice Example".equals(selenium.getText("//tr[@id='1']/td[4]"))) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//tr[@id='1']/td[4]");
		selenium.click("//tr[@id='1']/td[4]");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if ("Read Message".equals(selenium.getText("//div[@id='readMessage']/h2"))) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		verifyEquals("Nice Example", selenium.getValue("//form[@id='readMessageForm']/table/tbody/tr[2]/td[2]/input"));
		selenium.click("cancelRead");
	}
}