package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
/**
 * This class is a util class which contains the readFile(),
 * writeFile(), writeJaveFile() and writeJaveFile() methods
 *
 */
public class Util {
	
	/**
	 * This method is used to read the specific file
	 * @param fileName 
	 * @return
	 */
	public static byte[] readFile (String fileName) {
		File file = new File(fileName);
		byte[] fileContent = new byte[(int)file.length()];
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
			fis.read(fileContent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileContent;
	}
	
	/**
	 * This method is used to write the bytes into a file
	 * @param fileName String
	 * @param fileContent byte[]
	 */
	public static void writeFile (String fileName, byte[] fileContent) {
		
		FileOutputStream fos = null;
		try {
			fos= new FileOutputStream(fileName);
			fos.write(fileContent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is used to hash the method object
	 * @param m Method object
	 * @return
	 */
	public static long computeMethodHash(Method m) {
		StringBuffer sb = new StringBuffer();
		sb.append(m.getName().toString());
		sb.append(m.getReturnType().toString());
		for(Class<?> params : m.getParameterTypes()) {
			sb.append(params.toString());
		}
		return sb.toString().hashCode();
	}
	
	/**
	 * This method is used to write the stub's java class content into 
	 * a file
	 * @param content
	 * @param filename
	 */
	public static void writeJaveFile(String content, String filename) {
		File newfile = new File(filename);
		if(!newfile.exists()) {
			try {
				newfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(newfile);
			out.write(content.getBytes(), 0, content.getBytes().length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
