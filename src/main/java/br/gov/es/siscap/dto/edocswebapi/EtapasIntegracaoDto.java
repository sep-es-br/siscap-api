package br.gov.es.siscap.dto.edocswebapi;

import br.gov.es.siscap.enums.edocs.EtapasIntegracaoEdocsEnum;
import lombok.ToString;

@ToString
public class EtapasIntegracaoDto {

    private Long idProjeto;
    private EtapasIntegracaoEdocsEnum etapa;
    private boolean iniciada;
    private boolean finalizada;
    private boolean erro;
    
    public EtapasIntegracaoDto(Long idProjeto, EtapasIntegracaoEdocsEnum etapa, boolean iniciada, boolean finalizada, boolean erro ) {
        this.idProjeto = idProjeto;
        this.etapa = etapa;
        this.iniciada = iniciada;
        this.finalizada = finalizada;
        this.erro = erro;
    }

    public EtapasIntegracaoEdocsEnum getEtapa() { return etapa; }
    public boolean isIniciada() { return iniciada; }
    public boolean isFinalizada() { return finalizada; }
    public boolean isErro() { return erro; }
    public Long getIdProjeto() { return idProjeto; }
    public void setIniciou( boolean iniciou ) { this.iniciada = iniciou; }
    public void setFinalizou( boolean finalizou ) { this.finalizada = finalizou; }
    public void setErro(boolean erro) { this.erro = erro; }

}
