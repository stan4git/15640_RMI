package server;

import java.io.IOException;
import java.lang.reflect.Method;
import util.Util;

/**
 *	This class is used to generated stub class file.
 *	It takes a class name and generate the .java file of stub
 *	which will then be complied.
 */
public class RMIC {

	public static void main(String[] args) {
		if(args.length < 1){
			System.out.println("Usage:java rmic [classname]");
			System.out.println("The classname must like packageName.className");
			System.exit(0);
		}
		String classname = args[0];
		int pos = classname.lastIndexOf(".");
		String packageName = classname.substring(0, pos);
		String classRealName = classname.substring(pos+1, classname.length());
		Class<?> obj;
		try {
			obj = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			System.out.printf("Rmic: There's no such class: %s!\n", classname);
			return;
		}
		
		String buffer = "";
		buffer += "package " +packageName+";\n" +
				"import java.lang.reflect.Method;\n" +
				"import exception.RemoteException;\n" +
				"import message.ExceptionMessage;\n" +
				"import message.MethodInvocationMessage;\n" +
				"import message.ReturnMessage;" +
				"import util.CommunicationUtil;\n" +
				"import util.Util;\n" +
				"import server.RemoteStub;\n" +
				"import server.RemoteRef;\n";
		
		buffer += "public class "+ classRealName +"_Stub implements RemoteStub,";
		for(Class<?> i : obj.getInterfaces()) {
			buffer += i.getName() + ",";
		}
		
		if(buffer.endsWith(","))
			buffer = buffer.substring(0, buffer.length()-1);
		
		buffer += "{\n" +
				"RemoteRef ref;\n" +
				"public "+classRealName+"_Stub(RemoteRef r) {\n" +
				"	this.ref = r;\n" +
				"}\n";
		
		buffer += "public RemoteRef getRR(){ return ref;}\n";
		
		/*
		 * create each of the methods
		 */
		for(Method m : obj.getMethods()) {
			if(m.getDeclaringClass() == Object.class) 
				continue;
			buffer += "public "+m.getReturnType().getName()+" "+m.getName()+"(";
			int counter = 0;
			for(Class<?> param : m.getParameterTypes()) {
				buffer += param.getName()+" a"+counter+",";
				counter++;
			}
			if(buffer.endsWith(","))
				buffer = buffer.substring(0, buffer.length()-1);
			
			buffer += ") {\n";
			
			int length = m.getParameterTypes().length;
			if((length = m.getParameterTypes().length) > 0) {
				buffer += "Class<?>[] types = new Class<?>["+length+"];\n";
				counter = 0;
				for(Class<?> type:m.getParameterTypes()){
					buffer += "types["+counter+"] = "+type.getName()+".class;\n";
					counter++;
				}
			}
			
			boolean flag = true;
			if(m.getReturnType().getName().equals("void"))
				flag = false;
			
			buffer += "Method method;\n" +
					"try {\n" +
					"method = this.getClass().getMethod(\""+m.getName()+"\"";
			if(length > 0)
				buffer +=", types);\n";
			else 
				buffer += ");\n";
			buffer += "} catch (NoSuchMethodException | SecurityException e) {\n" +
					"System.out.println(\"No such method!\");\n" +
					"return";
			if(flag) {
				buffer += " null;\n}";
			}
			else 
				buffer += ";\n}";
			
			buffer += "long key = Util.computeMethodHash(method);\n";
			
			if(length > 0){
					buffer += "Object[] params = new Object["+length+"];\n";

					for(counter = 0;counter < length; counter++){
						buffer += "params["+counter+"] = a"+counter+";\n";
					}
			}
			buffer += "MethodInvocationMessage message = new MethodInvocationMessage (ref.getInstanceID(), key, ";
			
			if(length > 0)
				buffer += "params);";
			else 
				buffer += "null);";
				
			buffer += "\nObject obj = null;\n" +
					"try {\n" +
					"CommunicationUtil.sendMessage(ref.getHost(), ref.getPort(), message);\n";
			

			buffer += "obj = CommunicationUtil.receiveMessage(ref.getHost(), ref.getPort());\n" +
						"if(obj instanceof ExceptionMessage){\n" +
						"throw (RemoteException)((ExceptionMessage)obj).get();\n}";
			
			
			buffer += "} catch (RemoteException e) {\n" +
					"System.err.println(e.getType().toString() + \": \"+ e.getCause().toString());"+
					"e.printStackTrace();";
			
			if(flag)
				buffer += "return null;\n";
			else 
				buffer += "return;\n";
			
			buffer += "}\n";
			
			if(flag)
				buffer += "return " +
						"("+m.getReturnType().getName()+")((ReturnMessage)obj).get();\n";
			
			buffer += "}\n";
			
		}
		
		buffer += "}\n";
		
		/*
		 * output the buffer to generate the source code file
		 */
		Util.writeJaveFile(buffer, packageName+"/"+classRealName+"_Stub.java");
		
		/*
		 * compile the source code file to generate the stub
		 */
		try {
			Process pro = Runtime.getRuntime().exec("javac "+packageName+"/"+classRealName+"_Stub.java");
			pro.waitFor();
			pro = Runtime.getRuntime().exec("rm "+packageName+"/"+classRealName+"_Stub.java");
			pro.waitFor();
			Runtime.getRuntime().exec("mv "+classRealName+"_Stub.class "+packageName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
