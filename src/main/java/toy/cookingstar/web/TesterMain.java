package toy.cookingstar.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import toy.cookingstar.service.post.StatusType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TesterMain {

    public static void main(String[] args) throws InterruptedException {
        String url = UriComponentsBuilder.fromUriString("http://localhost:8080/post")
                .queryParam("userId", "elegantstar")
                .queryParam("postId", 127)
                .queryParam("size", 100)
                .queryParam("status", StatusType.POSTING)
                .build().toString();
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService es = Executors.newFixedThreadPool(1000);
        StopWatch totalStopWatch = new StopWatch();

        totalStopWatch.start();
        for (int i = 0; i < 1000; i++) {
            es.submit(() -> {
                restTemplate.getForObject(url, String.class);
            });
        }

        es.shutdown();
        es.awaitTermination(50, TimeUnit.SECONDS);
        totalStopWatch.stop();
        log.info("[time elapsed] : {} ms", totalStopWatch.getTotalTimeMillis());

    }
}
