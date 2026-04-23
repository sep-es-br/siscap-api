package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.enums.TipoStatusEnum;
import br.gov.es.siscap.form.ProjetoForm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

@Entity
@Table(name = "projeto")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Projeto extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private long id;

	@Column(name = "sigla", length = 12)
	private String sigla;

	@Column(name = "titulo", nullable = false, length = 150)
	private String titulo;

	@Column(name = "objetivo", nullable = false, length = 2000)
	private String objetivo;

	@Column(name = "objetivo_especifico", nullable = false, length = 2000)
	private String objetivoEspecifico;

	@ManyToOne
	@SQLJoinTableRestriction("apagado = FALSE")
	@JoinColumn(name = "id_tipo_status", nullable = false)
	private TipoStatus tipoStatus;

	@Column(name = "fase", nullable = false)
	private String fase;

	@Column(name = "rascunho", nullable = false)
	private boolean rascunho;

	@ManyToOne
	@JoinColumn(name = "id_organizacao", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private Organizacao organizacao;

	@Column(name = "situacao_problema", nullable = false, length = 2000)
	private String situacaoProblema;

	@Column(name = "solucoes_propostas", nullable = false, length = 2000)
	private String solucoesPropostas;

	@Column(name = "impactos", nullable = false, length = 2000)
	private String impactos;

	@Column(name = "arranjos_institucionais", nullable = false, length = 2000)
	private String arranjosInstitucionais;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoPessoa> projetoPessoaSet;

	@OneToMany(mappedBy = "projeto")
	private Set<LocalidadeQuantia> localidadeQuantiaSet;

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
        @SQLRestriction("""
                            apagado_em IS NULL AND
                            NOT EXISTS (
                                SELECT 1
                                FROM programa_status ps
                                WHERE ps.id_programa = id_programa
                                  AND ps.status = 5
                                  AND ps.inicio_em = (
                                      SELECT MAX(ps2.inicio_em)
                                      FROM programa_status ps2
                                      WHERE ps2.id_programa = ps.id_programa
                                  )
                            )
                        """)
        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
	private Set<ProjetoPrograma> programaHistorico;

	@ManyToOne
	@JoinColumn(name = "id_area")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Area area;

	@DateTimeFormat
	@Column(name = "data_registro")
	private LocalDateTime dataRegistro;

	@Column(name = "count_ano", nullable = false)
	private String countAno;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoIndicador> projetoIndicadorSet;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoAcao> projetoAcoesSet;

	@Column(name = "pecas_planejamento", nullable = false, length = 2000)
	private String pecasPlanejamento;

	@Column(name = "protocolo_edocs", nullable = false, length = 15)
	private String protocoloEdocs;

	@ManyToOne
	@JoinColumn(name = "id_tipo_motivo_arquivamento")
	@SQLJoinTableRestriction("apagado = FALSE")
	private TipoMotivoArquivamento tipoMotivoArquivamento;

	@Column(name = "justificativa_arquivamento", nullable = false, length = 255)
	private String justificativaArquivamento;

	@Column(name = "id_documento_edocs", nullable = false, length = 50)
	private String idDocumentoCapturadoEdocs;

	@Column(name = "id_processo_edocs", nullable = false, length = 50)
	private String idProcessoEdocs;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoCamposComplementacao> projetoComplementoSet;

	@Column(name = "justificativa_exclusao_logica", nullable = true, length = 500)
	private String justificativaExclusaoLogica;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoParecer> projetoParecerSet;

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
	@Setter(AccessLevel.NONE)
	private Set<StatusProjeto> historicoStatus;

	@ManyToOne()
	@JoinColumn(name = "id_pessoa_redator")
	private Pessoa pessoa;

	public Projeto(Long id) {
		this.setId(id);
	}

	public Projeto(ProjetoForm form) {
		this.setDadosProjeto(form);
	}

	public void atualizarProjeto(ProjetoForm form) {
		this.setDadosProjeto(form);
		super.atualizarHistorico();
	}

	public void apagarProjeto() {
		this.setSigla(null);
		this.setPrograma(null);
		super.apagarHistorico();
	}

	public Long getIdEixo() {
		return this.area.getEixo() != null ? this.area.getEixo().getId() : null;
	}

	public Long getIdPlano() {
		if (getIdEixo() == null)
			return null;
		return this.area.getEixo().getPlano() != null ? this.area.getEixo().getPlano().getId() : null;
	}

	public boolean isAtivo() {
		return Objects.equals(this.getTipoStatus().getId(), TipoStatusEnum.ATIVO.getValue());
	}

	public boolean isStatusElegivel() {
            if(this.getStatusAtual() == null) return false;
            return Objects.equals( this.getStatusAtual().getStatus(), StatusProjetoEnum.ELEGIVEL.getValue());
	}

	public boolean isElegivelParaVinculo() {

		if (!this.isStatusElegivel()) {
			return false;
		}

		if (this.getPrograma() == null) {
			return true;
		}

		return this.getPrograma().isRecusado()
				|| this.getPrograma().isEmEdicao();

	}

	private void setDadosProjeto(ProjetoForm form) {
		this.setSigla(form.sigla());
		this.setTitulo(form.titulo());
		this.setObjetivo(form.objetivo());
		this.setObjetivoEspecifico(form.objetivoEspecifico());
		this.setTipoStatus(new TipoStatus(TipoStatusEnum.ATIVO.getValue()));
		this.setOrganizacao(new Organizacao(form.idOrganizacao()));
		this.setSituacaoProblema(form.situacaoProblema());
		this.setSolucoesPropostas(form.solucoesPropostas());
		this.setImpactos(form.impactos());
		this.setArranjosInstitucionais(form.arranjosInstitucionais());
		this.setFase("DIC");
		this.setPecasPlanejamento(form.pecasPlanejamento());
		this.setProtocoloEdocs(form.protocoloEdocs());
	}

	public void alterarStatus(String novoStatus, Pessoa pessoa) {
            
                if(this.getStatusAtual() != null && this.getStatusAtual().getStatus().equals(novoStatus))
                    return;
            
		this.finalizarStatusAtual(pessoa);

		// Cria novo status
		StatusProjeto novoStatusProjeto = StatusProjeto.init(this, novoStatus);

		// Inicializa coleção se estiver nula
		if (this.getHistoricoStatus() == null) {
			this.historicoStatus = new HashSet<>();
		}

		this.getHistoricoStatus().add(novoStatusProjeto);

        }
        
        public StatusProjeto finalizarStatusAtual(Pessoa pessoa) {
            if(this.getStatusAtual() == null) return null;
            
            return this.getStatusAtual().finalizar(pessoa);
        }
        
        public StatusProjeto getStatusAtual() {
            if(historicoStatus == null) return null;
            return historicoStatus.stream()
                    .sorted(Comparator.comparing(StatusProjeto::getInicioEm).reversed())
                    .findFirst().orElse(null);
        }
        
        public Programa getPrograma() {
            return Optional.ofNullable(this.getHistoricoAtivo()).map(ProjetoPrograma::getPrograma).orElse(null);
        }
        
        public void removerPrograma() {
            ProjetoPrograma historicoAtivo = this.getHistoricoAtivo();
            if(historicoAtivo == null) return;
            
            historicoAtivo.setApagadoEm(LocalDateTime.now());
            
        }
        
        public void setPrograma(Programa programa) {
            
            Assert.isNull(this.getHistoricoAtivo(), "Favor remover o programa antes de incluir outro");
            
            ProjetoPrograma novo = new ProjetoPrograma(this, programa);
            if(this.programaHistorico == null){
                this.programaHistorico = new HashSet<>();
            } 
                
            this.programaHistorico.add(novo);
        }
        
        private ProjetoPrograma getHistoricoAtivo() {
            return programaHistorico.stream()
                .filter(pp -> pp.getApagadoEm() == null)
                .filter(pp -> !pp.getPrograma().isRecusado()) // ou equivalente
                .findFirst()
                .orElse(null);
        }

}