package br.com.alura.forum.config.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

	


/*Implementação da Logica abaixo:
 * Para disparar uma requisição, ele tem que adicionar mais um cabeçalho, que é o authorization. 
 * A ideia é colocar o cabeçalho, com o valor bearer, e o token. 
 * Nas próximas requisições, o cliente tem que sempre mandar o cabeçalho authorization com o token. 
 * Eu tenho que autenticar porque como a nossa autenticação é stateless, não existe mais a ideia de usuário logado.
 *  A autenticação é feita para cada requisição. 
 * 
 */

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

	private TokenService tokenService;
	private UsuarioRepository repository;
	
	//Injeção via contrutor
	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository repository) {
		this.tokenService = tokenService;
		this.repository = repository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = recuperarToken(request);
		//System.out.println(token);
		
		boolean valido = tokenService.isTokenValido(token);
		//System.out.println(valido);
		
		/*
		 *  No nosso método principal do filter só chamo o autenticar se o token estiver válido. Se não estiver, 
		 *  não vai autenticar, vai seguir o fluxo da requisição e o Spring vai barrar. 
		 */
		
		if(valido) {
			autenticarCliente(token);
		}
		
		filterChain.doFilter(request, response); 
		
		
	}
	
	
	/* 1 pegou id do token 
	 * 2 recuperou o objeto usuario 
	 * 3 Criou UsernamePasswordAuthenticationToken passando usuario , senha null e perfis 
	 * 4 Chamando a classe que força autenticação so para esse request e será chamado varias vezes
	 */

	private void autenticarCliente(String token) {
		Long idUsuario = tokenService.getIdUsuario(token); 
		Usuario usuario = repository.findById(idUsuario).get(); 
		
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
	}

	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7, token.length());
	}
	
		

}
