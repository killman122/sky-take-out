package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.models.properties.MapProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.List;

/**
 * 配置类，注册web层相关组件
 * 由于config配置类继承于WebMvcConfigurationSupport类, 所以需要重写addResourceHandlers方法
 * 使用Spring中的config注解表示该类是一个配置类, 使用Slf4j注解开启日志log, 需要注意的是在使用注解开发的时候使用的是lombok而不是将这里的日志注解@Slf4j作为主要
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")//获取登录状态
                .excludePathPatterns("/user/shop/status");//获取店铺的营业状态, 由于在登录前就已经获取了店铺的营业状态, 所以需要将获取店铺状态的相关请求进行排除
    }

    /**
     * 通过knife4j生成接口文档
     *
     * @return
     */
    @Bean
    public Docket docket1() {
        //log.info("准备生成接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口文档")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.admin"))//解析指定的包, 一般是解析controller层, 并且将controller层的所有方法都解析出来, 在网页中查看接口文档的时候, 可以看到controller层中的所有方法
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 使用多个Docket对象将文档中的接口分组, 一个Docket对象代表一个接口文档, 一个接口文档中可以包含多个接口, 也可以将接口分组, 比如将用户端的接口和管理端的接口分组
     * 在分组的时候需要注意Docket Bean的组名必须是唯一的, 如果是相同的组名可能会造成在使用的时候出现错误, 并且在分组的时候需要使用.groupName()并且在函数的参数中填写字符串用来为分组取名
     * 通过knife4j生成接口文档
     * @return
     */
    @Bean
    public Docket docket2() {
        //log.info("准备生成接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口文档")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.user"))//解析指定的包, 一般是解析controller层, 并且将controller层的所有方法都解析出来, 在网页中查看接口文档的时候, 可以看到controller层中的所有方法
                .paths(PathSelectors.any())
                .build();
        return docket;
    }


    /**
     * 设置静态资源映射, 并且由于config配置类继承于WebMvcConfigurationSupport类, 所以需要重写addResourceHandlers方法
     * 主要是访问接口文档(html,css,js)
     *
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 重写扩展消息转换器中的方法, 拓展Spring MVC框架中的消息转换器
     * 消息转换器能够实现统一后端返回给前端的数据进行统一的处理, 比如将后端返回的数据转换为json格式的数据, 这样前端就可以直接使用json格式的数据进行处理
     * 也可以将后端中的所有的时间相关的数据进行转换, 转换为指定的时间格式, 这样前端就可以直接使用指定的时间格式进行处理
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("创建消息转换器对象");
        //创建一个消息转换器类对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        //创建一个ObjectMapper对象, 用于将Java对象序列化为json数据
        ObjectMapper jacksonObjectMapper = new JacksonObjectMapper();

        //需要为消息转换器对象设置一个对象转换器, 这里使用的是Jackson的对象转换器, 也可以使用fastjson的对象转换器, 但是需要导入fastjson的依赖
        //对象转换器可以将一个Java对象序列化转换为一个json数据,setObjectMapper()设置对象映射器, 也就是设置对象转换器
        converter.setObjectMapper(jacksonObjectMapper);

        //将自己的消息转换器对象添加到Spring MVC框架中, 这里是添加到converters容器中
        converters.add(0, converter);//消息转换器在添加后是属于末尾的转换器,因为SpringBoot中还有多个消息转换器, 可能不会使用自己的消息转换器, 所以在添加的时候带上索引
    }
}
