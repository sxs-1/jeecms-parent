package com.jeecms.common;

import com.jeecms.admin.config.CmsAdminConfig;
import com.jeecms.common.constants.WebConstants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;

/**
 * 注册前端Dispatcher
 * 
 * @author: tom
 * @date: 2019年1月5日 下午2:06:38
 * @Copyright: 江西金磊科技发展有限公司 All rights reserved.Notice
 *             仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Configuration
@EnableAspectJAutoProxy
public class AdminDispatcherConfig  {


	/**
	 * 后台DispatcherServlet。前后台使用不同的DispatcherServlet
	 *
	 * @return
	 */
	@Bean(name = "cmsAdminDispatcherServlet")
	public DispatcherServlet cmsAdminDispatcherServlet() {
		AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();
		// 使用CmsAdminConfig作为配置类
		servletAppContext.register(CmsAdminConfig.class);
		return new DispatcherServlet(servletAppContext);
	}

	/**
	 * 手动注册后台DispatcherServlet。只处理`/admin/*`相关请求
	 *
	 * @param multipartConfigProvider
	 *            获取springboot自动定义的上传配置对象，实现前后台统一的上传配置
	 * @return
	 */
	@Bean(name = "cmsAdminDispatcherServletRegistration")
	public ServletRegistrationBean<DispatcherServlet> cmsAdminDispatcherServletRegistration(
			ObjectProvider<MultipartConfigElement> multipartConfigProvider, DispatcherServlet cmsAdminDispatcherServlet) {
		ServletRegistrationBean<DispatcherServlet> registration = new
				ServletRegistrationBean<>(cmsAdminDispatcherServlet);
		// 必须指定启动优先级，否则无法生效
		registration.setLoadOnStartup(1);
		registration.setName("cmsAdminDispatcherServlet");
		registration.addUrlMappings(WebConstants.ADMIN_PREFIX + "/*");
		// 注册上传配置对象，否则后台不能处理上传
		/*MultipartConfigElement multipartConfig = multipartConfigProvider.getIfAvailable();
		if (multipartConfig != null) {
			registration.setMultipartConfig(multipartConfig);
		}*/
		return registration;
	}


}
