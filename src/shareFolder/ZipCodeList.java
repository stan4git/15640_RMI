package shareFolder;

import java.io.Serializable;
/**
 *	This is the interface be implemented by client and server.
 *
 */
public class ZipCodeList implements Serializable {

	private static final long serialVersionUID = 7376192454767366269L;
	public String city;
	public String ZipCode;
	public ZipCodeList next;

	public ZipCodeList(String c, String z, ZipCodeList n) {
		city = c;
		ZipCode = z;
		next = n;
	}
}
