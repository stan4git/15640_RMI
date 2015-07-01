package shareFolder;

import java.io.Serializable;
/**
 *	This is the interface be implemented by client and server.
 *
 */
public interface ZipCodeRList extends Serializable// extends YourRemote or whatever
{
    public String find(String city);
    public ZipCodeRList add(String city, String zipcode);
    public ZipCodeRList next();
}
