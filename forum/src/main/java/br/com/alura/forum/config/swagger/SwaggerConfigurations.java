package br.com.alura.forum.config.swagger;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.alura.forum.modelo.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigurations {
	
	//Apos configurar e necessario liberar a url pois estamos utilizando Spring Security
	
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2) //Tipo de documentação
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.alura.forum")) //A partir de qual pacote ele deve começar a ler
                .paths(PathSelectors.ant("/**")) //quais endereços devem fazer analise todos
                .build() 
                .ignoredParameterTypes(Usuario.class) //quero que ele ignore urls que trabalhem com nossa classe usuario
                .globalOperationParameters(  //adicionar parametros globais
                        Arrays.asList( //lista com parametros
                                new ParameterBuilder() // iniciar contrução contrução
                                    .name("Authorization") //nome do cabeçalho
                                    .description("Header para Token JWT") //o que esse parametro
                                    .modelRef(new ModelRef("string")) //tipo do parametro
                                    .parameterType("header") //tipo de parametro
                                    .required(false) //opcional
                                    .build())); //contruir
    }

}