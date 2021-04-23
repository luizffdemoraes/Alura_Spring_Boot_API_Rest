package br.com.alura.forum.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



@Service
public class TokenService {
	
	@Value("${forum.jwt.expiration}") //Injetar parametros do aplication.properties
	private String expiration;
	
	@Value("${forum.jwt.secret}") //Injetar parametros do aplication.properties
	private String secret;
	

	public String gerarToken(Authentication authentication) {
		
		Usuario logado = (Usuario) authentication.getPrincipal(); //ele retorna um objetc é necessario fazer um cast
		Date hoje = new Date();
		Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration)); //converte a data para long ao inves de string
		
		return Jwts.builder() //cria objeto onde vc seta informações para definir o token
				.setIssuer("API do Fórum da Alura") //Quem esta gerando esse token
				.setSubject(logado.getId().toString()) //Passar id transformando em String
				.setIssuedAt(hoje) //Passar data de geração do token
				.setExpiration(dataExpiracao) //Passar data de expiração
				.signWith(SignatureAlgorithm.HS256, secret) //Forma de geração do Token 
				.compact(); //Compactar em uma string
	}


	public boolean isTokenValido(String token) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token); //fazer validação de token 
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	public Long getIdUsuario(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody(); 
		return Long.parseLong(claims.getSubject());
	}
	
	

}
