package com.dcloud.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public class DCloudTask implements Serializable{
	private String task_class_name;
	private byte[] task_class_data;
	private String task_function_name;
	private Object[] task_params;
	private int task_result;
	private Object task_function_return_value;
	
	public void setTask_function_return_value(Object taskFunctionReturnValue) {
		task_function_return_value = taskFunctionReturnValue;
	}

	public void setTask_result(int taskResult) {
		task_result = taskResult;
	}

	public String getTask_class_name() {
		return task_class_name;
	}

	public byte[] getTask_class_data() {
		return task_class_data;
	}

	public String getTask_function_name() {
		return task_function_name;
	}

	public Object[] getTask_params() {
		return task_params;
	}

	public int getTask_result() {
		return task_result;
	}

	public Object getTask_function_return_value() {
		return task_function_return_value;
	}

	public DCloudTask() {
		// TODO Auto-generated constructor stub
	}
	
	public DCloudTask(String taskClassName,
			String taskFunctionName, Object[] taskParams) throws ClassNotFoundException, IOException {
		super();
		task_class_name = taskClassName;
		task_function_name = taskFunctionName;
		task_params = taskParams;
		
		Class cl = Class.forName(taskClassName);
		
		if(cl!=null)
			formImage(cl, taskClassName);
	}
	
	private void formImage(Class cl, String p_className) throws IOException	{
		try {
		
			//Formulate class name for locating on the drive.
			//If the file exists then Open a fileinputstream.
			String className = "bin/"+p_className.replace('.', '/') +".class";
			File f = new File(className);		
			System.out.println("class "+className+" exists : "+f.exists());
			FileInputStream in;
			if(f.exists())	{
				in = new FileInputStream(f);
				System.out.println("Got inputstream for class file");
			}
			else {
				System.err.println("File "+p_className+" doesnot exist");
				throw new IOException("DCloudTask:formImage - File "+p_className+" doesnot exist");
			}
			
			//Open a ByteArrayOutputStream and read bytes from file into it.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			int count;
			int bufferSize = 1024*10; // 10KB buffer		
			byte[] buffer = new byte[bufferSize];		
			while ((count = in.read(buffer)) > 0)
			  baos.write(buffer, 0, count);
			
			//Fetch the byte data in the ByteArrayOutputStream as a byte array.
			this.task_class_data = baos.toByteArray();
			
		} catch (IOException e) {
			System.err.println("Faiiled to get byte data of class. Throwing an IOException.");
			throw e;
		} 				
	}	
	
}
