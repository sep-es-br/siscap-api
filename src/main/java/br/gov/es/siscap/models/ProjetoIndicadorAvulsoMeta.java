package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoMetaDto;

@Entity
@Table(name = "projeto_indicador_avulso_meta")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE projeto_indicador_avulso_meta SET apagado = true WHERE id=?")
@SQLRestriction("apagado = false")
public class ProjetoIndicadorAvulsoMeta extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projeto_indicador_avulso", nullable = false)
    private ProjetoIndicadorAvulso projetoIndicadorAvulso;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "valor")
    private String valor;

    public ProjetoIndicadorAvulsoMeta(
            ProjetoIndicadorAvulso projetoIndicadorAvulso,
            ProjetoIndicadorAvulsoMetaDto dto) {
        this.id = dto.id();
        this.projetoIndicadorAvulso = projetoIndicadorAvulso;
        this.ano = dto.anoMeta();
        this.valor = dto.valorMeta();
    }

}
