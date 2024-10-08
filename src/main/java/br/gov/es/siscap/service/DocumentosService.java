package br.gov.es.siscap.service;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class DocumentosService {

	private final Path diretorioDocumentos;
	private final Logger logger;

	private static final String formatacaoData = "ddMMuuuuHHmmss";
	private static final String extensaoArquivo = ".txt";

	private Path diretorioCartaConsulta;

	public DocumentosService(@Value("${raiz.documentos}") String caminhoRaiz) {
		this.diretorioDocumentos = Paths.get(caminhoRaiz);
		this.logger = LogManager.getLogger(DocumentosService.class);
	}

	@PostConstruct
	private void setDiretorioCartaConsulta() {
		this.diretorioCartaConsulta = diretorioDocumentos.resolve("carta-consulta");
	}

	public String buscarCartaConsultaCorpo(String nomeDocumento) {
		Path caminhoDocumento = diretorioCartaConsulta.resolve(nomeDocumento);

		try {
			return Files.readString(caminhoDocumento);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Erro ao buscar corpo da carta de consulta");
		}
	}

	public String cadastrarCartaConsultaCorpo(String corpo) {
		String nomeDocumento = gerarNomeDocumentoCartaConsulta();
		Path caminhoDocumento = diretorioCartaConsulta.resolve(nomeDocumento);

		try {
			Files.write(caminhoDocumento, corpo.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Erro ao salvar corpo da carta de consulta");
		}

		return nomeDocumento;
	}

	public void atualizarCartaConsultaCorpo(String nomeDocumento, String corpo) {
		Path caminhoDocumento = diretorioCartaConsulta.resolve(nomeDocumento);

		try {
			Files.write(caminhoDocumento, corpo.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Erro ao atualizar corpo da carta de consulta");
		}
	}

	public void excluirCartaConsultaCorpo(String nomeDocumento) {
		Path caminhoDocumento = diretorioCartaConsulta.resolve(nomeDocumento);

		try {
			Files.deleteIfExists(caminhoDocumento);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Erro ao excluir corpo da carta de consulta");
		}
	}

	private String gerarNomeDocumentoCartaConsulta() {
		String prefixo = "CC";
		String dataFormatada = LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatacaoData));
		String porcaoAleatoria = UUID.randomUUID().toString().substring(0, 5);
		return prefixo + dataFormatada + porcaoAleatoria + extensaoArquivo;
	}
}
