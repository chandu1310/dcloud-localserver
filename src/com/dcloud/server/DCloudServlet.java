package com.dcloud.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dcloud.common.DCloudConstants;
import com.dcloud.common.DCloudTask;


/**
 * Servlet implementation class DCloudServlet
 */
public class DCloudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DCloudServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String contentType = request.getContentType();
	    System.out.println("Content Type ="+contentType);
		InputStream in = request.getInputStream();		
		OutputStream os = response.getOutputStream();
		
		if( contentType!=null && !"".equals(contentType) && in != null && os != null)
		{

				ObjectInputStream ois = new ObjectInputStream( in );
				ObjectOutputStream oos = new ObjectOutputStream( os );
				response.setContentType("application/java-byte-code");
				
				System.out.println("Got a connection");		
				
				try {
					
					Object o = ois.readObject();
					
					DCloudTask resp = new DCloudTask();
					resp.setTask_result(-1);
					resp.setTask_function_return_value(null);
					
					if(o!=null && o instanceof DCloudTask)
					{
							DCloudTask img = (DCloudTask)o;

							byte[] imgB = img.getTask_class_data();
							String className = img.getTask_class_name();
							String methodName = img.getTask_function_name();
							Object[] methodParams = img.getTask_params();
							System.out.println("Read a ClassImage object. It has an array with length: "+imgB.length);			
							System.out.println("OK. Byte array kuda undi. Daani length : "+imgB.length);
							System.out.println("Trying to load and invoke method: "+methodName+ " with params size: "+methodParams.length);
							
							Object result = DCloudClassLoader.loadClassImageAndInvokeMethod(className, imgB, methodName, methodParams);
							
							img.setTask_function_return_value(result);
							
							resp = img;
							resp.setTask_result(DCloudConstants.EXEC_DONE);

					} else
					{
						System.out.println("object read is null or is not a ClassImage object");
						resp.setTask_result(DCloudConstants.FAILED);
						resp.setTask_function_return_value(null);
					}
					
					oos.writeObject(resp);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block					
					e.printStackTrace();
				}
		}
		os.flush();
		os.close();
		in.close();
		
	}

}
