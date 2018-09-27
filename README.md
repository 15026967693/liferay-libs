# liferay-libs
本项目基于gradle构建，gradle就是牛逼～_~写起来甩maven两条街就是编翻译的慢点反正有增量编译无所谓。
### liferay添加freemarker支持使用方法：
在WEB-INF目录下添加extend.yaml配置文件，文件会自动组合jar包中的默认配置，至于为什么，有兴趣的可以看源码![](https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=898286550,2901399876&fm=26&gp=0.jpg)
文件详情如下：
``` yaml
template:
#freemarker文件的模板基路径
        path: /template
#freemarker文件的后缀名
        suffix: .ftl
#友好连接名有待修改
friendlyURL: web/guest/home
```
## 另外不再复写onView方法改为复写showView方法，至于为什么，有兴趣的可以看源码![](https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=898286550,2901399876&fm=26&gp=0.jpg)，反正不复写他就会直接抛出一个RuntimeException让你咻的一下爆红(就是我干的)![](https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=898286550,2901399876&fm=26&gp=0.jpg)。

## showView的方法返回的是一个自定义的ModelAndView，定义如下，有个人需求的可以自己改，将来应该会换成接口，现在先这样：
``` java
public class ModelAndView implements IModelAndView<Map<String, Object>> {
	private static Logger mvLogger=LoggerFactory.getLogger(ModelAndView.class);
	private String view;
	private Map<String, Object> model=new HashMap<>();
	@Override
	public String getView() {
		// TODO Auto-generated method stub
		return view+((Map<String, Object>)Config.config.get("template")).get("suffix");
	}

	@Override
	public Map<String, Object> getModel() {
		
		return model;
	}

	@Override
	public void setView(String view) {
		this.view=view;
		
	}

	@Override
	public void setModel(Map<String, Object> model) {
		// TODO Auto-generated method stub
		this.model=model;
	}
	public void setAttr(String name,Object value)
	{
	  if(model.containsKey(name))
		  mvLogger.debug("您覆盖了一个已有元素请确定您的操作中不存在不必要的操作，这可能降低程序的效率，键值为：{}",name);
	  model.put(name, value);
	}

}
```

## 重要：不再使用默认的MVCPortlet而是MVCFreeMarkerPortlet，新加的拓展全是jiayang的包名下的。


# ajax URL生成的研究
liferay根据ajax地址自动调用serveResource方法，此研究旨在认为构造ajax地址，方便freemarker调用，目前
测试的ajaxurl地址为http://localhost:10000/web/guest/home?p_p_id=main_WAR_LiferayTestportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_cacheability=cacheLevelPage&p_p_col_id=column-3&p_p_col_count=1&p_p_resource_id=test1

其中http://localhost:10000/web/guest/home应该是固定部分
***p_p_id***参数经过验证可以用((LiferayPortletConfig)request.getAttribute(
JavaConstants.JAVAX_PORTLET_CONFIG)).getPortletId()方法获取到，其中request为一个PortletRequest或其子类对象有待验证但基本可以确定是他了

* ajax已经提供工具类ajaxUtil,可直接在ftl模板中${ajaxUtil.getAjaxPath("resourceId")}，剪掉了p_p_col_id、p_p_col_count、p_p_state这三个参数不知道干嘛的，有待测试。


加入了视图控制器，requestMapping是自己写的并不是spring的请注意！！
路径请使用pathUtil在模板中生成
``` java
  @RequestMapping(path="/test2",method=HttpMethod.GET)
	public ModelAndView  test2(RenderRequest renderRequest,RenderResponse response){
		renderRequest.setAttribute("hello", "this is hello message");
		return new ModelAndView("/test");
	}
```

