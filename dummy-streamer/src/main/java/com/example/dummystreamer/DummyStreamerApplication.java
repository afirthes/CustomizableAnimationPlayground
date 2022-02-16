package com.example.dummystreamer;

import org.apache.coyote.Request;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1; // we do not want to generally read the whole stream into memory ...
    }
}

@SpringBootApplication
public class DummyStreamerApplication {

    public static void consumerInputStreamWithoutBuffering(String url, Consumer<InputStream> streamConsumer) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        final ResponseExtractor responseExtractor =
                (ClientHttpResponse clientHttpResponse) -> {
                    streamConsumer.accept(clientHttpResponse.getBody());
                    return null;
                };

        final RequestCallback requestCallback = (ClientHttpRequest request) -> {
            request.getHeaders().add("1","1");
            request.getHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
        };

        restTemplate.execute(url, HttpMethod.GET, null, responseExtractor);
    }

    public static void sendFile() {
        try {
            final InputStream fis = new FileInputStream(new File("/Users/ruachs6/Downloads/movi.mov")); // or whatever

            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            String response;
            HttpStatus httpStatus = HttpStatus.CREATED;

            try {

                map.add("file", new MultipartInputStreamFileResource(fis, "/Users/ruachs6/Downloads/movi.mov"));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                String url = "http://localhost:8090/";

                HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
                response = new RestTemplate().postForObject(url, requestEntity, String.class);



            } catch (HttpStatusCodeException e) {
                httpStatus = HttpStatus.valueOf(e.getStatusCode().value());
                response = e.getResponseBodyAsString();
            } catch (Exception e) {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                response = e.getMessage();
            }


//            final RequestCallback requestCallback = new RequestCallback() {
//                @Override
//                public void doWithRequest(final ClientHttpRequest request) throws IOException {
//                    request.getHeaders().add("Content-type", "application/octet-stream");
//                    request.getHeaders().add("file", "movi2.mov");
//                    request.getHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
//                    IOUtils.copy(fis, request.getBody());
//                }
//            };
//
//            final RestTemplate restTemplate = new RestTemplate();
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            requestFactory.setBufferRequestBody(false);
//            restTemplate.setRequestFactory(requestFactory);
//
//
//            final HttpMessageConverterExtractor<String> responseExtractor =
//                    new HttpMessageConverterExtractor<String>(String.class, restTemplate.getMessageConverters());
//            restTemplate.execute("http://localhost:8090/", HttpMethod.POST, requestCallback, responseExtractor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    static void testUpload()  {
        // File upload service upload interface
        String url = "http://localhost:8090/";
        // Files to be uploaded (with client local disk)
        String filePath = "/Users/ruachs6/Downloads/movi.mov";

        // Encapsulate request parameters
        FileSystemResource resource = new FileSystemResource(new File(filePath));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("uploadFile", resource);  //Server MultipartFile uploadFile
        //param.add("param1", "test"); / / if the server accepts additional parameters, it can pass


        // Send the request and output the result
        System.out.println("--- Start uploading files ---");
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String result = restTemplate.postForObject(url, param, String.class);
        System.out.println("--- Visit address:" + result);
    }

    static void testUpload2() {
        final InputStream fis; // or whatever
        try {
            fis = new FileInputStream(new File("/Users/ruachs6/Downloads/movi.mov"));

            final RequestCallback requestCallback = new RequestCallback() {
                @Override
                public void doWithRequest(final ClientHttpRequest request) throws IOException {
                    request.getHeaders().add("Content-type", "application/octet-stream");
                    IOUtils.copy(fis, request.getBody());
                }
            };

            final RestTemplate restTemplate = new RestTemplate();
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setBufferRequestBody(false);
            restTemplate.setRequestFactory(requestFactory);

            final HttpMessageConverterExtractor<String> responseExtractor =
                    new HttpMessageConverterExtractor<String>(String.class, restTemplate.getMessageConverters());

            restTemplate.execute("http://localhost:8090/", HttpMethod.POST, requestCallback, responseExtractor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadBlob() {
        final InputStream fis; // or whatever
        try {
            fis = new FileInputStream(new File("/Users/ruachs6/Downloads/movi.mov"));


        final RestTemplate restTemplate = new RestTemplate();
        if (fis == null) {
            throw new IllegalArgumentException("blobStream can't be null");
        }

        String blobApiUrl = "http://localhost:8090/mock";

        final RequestCallback requestCallback = request -> {
            HttpHeaders headers = request.getHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            //headers.setContentLength(fis.available());

            IOUtils.copy(fis, request.getBody());
        };

        final ResponseExtractor<String> responseExtractor =
                response -> {
            InputStream is = response.getBody();
            StringBuilder sb = new StringBuilder();
            Scanner s = new Scanner(is);
            while(s.hasNextLine()) {
                sb.append(s.nextLine());
            }
            return sb.toString();
        };

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        restTemplate.setRequestFactory(requestFactory);

         restTemplate.execute(blobApiUrl, HttpMethod.POST, requestCallback, responseExtractor);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

//        Consumer<InputStream> doWhileDownloading = inputStream -> {
//            try {
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        consumerInputStreamWithoutBuffering("http://localhost:8090/download", doWhileDownloading);



        //sendFile();

        uploadBlob();
    }

}
