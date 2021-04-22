package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

	// injeção de dependencia
	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;
	
	/***
	 * -> Paginação conceito utilizado para controlar o retorno das informações em grande quantidade
	 * para ao devolver os registros de forma controlada de pouco em pouco.
	 * Para implementar utilizaremos 2 parametros.
	 * -> O RequestParam define que os parametros serão de url / request e define como obrigatorio
	 * (required = false) define que o parametro não é obrigatorio no caso usaremos para variavel nomeCurso
	 * Spring data tem um esquema para paginação usando a interface Pageable ea classe PageRequest
	 * precisamo revificar o retorno pois ele devolve um Page não um List e o retorno começa pelo 0.
	 * -> Permitir que a escolha de um parametro para ordenação e definir a forma que será ordenada ASC ou DESC
	 * -> Simplificação e organização de paginação utilizando Pageable como parametro adicionando uma anotação na classe main
	 * -> Com isso habilitamos um modulo no projeto focado em paginação
	 * page=0&size=10&sort=id,desc
	 * essa anotação PageableDefault define o comportamento padrão - parametros, 
	 * dessa forma de ordenação assim não obrigatorio na URL
	 * -> Vamos inserir uma anotação com atributo de identificação para guardar o retorno do metodo em cache
	 * -> Habilitar log do Hibernate para informar todas as vezes que realizar uma chamada na base de dados
	 * E necessário ir em aplication.properties para realizar a configuração 
	 * ele consulta e guarda no chache de acordo com alteração melhorando a performace
	 */

	@GetMapping    
	@Cacheable(value = "listaDeTopicos")
	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso, 
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao) {
		
//		Pageable paginacao = PageRequest.of(pagina, qtd, Direction.DESC, ordenacao);
		
		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);
		}

	}

	/**
	 *  Agora precisamos avisar ao Spring que quando um novo topico for cadastrado, autalizado ou excluido
	 * é necessário atualizar o cache, nos vamos modificar o metodo cadastrar para limpar o cache quando ele for utilizado.
	 */
	
	//Códigos com tratamento de erros 404 para tratar id que não pertence a base de dados
	@PostMapping
	@Transactional  //O Request Body define que os parametros serão no corpo da requisição
	@CacheEvict(value = "listaDeTopicos", allEntries = true )  //Anotação para limpar cache com paramatro de value
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);

		// O código 201 devolve uri e um corpo na resposta
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	

	@GetMapping("/{id}")
	public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id) {
		Optional<Topico> topico = topicoRepository.findById(id);
		if (topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
		}

		return ResponseEntity.notFound().build();
	}

	// Put sobrescrever o recurso atualizar tudo Patch um subconjunto alguns campos
	@PutMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true )  //Anotação para limpar cache com paramatro de value
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}

		return ResponseEntity.notFound().build();

	}

	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true )  //Anotação para limpar cache com paramatro de value
	public ResponseEntity<?> remover(@PathVariable Long id) {
		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();
	}
	
	
	

//	@GetMapping("/{id}")
//	public DetalhesDoTopicoDto detalhar(@PathVariable  Long id) {
//		Topico topico = topicoRepository.getOne(id);
//		return new DetalhesDoTopicoDto(topico);
//	}

//	@GetMapping("/{id}")
//	public TopicoDto detalharTop(@PathVariable  Long id) {
//		Topico topico = topicoRepository.getOne(id);
//		return new TopicoDto(topico);
//	}

//	//Put sobrescrever o recurso atualizar tudo Patch um subconjunto alguns campos
//	@PutMapping("/{id}")
//	@Transactional
//	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form){
//		Topico topico = form.atualizar(id, topicoRepository);
//		
//		return ResponseEntity.ok(new TopicoDto(topico));
//	}

//	@DeleteMapping("/{id}")
//	@Transactional
//	public ResponseEntity<?> remover(@PathVariable Long id) {
//		topicoRepository.deleteById(id);
//
//		return ResponseEntity.ok().build();
//	}
}
