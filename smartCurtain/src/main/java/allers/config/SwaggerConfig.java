package allers.config;

import allers.service.RaspberryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;




@Configuration
@EnableScheduling
@EnableSwagger2
public class SwaggerConfig {


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Control - service Microservice REST Interface")
                .description("The REST API for the control service.")
                .contact(new Contact("", "", ""))
                .version("1.0.0")
                .build();
    }


    @Bean
    public RaspberryService raspberryService(){
        return new RaspberryService();
    }

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("allers.controller"))
                .paths(PathSelectors.ant("/api/*"))
                .build();
    }




}
