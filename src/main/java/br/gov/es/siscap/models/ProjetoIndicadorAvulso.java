package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "projeto_indicador_avulso")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE projeto_indicador_avulso SET apagado = true WHERE id=?")
@SQLRestriction("apagado = false")
public class ProjetoIndicadorAvulso extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_indicador_avulso", nullable = false)
    private IndicadorAvulso indicadorAvulso;

}
