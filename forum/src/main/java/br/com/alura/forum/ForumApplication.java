package br.com.alura.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/***
 * Com essa anotação EnableSpringDataWebSupport habilitamos esse suporte, para o Spring pegar da requisição, 
 * dos parâmetros da url os campos, as informações de paginação e ordenação, e repassar isso para o Spring data.
 * Com essa anotação EnableCaching habilitamos o uso de caching na aplicação
 * Habilitamos o Swagger no projeto para realizar a documentação agora precisamos criar uma classe de configuração
 */

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableCaching
@EnableSwagger2
public class ForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}

}
