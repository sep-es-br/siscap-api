package br.gov.es.siscap.service;

import br.gov.es.siscap.exception.service.SiscapServiceException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Service
public class ArquivosService {

    private final String raizRelatorios;
    private final DataSource dataSource;
    private final Logger logger = LogManager.getLogger(ArquivosService.class);

    public ArquivosService(@Value("${raiz.relatorios}") String raizRelatorios,
                           @Autowired DataSource dataSource) {
        this.raizRelatorios = raizRelatorios;
        this.dataSource = dataSource;
    }

    public Resource gerarArquivo(String nomeArquivo, Integer idProjeto) {
        JasperPrint jasperPrint = preencherArquivo(recuperarArquivo(nomeArquivo), idProjeto);
        return exportarRelatorio(jasperPrint);
    }

    private InputStream recuperarArquivo(String nomeArquivo) {
        try {
            return Files.newInputStream(Path.of(raizRelatorios + "/" + nomeArquivo + ".jasper"));
        } catch (IOException e) {
            logger.info("Erro ao encontrar o arquivo {}.jasper", nomeArquivo);
            throw new SiscapServiceException(List.of("Erro ao encontrar o arquivo " + nomeArquivo + ".jasper"));
        }
    }

    private JasperPrint preencherArquivo(InputStream relatorio, Integer idProjeto) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("idProjeto", idProjeto);
            map.put("pathRelatorios", raizRelatorios);
            return JasperFillManager.fillReport(relatorio, map, dataSource.getConnection());
        } catch (JRException | SQLException e) {
            logger.info("Erro ao preencher o relatório.");
            throw new SiscapServiceException(List.of("Erro ao preencher o relatório. Contate o suporte."));
        }
    }

    private Resource exportarRelatorio(JasperPrint jasperPrint) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
            return new ByteArrayResource(byteArrayOutputStream.toByteArray());
        } catch (JRException e) {
            logger.info("Erro ao exportar o relatório.");
            throw new SiscapServiceException(List.of("Erro ao exportar o relatório. Contate o suporte."));
        }
    }

}
