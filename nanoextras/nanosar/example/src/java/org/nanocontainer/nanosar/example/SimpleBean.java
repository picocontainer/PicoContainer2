package org.nanocontainer.nanosar.example;

public class SimpleBean implements SimpleBeanMBean {
	String foo;

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}
	
}
