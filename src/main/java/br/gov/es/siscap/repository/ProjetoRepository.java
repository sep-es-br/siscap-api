package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long>, JpaSpecificationExecutor<Projeto> {

	boolean existsBySigla(String sigla);

        @Query("""
            SELECT p
            FROM Projeto p
            WHERE EXISTS (
                SELECT 1
                FROM ProjetoPrograma pp
                WHERE pp.projeto = p
                  AND pp.programa = :programa
                  AND pp.apagadoEm IS NULL
            )
            """)
         Set<Projeto> findAllByPrograma(@Param("programa") Programa programa);

	@Query("select count(p) from Projeto p where year(p.criadoEm) = year(current_date)")
	int contagemAnoAtual();

	@Modifying
    @Query(value = "DELETE FROM projeto WHERE id = :idProjeto", nativeQuery = true)
    void deleteFisico(@Param("idProjeto") Long id);

}