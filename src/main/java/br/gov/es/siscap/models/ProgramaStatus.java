/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.StatusProgramaEnum;
import br.gov.es.siscap.models.converter.StatusProgramaEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author gean.carneiro
 */
@Entity
@Table(name = "programa_status")
@NoArgsConstructor
@Getter

public class ProgramaStatus {
    
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
        @JoinColumn(name = "id_programa", nullable = false)
	private Programa programa;
        
        @ManyToOne
        @JoinColumn(name = "id_pessoa", nullable = true)
        private Pessoa pessoa;
        
        @Column(name = "status", nullable = false)
        @Convert(converter = StatusProgramaEnumConverter.class)
        private StatusProgramaEnum status;

	@DateTimeFormat
        @Column(name = "inicio_em", nullable = false)
	private LocalDateTime inicioEm;
        
	@DateTimeFormat
        @Column(name = "fim_em", nullable = true)
	private LocalDateTime fimEm;
        
        
        public void finalizarStatus(Pessoa pessoa) {
           this.fimEm = LocalDateTime.now();
           this.pessoa = pessoa;
        }

                
        public static ProgramaStatus init(Programa programa, StatusProgramaEnum status) {
            ProgramaStatus novoStatus = new ProgramaStatus();
            novoStatus.programa = programa;
            novoStatus.status = status;
            novoStatus.inicioEm = LocalDateTime.now();
            return novoStatus;
        }
    
}
