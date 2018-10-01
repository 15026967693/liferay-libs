package com.jiayang.portlet.commons;

import javax.portlet.RenderRequest;

import com.jiayang.util.config.Config;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.util.JavaConstants;

public class AjaxUtil {
	private String serverPath;
	private String ppid;
	public  AjaxUtil(RenderRequest request)
	{
		serverPath=request.getScheme()+"://"+request.getServerName()+":"
	+request.getServerPort()+"/";
		ppid=((LiferayPortletConfig)request.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG)).getPortletId();
	  
	}
	public String getAjaxPath(String resourceId) {
		//p_p_id=main_WAR_LiferayTestportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_cacheability=cacheLevelPage&p_p_col_id=column-3&p_p_col_count=1&p_p_resource_id=test1
		String configFriendlyURL=(String) Config.config.get("friendlyURL");
		String friendlyURL=configFriendlyURL.endsWith("/")?configFriendlyURL.substring(0, configFriendlyURL.length()-1):configFriendlyURL;
		return serverPath+friendlyURL
				+ "?p_p_id="
				+ ppid
				+ "&p_p_lifecycle=2"
				+ "&p_p_mode=view"
				+ "&p_p_cacheability=cacheLevelPage"
				+ "&p_p_resource_id="+resourceId;
	}
}
