package br.gov.es.siscap.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import br.gov.es.siscap.dto.edocswebapi.UploadS3BodyDto;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

@Service
public class UploadS3Service {

    private final Logger logger = LogManager.getLogger(UploadS3Service.class);

    public boolean enviarArquivoParaS3OkHttp( String url, UploadS3BodyDto body, Resource arquivo, String nomeArquivo, String tokenAcesso ) {

        OkHttpClient client = new OkHttpClient.Builder().build();

        String absolutePathArquivoParaS3 = "";
        try {
            
            absolutePathArquivoParaS3 = resourceToTempFile( arquivo, nomeArquivo );
            
            File arquivotestFile = new File(absolutePathArquivoParaS3);
            
            logger.info("Arquivo existe {} - tamanho do arquivo {}.", arquivotestFile.exists(), arquivotestFile.length());

            if (!arquivotestFile.exists() || arquivotestFile.length() == 0) {
                throw new IllegalStateException("Arquivo inexistente ou vazio: " + absolutePathArquivoParaS3 );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
      
        RequestBody bodyOkHttp = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("key", body.key()  )
        .addFormDataPart("bucket", body.bucket() )
        .addFormDataPart("x-amz-algorithm", body.xAmzAlgorithm() )
        .addFormDataPart("x-amz-credential", body.xAmzCredential() )
        .addFormDataPart("x-amz-date", body.xAmzDate() )
        .addFormDataPart("policy", body.policy() )
        .addFormDataPart("x-amz-signature", body.xAmzSignature() )
        .addFormDataPart("file", absolutePathArquivoParaS3 ,
            RequestBody.create( new File( absolutePathArquivoParaS3 ), 
            MediaType.parse("application/pdf") ))
        .build();

        Request request = new Request.Builder()
            .url(url)
            .method("POST", bodyOkHttp)
            .build();
        
        try {
            
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
            } else {
                throw new RuntimeException("Erro ao executar o upload do arquivo para o servidor S3 do E-Docs.");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
			throw new RuntimeException("Erro ao construir o request para fazer o upload do arquivo ao S3 do E-Docs.");
        }

        return true;

    }
    
    private String resourceToTempFile(Resource resource, String filename) throws IOException {
        filename = filename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        Path tempDir = Files.createTempDirectory("upload-dir-");
        Path filePath = tempDir.resolve(filename + ".pdf");
        File tempFile = filePath.toFile();
        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile.getAbsolutePath();
    }
       
}
