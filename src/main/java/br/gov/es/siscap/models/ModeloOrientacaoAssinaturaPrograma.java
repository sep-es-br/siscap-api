package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "modelo_orientacao_assinatura_programa")
@NoArgsConstructor
@Getter
@Setter
public class ModeloOrientacaoAssinaturaPrograma extends ControleHistorico {

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modelo_orientacao_assinatura_programa_id_seq")
	@SequenceGenerator(name = "modelo_orientacao_assinatura_programa_id_seq", sequenceName = "modelo_orientacao_assinatura_programa_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

    @Column(name = "guid_lotacao", nullable = false, length = 50)
    private String guidLotacao;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "versao", nullable = false)
    private Integer versao;

    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

}
