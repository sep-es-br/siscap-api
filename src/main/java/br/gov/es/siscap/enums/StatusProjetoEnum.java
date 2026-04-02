package br.gov.es.siscap.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Projeto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusProjetoEnum {

	EM_ELABORACAO("Elaboração") {
        @Override
        public void validar(Projeto projeto) {
            exigirNulo(projeto.getProtocoloEdocs(), "protocolo E-Docs");
        }
    },

	EM_ANALISE("Análise") {
		@Override
		public void validar(Projeto projeto) {
			 // TODO implementar regra de negócio
		}
	},
	ARQUIVADO("Arquivado") {
		@Override
		public void validar(Projeto projeto) {
			// TODO implementar regra de negócio
		}
	},
	PARECER_SEP("Parecer SEP") {
		@Override
		public void validar(Projeto projeto) {
			// TODO implementar regra de negócio
		}
	},
	COMPLEMETACAO("Complementação") {
		@Override
		public void validar(Projeto projeto) {
			// TODO implementar regra de negócio
		}
	},
	ENCERRADO("Encerrado") {
		@Override
		public void validar(Projeto projeto) {
			// TODO implementar regra de negócio
		}
	},
	ELEGIVEL("Elegível") {
		@Override
		public void validar(Projeto projeto) {
			// TODO implementar regra de negócio
		}
	};

	private final String value;

	public abstract void validar(Projeto projeto);

	protected void exigirNulo(Object valor, String campo) {
		
		List<String> erros = new ArrayList<>();
        
		if (valor == null) {
			return; 
		}
	
		if (valor instanceof String str && str.isBlank()) {
			return; 
		}
	
		erros.add("Campo '" + campo + "' não deve estar preenchido no status " + this);

		throw new ValidacaoSiscapException(erros);

    }

	public static StatusProjetoEnum fromDescricao(String descricao) {
        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(descricao))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("Status inválido: " + descricao)
            );
    }
	
}