package br.com.alura.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.dto.TokenDto;
import br.com.alura.forum.controller.form.LoginForm;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {
	
	@Autowired
	private AuthenticationManager authManager; //detalhe essa classe precisa ser configurada para realizar injeção de dependencia
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginForm form){
		UsernamePasswordAuthenticationToken dadosLogin = form.converter();
		
		//Parte da autenticação
		try {
			Authentication authentication = authManager.authenticate(dadosLogin); //o spring vai consultar os dados 
			String token = tokenService.gerarToken(authentication);//mandou gerar o token
			//System.out.println(token); verificar criação
			return ResponseEntity.ok(new TokenDto(token, "Bearer")); //precisamos alem de mandar um 200 ok, precisamos mandar o token
			
		} catch (AuthenticationException e) {
			return ResponseEntity.badRequest().build();
		}
		
	
		
		
		
	}

}
