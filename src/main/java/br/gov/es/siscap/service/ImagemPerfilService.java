package br.gov.es.siscap.service;

import br.gov.es.siscap.exception.service.SiscapImagemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImagemPerfilService {

	private final Path diretorioPadrao;
	private final Logger logger = LogManager.getLogger(ImagemPerfilService.class);

	public ImagemPerfilService(@Value("${raiz.imagens}") String caminhoRaiz) {
		this.diretorioPadrao = Paths.get(caminhoRaiz);
	}


	public String salvar(MultipartFile imagem) {
		try {
			if (imagem == null)
				return null;
			if (imagem.isEmpty())
				throw new SiscapImagemException("O arquivo de imagemPerfil está vazio");

			logger.info("Salvar nova imagem {}", imagem.getOriginalFilename());

			String destino = UUID.randomUUID() + "." +
						Objects.requireNonNull(imagem.getResource().getFilename()).split("\\.")[1];

			try (InputStream inputStream = imagem.getInputStream()) {
				Files.copy(inputStream, diretorioPadrao.resolve(destino), StandardCopyOption.REPLACE_EXISTING);
			}

			logger.info("Imagem {} salva com sucesso.", destino);

			return destino;
		} catch (IOException e) {
			logger.error("Erro ao salvar imagem. {}", e.getMessage());
			throw new SiscapImagemException(e.getMessage());
		}
	}

	public Resource buscar(String nomeImagem) {
		try {
			if (nomeImagem == null)
				return null;
			logger.info("Buscar imagem {}.", nomeImagem);
			Path caminhoImagem = load(nomeImagem);
			Resource resource = new UrlResource(caminhoImagem.toUri());
			if (resource.exists() || resource.isReadable()) {
				logger.info("Imagem {} encontrada.", nomeImagem);
			} else {
				logger.error("Imagem {} não encontrada no diretório de imagens.", nomeImagem);
			}

			return resource;
		} catch (MalformedURLException e) {
			logger.error("Não foi possível ler o arquivo {}. {}", nomeImagem, e.getMessage());
			throw new SiscapImagemException("Não foi possível ler o arquivo " + nomeImagem);
		}
	}

	public void apagar(String nomeImagem) {
		try {
			if (nomeImagem == null)
				return;
			logger.info("Remover imagem {}.", nomeImagem);
			Path caminhoImagem = load(nomeImagem);
			Files.delete(caminhoImagem);
			logger.info("Imagem removida com sucesso!");
		} catch (IOException e) {
			logger.info("Imagem não excluída pois não foi encontrado arquivo com a referência {}", nomeImagem);
		}
	}

	public String atualizar(String imagemAntiga, MultipartFile imagemNova) {
		if (imagemAntiga != null)
			apagar(imagemAntiga);
		return salvar(imagemNova);
	}

	private Path load(String filename) {
		return diretorioPadrao.resolve(filename).normalize();
	}

}
