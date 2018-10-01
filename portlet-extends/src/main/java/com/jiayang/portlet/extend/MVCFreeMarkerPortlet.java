package com.jiayang.portlet.extend;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.liferay.portal.util.PortalUtil;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jiayang.portlet.annotation.RequestMapping;
import com.jiayang.portlet.annotation.ResourceMapping;
import com.jiayang.portlet.commons.IModelAndView;
import com.jiayang.portlet.commons.ModelAndView;
import com.jiayang.portlet.commons.PathUtil;
import com.jiayang.portlet.enumeration.HttpMethod;
import com.jiayang.portlet.enumeration.ResourceType;
import com.jiayang.portlet.exception.ViewNullExcetion;
import com.jiayang.portlet.freemarker.FreemarkerConfig;
import com.jiayang.portlet.struts.FileStruct;
import com.jiayang.portlet.struts.MethodDataStruct;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.service.persistence.PortletUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.JavaConstants;

import com.liferay.util.bridges.mvc.MVCPortlet;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@SuppressWarnings("deprecation")
public class MVCFreeMarkerPortlet extends MVCPortlet {
    private static Logger mvcFreeMarkerPortletLogger =LoggerFactory.getLogger(MVCFreeMarkerPortlet.class);
    private String basePath="/";
    private static Gson gson=new GsonBuilder().setPrettyPrinting().create(); 
    private Map<String,Map<HttpMethod,Method>> URLMapping=new HashMap<>(); 
    private Map<String,MethodDataStruct> resourceMapping=new HashMap<>(); 
	@Override
	public final void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException{
		// TODO Auto-generated method stub
		ModelAndView modelAndView=showView(renderRequest, renderResponse);
		if(modelAndView==null) {
			mvcFreeMarkerPortletLogger.debug("对不起编程必须重写showView方法并且不能为null");
			throw new RuntimeException("对不起编程必须重写showView方法并且不能为null");
		}
		
		renderRequest.setAttribute("projectPath", renderRequest.getScheme()+"://"+renderRequest.getServerName()+":"+renderRequest.getServerPort()+"/"+getPortletContext().getPortletContextName());
		
		renderRequest.setAttribute("pathUtil", new PathUtil(renderRequest));
		
	    
		
		mergeRenderRequestToModel(renderRequest,modelAndView);
		Template template;
		
		
		try {
			template=FreemarkerConfig.configuration.getTemplate(modelAndView.getView());
			template.process(modelAndView.getModel(),renderResponse.getWriter());
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			mvcFreeMarkerPortletLogger.error("模板解析错误！！！！：{}",e);
			e.printStackTrace();
		} 
		catch (ViewNullExcetion e) {
			e.printStackTrace();
			throw new RuntimeException("showView方法返回的视图视图名名不存在或为空");
			// TODO: handle exception
		}
		
		
		
		
	}
   private String getRequestMappingPath(RequestMapping requestMapping)
   { 
	   if(requestMapping!=null)
		{
		   String path=null;
		   if(requestMapping.path().equals(""))
		      path=requestMapping.value();
		   else
		      path=requestMapping.path();
		   
		   return path;
		}
	   else
		   return "/";
	   
   }
	private void viewToResponse(RenderRequest renderRequest,RenderResponse renderResponse,ModelAndView modelAndView,Method invokeMethod)
	throws IOException,PortletException
	{
        renderRequest.setAttribute("projectPath", renderRequest.getScheme()+"://"+renderRequest.getServerName()+":"+renderRequest.getServerPort()+"/"+getPortletContext().getPortletContextName());
		
		renderRequest.setAttribute("pathUtil", new PathUtil(renderRequest));
		
	    
		
		mergeRenderRequestToModel(renderRequest,modelAndView);
		Template template;
		
		
		try {
			template=FreemarkerConfig.configuration.getTemplate(modelAndView.getView());
			template.process(modelAndView.getModel(),renderResponse.getWriter());
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			mvcFreeMarkerPortletLogger.error("模板解析错误！！！！：{}",e);
			e.printStackTrace();
		} 
		catch (ViewNullExcetion e) {
			mvcFreeMarkerPortletLogger.debug("疑似编程错误",e);
			mvcFreeMarkerPortletLogger.debug("在解析路径{}的时候您在对应的{}方法中返回了一个值为null的视图，当前portlet全类名为：{},如果这不是您的预期行为，请检查代码",getPath(renderRequest),invokeMethod.getName(),this.getClass().getName());
			doView(renderRequest, renderResponse);
		}
		
	}
	
	
	
	
	public  MVCFreeMarkerPortlet() {
		super();
		RequestMapping baseMapping=this.getClass().getAnnotation(RequestMapping.class);
		basePath=getRequestMappingPath(baseMapping);
		for(Method method:this.getClass().getMethods())
		{		
			RequestMapping requestMappingAnnotation=method.getAnnotation(RequestMapping.class);
			if(requestMappingAnnotation!=null)
				{
				 Class<?>[] parameterTypes=method.getParameterTypes();
				   if(!(parameterTypes.length==2
						   &&parameterTypes[0].getName().equals("javax.portlet.RenderRequest")
						   &&parameterTypes[1].getName().equals("javax.portlet.RenderResponse")
						   &&IModelAndView.class.isAssignableFrom(method.getReturnType())))
					   {
				
						  throw new RuntimeException("方法签名错误，错误方法为"+method.getName()+"目前只支持方法签名为([Ljavax.portlet.RenderRequest;[Ljavax.portlet.RenderResponse;)[Lcom.jiayang.portlet.commons.ModelAndView;的方法");
					   }
				
				
				      String path=getRequestMappingPath(requestMappingAnnotation);
					  Map<HttpMethod,Method> methodMap=new HashMap<>();
					  if(requestMappingAnnotation.method()==HttpMethod.ALL)
					  {
						  methodMap.put(HttpMethod.GET, method);
						  methodMap.put(HttpMethod.POST, method);
					  }
				      else
					  methodMap.put(requestMappingAnnotation.method(), method);
					  String mapPath=(this.basePath+path).replaceAll("/+", "/").replaceAll("\\+", "/");
					  this.URLMapping.put(mapPath,methodMap);
					  
				  
				 
				   
				   
				}
			ResourceMapping resourceMapping=method.getAnnotation(ResourceMapping.class);
			if(resourceMapping!=null)
			{
				Class<?>[] parameterTypes=method.getParameterTypes();
				ResourceType resourceType=resourceMapping.resourceType();
				switch(resourceType)
				{
				case JSON:
				if(!(parameterTypes.length==2
						   &&parameterTypes[0].getName().equals("javax.portlet.ResourceRequest")
						   &&parameterTypes[1].getName().equals("javax.portlet.ResourceResponse")
						   &&!method.getReturnType().isPrimitive()))
					   {
				
						  throw new RuntimeException("方法签名错误，错误方法为"+method.getName()+"目前只支持方法签名为([Ljavax.portlet.ResourceRequest;[Ljavax.portlet.ResourceResponse;)[Ljava.lang.Object的方法");
					   }
				
				break;
				case FILE:
						if(!(parameterTypes.length==2
								   &&parameterTypes[0].getName().equals("javax.portlet.ResourceRequest")
								   &&parameterTypes[1].getName().equals("javax.portlet.ResourceResponse")
								   &&FileStruct.class.isAssignableFrom(method.getReturnType())))
							   {
								  throw new RuntimeException("方法签名错误，错误方法为"+method.getName()+"目前只支持方法签名为([Ljavax.portlet.ResourceRequest;[Ljavax.portlet.ResourceResponse;)[Lcom.jiayang.portlet.struts.FileStruct的方法");
							   }
					
					break;
				
				
				}
				
				
				
				
				String[] resourceId=resourceMapping.resourceId();
				
				String encoding=resourceMapping.encoding();
				for(String id:resourceId)
				{
					MethodDataStruct methodDataStruct=new MethodDataStruct();
					methodDataStruct.setMethod(method);
					methodDataStruct.getData().put("resourceType",resourceType);
					methodDataStruct.getData().put("encoding", encoding);
					this.resourceMapping.put(id, methodDataStruct);
				
				}
			}
			
		}
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	
	
	
	@Override
	public final void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		String resourceId=resourceRequest.getResourceID();
		MethodDataStruct invokeMethodStruct=resourceMapping.get(resourceId);
		if(invokeMethodStruct==null) {
			HttpServletResponse response=PortalUtil.getHttpServletResponse(resourceResponse);
			response.setStatus(404);
			mvcFreeMarkerPortletLogger.debug("未映射资源请自行添加方法，资源ID:{}",resourceId);
			return;
			
			
		}
		Method invokeMethod=invokeMethodStruct.getMethod();
		ResourceType resourceType=invokeMethodStruct.getAttribute("resourceType");
		String encoding=invokeMethodStruct.getAttribute("encoding");
		System.out.println(resourceId);
		
		
		
		
		switch (resourceType) {
		case JSON:
			resourceResponse.setContentType(ResourceType.JSON.getMIMEType()+"charset="+encoding);
			
			
			
			
			try {
				
				Object resultBean=invokeMethod.invoke(this,resourceRequest,resourceResponse);
			    if(resultBean instanceof String)
			    	resourceResponse.getWriter().print(resultBean);
			    else
				resourceResponse.getWriter().print(gson.toJson(resultBean));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				mvcFreeMarkerPortletLogger.error("错误：",e);
				throw new RuntimeException("修饰方法参数错误，方法签名必须为([Ljavax.portlet.ResourceRequest;[Ljavax.portlet.ResourceResponse)[Ljava.lang.Object");
				
			}
			
			break;
		case FILE:

			try {
				FileStruct fileStruct=(FileStruct) invokeMethod.invoke(this,resourceRequest,resourceResponse);
			   if(fileStruct.getFile()==null) {
				   mvcFreeMarkerPortletLogger.error("错误，未设置文件请检查您的编程,错误方法为：",invokeMethod.getName());
				   throw new RuntimeException("错误，未设置文件请检查您的编程");
			   }
				byte[] bytes=FileUtil.getBytes(fileStruct.getFile());
				 HttpServletRequest request = PortalUtil.getHttpServletRequest(resourceRequest);
				 HttpServletResponse response = PortalUtil.getHttpServletResponse(resourceResponse);
				
				ServletResponseUtil.sendFile(request, response, fileStruct.getDownloadName(), bytes, fileStruct.getContentType());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				mvcFreeMarkerPortletLogger.error("错误：",e);
				throw new RuntimeException("修饰方法参数错误，方法签名必须为([Ljavax.portlet.ResourceRequest;[Ljavax.portlet.ResourceResponse)[Lcom.jiayang.portlet.struts.FileStruct;");
				
			}
			catch (ClassCastException e) {
				// TODO: handle exception
			}
			
			break;
		default:
			break;
		}
		
		
		
		
		
		
	}
	@Override
	protected final void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		// TODO Auto-generated method stub
		String path=getPath(renderRequest);
		if(path==null) {
			doView(renderRequest, renderResponse);
		  
		}
		else
		{
			
			
			
			
		   HttpMethod method=HttpMethod.valueOf(PortalUtil.getHttpServletRequest(renderRequest).getMethod());
		   Method invokeMethod=URLMapping.get(getPath(renderRequest)).get(method);
		   
			   
			   try {
			      ModelAndView modelAndView=(ModelAndView)invokeMethod.invoke(this,renderRequest,renderResponse); 
			      //合并portlet路径和url生成工具到modelAndView
			      renderRequest.setAttribute("projectPath", renderRequest.getScheme()+"://"+renderRequest.getServerName()+":"+renderRequest.getServerPort()+"/"+getPortletContext().getPortletContextName());	
				  renderRequest.setAttribute("pathUtil", new PathUtil(renderRequest));
                  mergeRenderRequestToModel(renderRequest,modelAndView);
			      
			      
			      
			      
			      
			      
			      
			      viewToResponse(renderRequest, renderResponse, modelAndView,invokeMethod);
			      
			      
			      
			   }
			   catch (IllegalAccessException e) {
				   e.printStackTrace();
				// TODO: handle exception
			}
			   catch (InvocationTargetException e) {
				   e.printStackTrace();
				  
				   mvcFreeMarkerPortletLogger.error("错误：{}",e);
				// TODO: handle exception
			}
			   
		  
		   
		   
		   
		   
           
		}
		return;
	}




	protected String getPath(PortletRequest portletRequest) {
		String mvcPath = portletRequest.getParameter("mvcPath");

		// Check deprecated parameter

		if (mvcPath == null) {
			mvcPath = portletRequest.getParameter("jspPage");
		}

		return mvcPath;
	}










	protected ModelAndView showView(RenderRequest renderRequest, RenderResponse renderResponse) {
		
		return null;
	}
	
	
	private Map<String, Object> renderRequestToMap(RenderRequest request)
	{
		Map<String, Object> result=new HashMap<>();
		Enumeration<String> enumeration=request.getAttributeNames();
		while(enumeration.hasMoreElements()) {
			String attrName=enumeration.nextElement();
			result.put(attrName,request.getAttribute(attrName));
		    
		}
		return result;
	}
	private void mergeRenderRequestToModel(RenderRequest renderRequest,ModelAndView modelAndView) {
		Map<String,Object> requestMap=renderRequestToMap(renderRequest);
		Map<String,Object> model=modelAndView.getModel();
		for(String key:requestMap.keySet())
		{
			if(model.containsKey(key))
				mvcFreeMarkerPortletLogger.debug("您覆盖了model中的值,键名为：{}，键的初始值：{}，现在值：{}",key,model.get(key),requestMap.get(key));
			model.put(key, requestMap.get(key));
		}
		
	}
	
	

}
