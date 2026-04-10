package br.gov.es.siscap.specification;

import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.StatusProjeto;
import br.gov.es.siscap.utils.FormatadorData;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
public class ProjetoSpecification {

	public static Specification<Projeto> filtroSiglaTitulo(String siglaOuTitulo) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.or(
				criteriaBuilder.like(criteriaBuilder.lower(root.get("sigla")), "%" + siglaOuTitulo.toLowerCase() + "%"),
				criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")),
						"%" + siglaOuTitulo.toLowerCase() + "%"));
	}

	public static Specification<Projeto> filtroIdOrganizacao(Long idOrganizacao) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organizacao").get("id"),
				idOrganizacao);
	}

	public static Specification<Projeto> filtroStatus(String status) {
            return (root, query, cb) -> {

                Join<Projeto, StatusProjeto> statusJoin = root.join("historicoStatus");

                // Subquery para pegar o maior inicioEm
                Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
                Root<StatusProjeto> subRoot = subquery.from(StatusProjeto.class);

                subquery.select(cb.greatest(subRoot.<LocalDateTime>get("inicioEm")))
                        .where(cb.equal(subRoot.get("projeto"), root));

                return cb.and(
                    cb.equal(statusJoin.get("inicioEm"), subquery),
                    cb.equal(statusJoin.get("status"), status)
                );
            };
        }

	public static Specification<Projeto> filtroData(String dataPeriodoInicio, String dataPeriodoFim) {
		LocalDateTime inicio = FormatadorData
				.parseSimples(dataPeriodoInicio.isBlank() ? FormatadorData.DATA_MINIMA : dataPeriodoInicio);
		LocalDateTime fim = FormatadorData
				.parseSimples(dataPeriodoFim.isBlank() ? FormatadorData.DATA_MAXIMA : dataPeriodoFim);

		return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("criadoEm"), inicio, fim);
	}

	public static Specification<Projeto> filtroStatusParecerSEP() {
            return (root, query, cb) -> {
                Join<Projeto, StatusProjeto> statusJoin = root.join("historicoStatus");

                return cb.and(
                    cb.isNull(statusJoin.get("fimEm")),
                    cb.equal(statusJoin.get("status"), StatusProjetoEnum.PARECER_SEP.getValue())
                );
            };
        }

}