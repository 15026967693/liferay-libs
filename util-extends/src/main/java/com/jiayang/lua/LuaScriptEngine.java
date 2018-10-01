package com.jiayang.lua;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.jiayang.exceptions.NotDirectoryException;
import com.jiayang.util.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuaScriptEngine {
	private static Logger luaScriptEngineLogger=LoggerFactory.getLogger(org.luaj.vm2.script.LuaScriptEngine.class);
	private  static Globals globals=JsePlatform.standardGlobals();
	private Globals runtimeGlobals=JsePlatform.standardGlobals();
	private static String[] globalPaths=((List<String>)((Map<String, Object>)Config.config.get("luaScript")).get("globalPaths")).toArray(new String[0]);
	private static String[] runtimePaths=((List<String>)((Map<String, Object>)Config.config.get("luaScript")).get("runtimePaths")).toArray(new String[0]);
	static {
		for(String globalPath:globalPaths)
		{
			try {
				File[] files=getAllFiles(Config.config.get("rootPath")+"/"+globalPath,true,new ArrayList<File>());
				for(File file:files) {
			    	globals.loadfile(file.getCanonicalPath()).call();	
			    }
			} catch (NotDirectoryException e) {
				// TODO Auto-generated catch block
				luaScriptEngineLogger.error("脚本全局路径配置错误，您指定了一个非文件夹作为读取脚本的路径，路径为：{}",Config.config.get("rootPath")+"/"+globalPath);
				e.printStackTrace();
			}
			catch (FileNotFoundException e) {
				// TODO: handle exception
				luaScriptEngineLogger.debug("脚本全局路径配置错误，未找到相关文件夹请自行创建相关的文件夹，路径为：{}",Config.config.get("rootPath")+"/"+globalPath);
			}
			
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	private static  File[] getAllFiles(String pathname,boolean root,List<File> result) throws NotDirectoryException, IOException {
		File file=new File(pathname);
		if(!file.exists()&&!root)
		throw new FileNotFoundException();
		if(!file.isDirectory()&&!root)
			return new File[] {file};
		if(!file.isDirectory())
			throw new NotDirectoryException();
		else {
			for(File f:file.listFiles())
			{
				if(!f.isDirectory()&&f.getName().endsWith(".lua"))
				result.add(f);
				else
					getAllFiles(f.getCanonicalPath(),false, result);
					
			}
			
			
			
			
		}
		
		return result.toArray(new File[0]);
	}
	
	private String getRuntimeScriptFileCanonicalPath(final String filename) throws IOException {
		for(String runtimePath:runtimePaths)
		{
			File file=new File(Config.config.get("rootPath")+"/"+runtimePath);
			File[] targetFiles=file.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return name.contains(filename.endsWith(".lua")?filename:filename+".lua");
				}
			});
			if(targetFiles.length>0) {
				return targetFiles[0].getCanonicalPath();
			}
		}
		return null;
		
	}
	
	
	
	
	//并不推荐使用会一次性加载进所有的runtimePaths配置项中目录的脚本，除非脚本量较少，否则请不要使用
	public LuaScriptEngine() {
		for(String runtimePath:runtimePaths)
		{
			try {
				File[] files=getAllFiles(Config.config.get("rootPath")+"/"+runtimePath,true,new ArrayList<File>());
			    for(File file:files) {
			    	runtimeGlobals.loadfile(file.getCanonicalPath()).call();	
			    }
			    luaScriptEngineLogger.debug("开发人员请注意，此构造方法将会将所有lua变量及函数加载进内存，并不推荐使用！请考虑使用String...参数的构造方法");
			} catch (NotDirectoryException e) {
				// TODO Auto-generated catch block
				luaScriptEngineLogger.error("局部脚本位置配置错误，您指定了一个非文件夹作为读取脚本的路径，路径为：{}",Config.config.get("rootPath")+"/"+runtimePath);
				e.printStackTrace();
			}
			catch (FileNotFoundException e) {
				// TODO: handle exception
				luaScriptEngineLogger.debug("局部脚本位置配置错误，未找到相关文件夹请自行创建相关的文件夹，路径为：{}",Config.config.get("rootPath")+"/"+runtimePath);
			}
			
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	/*推荐使用的构造方法将根据参数中的路径加载脚本文件，推荐使用全路径，否则该方法会
	  会自己到runtimePaths配置项中寻找对应的文件名较耗时
	*/
	public LuaScriptEngine(String... runtimeFileNames)  {
		for(String runtimeFileName:runtimeFileNames)
		{
			File file=new File(runtimeFileName);
			if(file.exists())
				try {
					this.runtimeGlobals.loadfile(file.getCanonicalPath()).call();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else if(runtimeFileName.contains("/")||runtimeFileName.contains("\\"))
				throw new RuntimeException("构造函数中存在错误路径，值为："+runtimeFileName);
			else 
			{
				try {
					String filePath=getRuntimeScriptFileCanonicalPath(runtimeFileName);
					if(filePath==null)
					{
						luaScriptEngineLogger.error("在扫描路径中不存在此文件,文件名为:{},请检查编程时构造函数传入的参数",runtimeFileName);
						throw new RuntimeException("在扫描路径中不存在此文件");
					}
						else
						runtimeGlobals.loadfile(filePath).call();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	private static LuaValue[] genArgs(Object[] arguments)
	{
	
		if(arguments==null)
			return new LuaValue[] {};
		LuaValue[] args=new LuaValue[arguments.length];
		for(int i=0;i<arguments.length;i++)
			args[i]=CoerceJavaToLua.coerce(arguments[i]);
		return args;
	}
	private static Object[] genReturns(Varargs returnVal,Class<?>[] returnTypes)
	{
		if(returnTypes==null)
			return null;
		Object[] returns=new Object[returnTypes.length];
		for(int i=0;i<returnTypes.length;i++) {
			returns[i]=CoerceLuaToJava.coerce(returnVal.arg(i+1),returnTypes[i]);
		}
		return returns;
	}
	public static Object[] invokeGlobal(String methodName,Class<?>[] returnTypes,Object... arguments) {
		LuaValue method=globals.get(methodName);
		LuaValue[] args=genArgs(arguments);
		Varargs returnVal=method.invoke(args);
		return genReturns(returnVal, returnTypes);
		
	}
	public Object[] invoke(String methodName,Class<?>[] returnTypes,Object... arguments) {
		LuaValue method=runtimeGlobals.get(methodName);
		LuaValue[] args=genArgs(arguments);
		Varargs returnVal=method.invoke(args);
        return genReturns(returnVal, returnTypes);
		
	}
	public int invoke_I(String methodName,Object... arguments) {
		LuaValue method=runtimeGlobals.get(methodName);
		LuaValue[] args=genArgs(arguments);
		return method.invoke(args).toint(1);
	}
	public float invoke_F(String methodName,Object... arguments) {
		
		LuaValue method=runtimeGlobals.get(methodName);
		LuaValue[] args=genArgs(arguments);
		return method.invoke(args).tofloat(1);
	}

}
