package br.gov.es.siscap.service;

import br.gov.es.siscap.exception.service.ImagemSisCapException;
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
            if (imagem.isEmpty())
                throw new ImagemSisCapException("O arquivo de imagemPerfil está vazio");

            String destino = UUID.randomUUID() + "." +
                    Objects.requireNonNull(imagem.getResource().getFilename()).split("\\.")[1];

            try (InputStream inputStream = imagem.getInputStream()) {
                Files.copy(inputStream, diretorioPadrao.resolve(destino), StandardCopyOption.REPLACE_EXISTING);
            }

            return destino;
        } catch (IOException e) {
            throw new ImagemSisCapException(e.getMessage());
        }
    }

    private Path load(String filename) {
        return diretorioPadrao.resolve(filename).normalize();
    }

    public Resource  buscar(String nomeImagem) {
        try {
            Path caminhoImagem = load(nomeImagem);
            Resource resource = new UrlResource(caminhoImagem.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else
                throw new ImagemSisCapException("Não foi possível ler o arquivo " + nomeImagem);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            throw new ImagemSisCapException("Não foi possível ler o arquivo " + nomeImagem);
        }
    }

    public void apagar(String nomeImagem) {
        try {
            Path caminhoImagem = load(nomeImagem);
            Files.delete(caminhoImagem);
        } catch (IOException e) {
            logger.info("Imagem não excluída pois não foi encontrado arquivo com a referência {}", nomeImagem);
        }
    }
}
