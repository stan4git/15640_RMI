package shareFolder;

/**
 *	This is the interface be implemented by client and server.
 *
 */
public interface ZipCodeServer // extends YourRemote or whatever
{
    public void initialise(ZipCodeList newlist);
    public String find(String city);
    public ZipCodeList findAll();
    public void printAll();
}

