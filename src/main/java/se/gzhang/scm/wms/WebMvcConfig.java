/**
 * Copyright 2018
 *
 * @author gzhang
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.gzhang.scm.wms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import se.gzhang.scm.wms.authorization.controller.WebPageAccessControllerFilter;
import se.gzhang.scm.wms.authorization.service.UserService;

/*******
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;
*****/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    // Configuration for japser report
    private final static String REPORT_DATA_KEY = "datasource";
    private final static String PATH_KEY = "classpath:jaspertemplate/";
    private final static String TYPE_KEY = ".jrxml";
    private final static String VIEW_KEY = "Report";

    @Bean
    // When we need to encrypt password in the server side
    // we can call passwordEncoer.encode(password) to encrypt the password
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Bean
    public FilterRegistrationBean filterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        WebPageAccessControllerFilter webPageAccessControllerFilter = new WebPageAccessControllerFilter();
        // Add the auto wired user service into the page access controller as the controller will
        // use the user service to validate whether the current user has access to the page
        webPageAccessControllerFilter.setupUserService(userService);

        registration.setFilter(webPageAccessControllerFilter);
        registration.addUrlPatterns("/*");
        registration.setName("webPageAccessController");
        registration.setOrder(1);
        return registration;
    }
/*********
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public JasperReportsViewResolver getJasperReportsViewResolver() {
        JasperReportsViewResolver resolver = new JasperReportsViewResolver();
        resolver.setPrefix(PATH_KEY); //resource文件夹下放模板的路径
        resolver.setSuffix(TYPE_KEY); //模板文件的类型，这里选用jrxml而不是编译之后的jasper

        resolver.setReportDataKey(REPORT_DATA_KEY);
        resolver.setViewNames("*" + VIEW_KEY + "*"); //视图名称，模板名称需要符合 *你定义的key* 如*Report*
        resolver.setViewClass(JasperReportsMultiFormatView.class); //视图类
        resolver.setOrder(0); //顺序为第一位
        return resolver;
    }
****/

}