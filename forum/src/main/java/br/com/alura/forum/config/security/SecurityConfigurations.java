package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

/*
 * Com isso já habilitamos a parte de segurança por padrão o Spring bloqueia o acesso a API 
 * agora e necessario fazer configuração para liberar acesso
 * ao utilizar nyRequest().authenticated(); //qualquer outra requisição tem que estar autenticada
 * Então na Classe usuario colocaremos implements UserDetails e geraremos os metodos solicitado 
 * Criaremos uma outra classe chamada Perfil onde definimos os tipos de pesfis de usuarios
 */

@EnableWebSecurity // Habilitar o modulo de segurança na aplicação
@Configuration // No startup do projeto o Spring vai ler as configurações dessa classe
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AutenticacaoService autenticacaoService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	//Medoto para criação do authenticationManager ao colocar o Bean aí o spring sabe que esse metodo devolve authenticationManager
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {	
		return super.authenticationManager();
	}
	

	//Configurações de autenticação login e acesso
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
		// Dizer qual algoritimo de Hash para senha BCryptPasswordEncoder
		// Dizer para o spring qual a classe que implementa a logica de autenticação
	}
	
	/*	Informar para o Spring Security que não vamos fazer mais autenticação por sessão,
	 * 	e sim autenticação de maneira stateless. Vamor realizar a configuração para utilizar Web Token.
	 *  Desvantagem não temos formulário de login e um controller que fazia a parte de autenticação,
	 *  Vamos precisar criar uma classe responsavel.
	 *  Precisamos liberar a nova url responsavel por login
	 *  -> Actuator -  É uma maneira de você expor informações da sua API para que alguém consiga puxar esses dados e fazer o monitoramento. 
	 */
	
	
	//Configurações de autorização url perfil de acesso
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/topicos").permitAll() //Liberação de Url
		.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
		.antMatchers(HttpMethod.POST, "/auth").permitAll()
		.antMatchers(HttpMethod.GET, "/actuator/**").permitAll() // Habilitar end point que retorna informações em produção não pode ser permitAll 
		.anyRequest().authenticated() // Qualquer outra requisição tem que estar autenticada
		.and().csrf().disable() //Desabilitar a verificação de token
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Avisamos ao Spring security para não criar sessão
		.and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class); // Aque vamos adicionar um novo filtro para recuperar o token e falamos a ordem que no caso que remos que rode primeiro o autenticação para pegar o token 
		//.and().formLogin(); Formulário de login tradicional cria sessão
	}
	
	
	//Configuração de recursos estaticos(js, css, img, etc.)
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring()
	        .antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**"); //lista de string do swagger
	}
	
//	public static void main(String[] args) {
//		System.out.println(new BCryptPasswordEncoder().encode("123456"));
//	}
}
