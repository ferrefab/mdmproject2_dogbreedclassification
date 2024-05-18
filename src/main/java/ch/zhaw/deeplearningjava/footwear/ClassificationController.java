package ch.zhaw.deeplearningjava.footwear;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.springframework.core.io.Resource;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.InputStream;

@RestController
public class ClassificationController {

    private Inference inference = new Inference();

    @GetMapping("/ping")
    public String ping() {
        return "Classification app is up and running!";
    }

    @PostMapping(path = "/analyze")
    public String predict(@RequestParam("image") MultipartFile image) throws Exception {
        InputStream is = new ByteArrayInputStream(image.getBytes());
        var uri = "http://localhost:8080/predictions/resnet18_v1";
        if (this.isDockerized()) {
            uri = "http://model-service:8080/predictions/resnet18_v1";
        }
        var webClient = WebClient.create();
        Resource resource = new InputStreamResource(is);
        var result = webClient.post()
                              .uri(uri)
                              .contentType(MediaType.MULTIPART_FORM_DATA)
                              .body(BodyInserters.fromResource(resource))
                              .retrieve()
                              .bodyToMono(String.class)
                              .block();
        return result;
    }
    
    private boolean isDockerized() {
        File f = new File("/.dockerenv");
        return f.exists();
    }
    

}