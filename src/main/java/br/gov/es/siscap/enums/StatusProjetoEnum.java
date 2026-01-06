package br.gov.es.siscap.enums;

import java.util.ArrayList;
import java.util.List;

import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Projeto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusProjetoEnum {

	EM_ELABORACAO("Em Elaboração") {
        @Override
        public void validar(Projeto projeto) {
            exigirNulo(projeto.getProtocoloEdocs(), "protocolo E-Docs");
        }
    },

	// EM_ELABORACAO("Em Elaboração"),
	EM_ANALISE("Em Análise") {
		@Override
		public void validar(Projeto projeto) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validar'");
		}
	},
	ARQUIVADO("Arquivado") {
		@Override
		public void validar(Projeto projeto) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validar'");
		}
	},
	PARECER_SEP("Parecer SEP") {
		@Override
		public void validar(Projeto projeto) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validar'");
		}
	},
	PARECER_ESTRATEGICO_ORCAMENTARIO("Aguardando Parecer") {
		@Override
		public void validar(Projeto projeto) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validar'");
		}
	},
	COMPLEMETACAO("Em Complementação") {
		@Override
		public void validar(Projeto projeto) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validar'");
		}
	},
	ENCERRADO("Encerrado") {
		@Override
		public void validar(Projeto projeto) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validar'");
		}
	},
	ELEGIBILIDADE("Elegibilidade") {
		@Override
		public void validar(Projeto projeto) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validar'");
		}
	};

	private final String value;

	public abstract void validar(Projeto projeto);

	protected void exigirNulo(Object valor, String campo) {
		List<String> erros = new ArrayList<>();
        if (valor != null) {
			erros.add("Campo '" + campo + "' não deve estar preenchido no status " + this);
            throw new ValidacaoSiscapException(erros);
        }
    }
	
}