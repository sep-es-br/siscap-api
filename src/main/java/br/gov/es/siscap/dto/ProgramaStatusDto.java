/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.ProgramaStatus;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author gean.carneiro
 */
public record ProgramaStatusDto(
            Long id,
            Integer status,
            Long idPessoa,
            String nomePessoa,
            LocalDateTime inicioEm
        ) {
    
    
    public static ProgramaStatusDto fromModel(ProgramaStatus model) {
        if(model == null) return null;
        
        return new ProgramaStatusDto(
                model.getId(), 
                model.getStatus().getValue(), 
                Optional.ofNullable(model.getPessoa()).map(Pessoa::getId).orElse(null), 
                Optional.ofNullable(model.getPessoa()).map(Pessoa::getNome).orElse(null), 
                model.getInicioEm()
        );
    }

}
