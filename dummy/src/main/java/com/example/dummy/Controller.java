package com.example.dummy;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class Controller {

    Integer i = 0;

    @GetMapping("/ocr")
    ResponseEntity<String> result() {
        if ( i % 4 != 0) {
            i ++;
            return ResponseEntity.status(404).body("Not ready...");
        }
        i ++;
        return ResponseEntity.ok("OK");
    }


    @GetMapping(path = "/ocr-result", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<ServerSentEvent<String>> streamFlux(
            @RequestHeader("applicationId") String applicationId,
            @RequestHeader("borrowerId") String borrowerId,
            @RequestHeader("documentIds") String documentIds
            ) {
        // http://localhost:8086/ocr-result

        return Flux.interval(Duration.ofSeconds(5)).take(4)
                .map(sequence -> ServerSentEvent.<String> builder()
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("{\"result\":\"POOR\"}")
                        .build());
    }

    @GetMapping("/digits")
    public SseEmitter streamSseMvc() {
        SseEmitter emitter = new SseEmitter();
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            try {
                for (int i = 0; true; i++) {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .data("SSE MVC - " + LocalTime.now().toString())
                            .id(String.valueOf(i))
                            .name("message");
                    //emitter.send("message "+i);
                    emitter.send(event);
                    Thread.sleep(1000);
                }
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    public static void main(String[] args) {

        List.of(1,12,3)
    }
}
