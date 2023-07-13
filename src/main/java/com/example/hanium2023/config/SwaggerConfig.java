package com.example.hanium2023.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    private String version = "V0.1";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("🚌탈래말래_5MR🚌")
                .description("본 프로젝트는 사용자의 GPS를 기반으로 현 위치에서 탑승 가능한 지하철의 실시간 정보를 푸시 메시지로 제공하는 \n‘지하철 도착 예정 ⏰실시간 알리미(탈래말래)⏰ 프로젝트’ 입니다!")
                .version(version)
                .contact(new Contact("5MR📌", "배포 링크", "이메일"))
                .build();
    }
}