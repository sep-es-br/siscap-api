package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProgramaOrganizacaoDto;
import br.gov.es.siscap.enums.TipoStatusEnum;

@Entity
@Table(name = "programa_organizacao")
@NoArgsConstructor
@IdClass(ProgramaOrganizacaoId.class)
@Getter
@Setter
@SQLDelete(sql = """
UPDATE programa_organizacao 
SET apagado = true 
WHERE id_programa = ? AND id_organizacao = ?
""")
public class ProgramaOrganizacao  {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_programa")
    private Programa programa;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_organizacao")
    private Organizacao organizacao;

    @Column(name = "tipo_organizacao", nullable = false)
    private Integer tipoOrganizacao;

    public ProgramaOrganizacao(Programa programa, Organizacao organizacao, ProgramaOrganizacaoDto dto) {
        this.programa = programa;
        this.organizacao = organizacao;
        this.tipoOrganizacao = dto.papel();
    }

    public ProgramaOrganizacao(Programa programa, Organizacao organizacao, Integer tipoOrganizacao) {
        this.programa = programa;
        this.organizacao = organizacao;
        this.tipoOrganizacao = tipoOrganizacao;
    }

    public boolean compararIdOrgaoComOrgaoProgramaDto(ProgramaOrganizacaoDto programaOrganizacaoDto) {
		return Objects.equals( this.getOrganizacao().getId(), programaOrganizacaoDto.id());
	}

    public void atualizarOrgaoPrograma(ProgramaOrganizacaoDto programaOrganizacaoDto) {
        this.setTipoOrganizacao(programaOrganizacaoDto.papel());
	}

}
