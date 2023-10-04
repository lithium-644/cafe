package com.ktdsuniversity.edu.beans;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configurable // spring boot 설정을 해주는 클래스로 변경
@EnableWebMvc // WebMVC Module을 사용하겠음 (Validator 사용하기 위해서)
public class WebConfig implements WebMvcConfigurer {
	

}
