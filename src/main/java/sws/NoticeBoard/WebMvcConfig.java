package sws.NoticeBoard;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/files/image/**") // --1
			.addResourceLocations("file:///Users/shinwonsub/Documents/SpringBoot/NoticeBoard/files/image/"); //--2
	}
}