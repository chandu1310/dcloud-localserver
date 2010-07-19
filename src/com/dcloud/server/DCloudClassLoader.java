package com.dcloud.server;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

public class DCloudClassLoader extends URLClassLoader{   
	private static DCloudClassLoader appLoader = null;
	private boolean TRACE = true;
	
	private byte[] classImageData = null;
	private String className = null; 
	
    public static Object loadClassImageAndInvokeMethod (String class_name, byte[] img, String methodName, Object[] methodParams)
        throws Exception
    {
    	if(class_name == null || "".equals(class_name) || img == null || img.length == 0)
    		return null;
    	
    	if(appLoader == null)
    		appLoader = new DCloudClassLoader (DCloudClassLoader.class.getClassLoader (), new File("."));
    	
            
            // Thread context loader must be adjusted as well:
            Thread.currentThread ().setContextClassLoader (appLoader);
            
            appLoader.classImageData = img;
            appLoader.className = class_name;
            
            final Class classInstance = appLoader.loadClass (class_name);
            
            Object obj = classInstance.newInstance();
            System.out.println("Loaded class and created an instance.");

            
            final Method appmain = classInstance.getMethod (methodName, new Class [] {String[].class});
            final Object [] appargs = methodParams;
            //System.arraycopy (args, 3, appargs, 0, appargs.length);
            
            Object result = appmain.invoke (obj, appargs);            
            
            return result;
    }
    
    /**
     * Overrides java.lang.ClassLoader.loadClass() to change the usual parent-child
     * delegation rules just enough to be able to "snatch" application classes
     * from under system classloader's nose.
     */
    @Override
	public Class loadClass (final String name, final boolean resolve)
        throws ClassNotFoundException
    {
        if (TRACE) 
        	System.out.println ("loadClass (" + name + ", " + resolve + ")");
        
        Class c = null;
        
        // First, check if this class has already been defined by this classloader
        // instance:
        c = findLoadedClass (name);
        
        if (c == null)
        {
            Class parentsVersion = null;
            try
            {
                // This is slightly unorthodox: do a trial load via the
                // parent loader and note whether the parent delegated or not;
                // what this accomplishes is proper delegation for all core
                // and extension classes without my having to filter on class name: 
                parentsVersion = getParent ().loadClass (name);
                
                if (parentsVersion.getClassLoader () != getParent ())
                    c = parentsVersion;
            }
            catch (ClassNotFoundException ignore) {}
            catch (ClassFormatError ignore) {}
            
            if (c == null)
            {
                try
                {
                    // OK, either 'c' was loaded by the system (not the bootstrap
                    // or extension) loader (in which case I want to ignore that
                    // definition) or the parent failed altogether; either way I
                    // attempt to define my own version:
                    c = findClass (name);
                }
                catch (ClassNotFoundException ignore)
                {
                    // If that failed, fall back on the parent's version
                    // [which could be null at this point]:
                    c = parentsVersion;
                }
            }
        }
        
        if (c == null)
            throw new ClassNotFoundException (name);
        
        if (resolve)
            resolveClass (c);
        
        return c;
    }
        
    /**
     * Overrides java.new.URLClassLoader.defineClass() to be able to call
     * crypt() before defining a class.
     */
    @Override
	protected Class findClass (final String name)
        throws ClassNotFoundException
    {
        if (TRACE) System.out.println ("findClass (" + name + ")");
        try
        {
            
            return defineClass (name, classImageData, 0, classImageData.length);
        }
        catch (Exception ioe)
        {
            throw new ClassNotFoundException (name);
        }
    }
    
    /**
     * This classloader is only capable of custom loading from a single directory. 
     */
    @SuppressWarnings("deprecation")
	private DCloudClassLoader (final ClassLoader parent, final File classpath)
        throws MalformedURLException
    {
        super (new java.net.URL [] {classpath.toURL()}, parent);
        
        if (parent == null)
            throw new IllegalArgumentException ("EncryptedClassLoader" +
                " requires a non-null delegation parent");
    }
}