package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.dto.ProspeccaoOrganizacaoDetalhesDto;
import br.gov.es.siscap.utils.PreencherZerosEsquerda;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

	/*
		- ADICIONAR ResponseEntity NO RETORNO DOS METODOS PERTINENTES
		- VALIDAR STATUS DA PROSPECCAO [prospeccao_status = 'Prospectado' / 'Nao Prospectado'] AO ENVIAR EMAIL
		- ADICIONAR LOGO SISCAP NO CABECALHO DO EMAIL / BRASAO DA SEP NO RODAPE -> VER NECESSIDADE
		- ADICIONAR NOME, ORGAO E MAIS ALGUMA COISA (?) DA PESSOA PROSPECTORA DA PROSPECCAO NO FINAL DO CORPO DO EMAIL -> VER NECESSIDADE
	*/

	private final JavaMailSenderImpl sender;

	public void enviarEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto, List<String> emailsInteressadosList) throws MessagingException {
		MimeMessage mensagem = this.sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

		String assuntoEmail = this.montarAssuntoEmail(prospeccaoDetalhesDto);
		String corpoEmail = this.montarCorpoEmail(prospeccaoDetalhesDto);

		helper.setSubject(assuntoEmail);
		helper.setText(corpoEmail, true);

		for (String email : emailsInteressadosList) {
			helper.setTo(email);
			try {
				this.sender.send(mensagem);
			} catch (MailException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private String montarAssuntoEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto) {
		String idCartaConsultaPreenchido = PreencherZerosEsquerda.preencher(prospeccaoDetalhesDto.cartaConsultaDetalhes().id().toString());
		String nomeObjetoCartaConsulta = prospeccaoDetalhesDto.cartaConsultaDetalhes().objeto().nome();

		return "Carta Consulta " + idCartaConsultaPreenchido + " - " + nomeObjetoCartaConsulta;
	}

	private String montarCorpoEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto) {
		String campoOrganizacaoProspectoraTitulo = this.montarElementoTitulo("De:");
		String campoOrganizacaoProspectoraConteudo = this.montarCampoOrganizacaoDetalhesConteudo(prospeccaoDetalhesDto.organizacaoProspectoraDetalhes());

		String campoOrganizacaoProspectadaTitulo = this.montarElementoTitulo("Para:");
		String campoOrganizacaoProspectadaConteudo = this.montarCampoOrganizacaoDetalhesConteudo(prospeccaoDetalhesDto.organizacaoProspectadaDetalhes());

		String campoInteressadosTitulo = this.montarElementoTitulo("Interessado(a)(s):");
		String campoInteressadosConteudo = this.montarCampoInteressadosConteudo(prospeccaoDetalhesDto.nomesInteressados());

		String campoOperacaoTitulo = this.montarElementoTitulo("Operação:");
		String campoOperacaoConteudo = this.montarCampoOperacaoConteudo(prospeccaoDetalhesDto.tipoOperacao());

		String campoObjetoTitulo = this.montarElementoTitulo("Objeto:");
		String campoObjetoConteudo = this.montarCampoObjetoConteudo(prospeccaoDetalhesDto.cartaConsultaDetalhes().objeto().nome());

		String campoValorEstimadoTitulo = this.montarElementoTitulo("Valor Estimado:");
		String campoValorEstimadoConteudo = this.montarCampoValorEstimadoConteudo(prospeccaoDetalhesDto.cartaConsultaDetalhes().valor().quantia());

		String corpoEmail = "<html>" +
					"<body>" +

					// Organização Prospectora
					"<p>" +
					campoOrganizacaoProspectoraTitulo +
					campoOrganizacaoProspectoraConteudo +
					"</p>" +

					// Organização Prospectada
					"<p>" +
					campoOrganizacaoProspectadaTitulo +
					campoOrganizacaoProspectadaConteudo +
					"</p>" +

					// Interessados
					"<p>" +
					campoInteressadosTitulo +
					campoInteressadosConteudo +
					"</p>" +

					// Operação
					"<p>" +
					campoOperacaoTitulo +
					campoOperacaoConteudo +
					"</p>" +

					// Objeto
					"<p>" +
					campoObjetoTitulo +
					campoObjetoConteudo +
					"</p>" +

					// Valor Estimado
					"<p>" +
					campoValorEstimadoTitulo +
					campoValorEstimadoConteudo +
					"</p>" +

					//Corpo Carta Consulta
					"<div>" +
					prospeccaoDetalhesDto.cartaConsultaDetalhes().corpo() +
					"</div>" +

					"</body>" +
					"</html>";

		return corpoEmail;
	}


	private String montarElementoTitulo(String titulo) {
		return "<strong style='margin-bottom: 8px;'>" + titulo + "</strong><br/>";
	}

	private String montarElementoConteudo(String conteudo) {
		return "<span>" + conteudo + "</span><br/>";
	}

	private String montarCampoOrganizacaoDetalhesConteudo(ProspeccaoOrganizacaoDetalhesDto detalhesOrganizacao) {
		String nomeFantasia = detalhesOrganizacao.nomeFantasia();
		String nome = detalhesOrganizacao.nome();
		String endereco = detalhesOrganizacao.cidade() + ", " + detalhesOrganizacao.estado() + ", " + detalhesOrganizacao.pais();
		String telefone = detalhesOrganizacao.telefone();
		String email = detalhesOrganizacao.email();

		return montarElementoConteudo(nomeFantasia) +
					montarElementoConteudo(nome) +
					montarElementoConteudo(endereco) +
					montarElementoConteudo(telefone) +
					montarElementoConteudo(email);
	}

	private String montarCampoInteressadosConteudo(List<String> nomesInteressados) {
		StringBuilder interessadosSB = new StringBuilder();

		for (String nomeInteressado : nomesInteressados) {
			interessadosSB.append(montarElementoConteudo(nomeInteressado));
		}

		return interessadosSB.toString();
	}

	private String montarCampoOperacaoConteudo(String tipoOperacao) {
		return montarElementoConteudo(tipoOperacao);
	}

	private String montarCampoObjetoConteudo(String nomeObjeto) {
		return montarElementoConteudo(nomeObjeto);
	}

	private String montarCampoValorEstimadoConteudo(BigDecimal valorEstimado) {
		String valorEstimadoFormatado = NumberFormat.getCurrencyInstance().format(valorEstimado);

		return montarElementoConteudo(valorEstimadoFormatado);
	}
}

