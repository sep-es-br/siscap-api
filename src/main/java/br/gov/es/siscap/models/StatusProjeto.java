/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.siscap.models;

import jakarta.persistence.Column;
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
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author gean.carneiro
 */
@Entity
@Table(name = "status_projeto")
@NoArgsConstructor
@Getter
@Setter

public class StatusProjeto {
    
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
        @JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@DateTimeFormat
        @Column(name = "inicio_em", nullable = false)
	private LocalDateTime inicioEm;

	@DateTimeFormat
        @Column(name = "fim_em", nullable = true)
	private LocalDateTime fimEm;
        
        @ManyToOne
        @JoinColumn(name = "id_pessoa", nullable = true)
        private Pessoa pessoa;
        
        @Column(name = "status", nullable = false)
        private String status;
    
}
