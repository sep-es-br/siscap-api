package br.gov.es.siscap.specification;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.utils.FormatadorData;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor
public class ProjetoSpecification {

	public static Specification<Projeto> filtroSiglaTitulo(String siglaOuTitulo) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.or(
					criteriaBuilder.like(criteriaBuilder.lower(root.get("sigla")), "%" + siglaOuTitulo.toLowerCase() + "%"),
					criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + siglaOuTitulo.toLowerCase() + "%")
		);
	}

	public static Specification<Projeto> filtroIdOrganizacao(Long idOrganizacao) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organizacao").get("id"), idOrganizacao);
	}

	public static Specification<Projeto> filtroStatus(String status) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
	}

	public static Specification<Projeto> filtroData(String dataPeriodoInicio, String dataPeriodoFim) {
		LocalDateTime inicio = FormatadorData.parseSimples(dataPeriodoInicio.isBlank() ? FormatadorData.DATA_MINIMA : dataPeriodoInicio);
		LocalDateTime fim = FormatadorData.parseSimples(dataPeriodoFim.isBlank() ? FormatadorData.DATA_MAXIMA : dataPeriodoFim);

		return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("criadoEm"), inicio, fim);
	}
}