package service;

import server.Remote;
import shareFolder.Hello_Interface;
/**
 * This is implementation of test0.
 *
 */
public class Hello_Interface_Impl implements Hello_Interface,Remote{

	@Override
	public String sayHello(String name) {
		return "********** Result from Romote is : Hello " + name + "     **************";
	}

	@Override
	public String sayHello(String name, Long meters) {
		return "*********** Result from Remote is : Hello " + name +", Today you have run more than  " + meters + "  meters!************\nCongratulations!";
	}

}
