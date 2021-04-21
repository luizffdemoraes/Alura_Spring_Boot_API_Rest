package br.com.alura.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.forum.modelo.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

	/***
	 * Query informando após findBy no caso a entidade e o atributo para realizar a consulta; 
	 * Dessa forma filtramos por parametros usando o spring data;
	 * E preciso declarar só a assinatura do metodo que é gerado automaticamente;
	 * _ é para tirar problemas de ambiguidade no momento de filtragem;
	 * Vantagem e que o spring monta aquery para vc;
	 * Desvantagem e que você deve seguir esse padrão de nomenclatura.
	 */
	List<Topico> findByCursoNome(String nomeCurso);
	
	/**
	 * É possivel criar um metodo com sua proprio nomenclatura;
	 * Não e gerado a query automatica porem e possivel a query a ser realizada com @Query;
	 * No @Query é necessario criar seu proprio JPQL e utilizar anotação @Param para definição de parametros;
	 */

	@Query("SELECT t FROM Topico t WHERE t.curso.nome = :nomeCurso")
	List<Topico> carregarPorNomeDoCurso(@Param("nomeCurso") String nomeCurso);
	

}
