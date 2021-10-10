package net.kamradtfamily;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface StringService {
    static WebClient wc = WebClient.builder()
            .baseUrl("https://foaas.com/")
            .build();
    static String returnsAString() {
        return wc.get()
                .exchange()
                .flatMap(cr -> cr.bodyToMono(String.class))
                .subscribeOn(Schedulers.single())
                .block();
    }
    static Mono<String> returnsAMono() {
        return wc.get()
                .exchange()
                .flatMap(cr -> cr.bodyToMono(String.class));
    }
    static Mono<String> returnsAMonoBad() throws InterruptedException {
        List<String> list = new ArrayList<>();
        wc.get()
                .exchange()
                .flatMap(cr -> cr.bodyToMono(String.class))
                .subscribeOn(Schedulers.elastic())
                .subscribe(s -> list.add(s));
        Thread.sleep(1000);
        return Mono.just(list.get(0));
    }

    static Flux<String> returnStringsBad() {
        return Flux.fromArray(new String [] {"1", "a", "3", "4"})
                .map(s -> Integer.parseInt(s))
                .map(i -> Integer.toString(i));
    }

    static Flux<String> returnStringsStillBad() {
        return Flux.fromArray(new String [] {"1", "a", "3", "4"})
                .map(s -> Integer.parseInt(s))
                .onErrorReturn(0)
                .map(i -> Integer.toString(i));
    }
    static Flux<String> returnStringsGood() {
        return Flux.fromArray(new String [] {"1", "a", "3", "4"})
                .map(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch(Exception e) {
                        return 0;
                    }
                })
                .onErrorReturn(0)
                .map(i -> Integer.toString(i));
    }

    static Flux<Integer> messages() {
        AtomicInteger i = new AtomicInteger(0);
        return Flux.fromStream(Stream.generate(() -> i.incrementAndGet()));
    }

    static void receive() {
        messages()
                .map(i -> 5/(i-5))
                .subscribe(i -> System.out.println(i));

    }
    static void receiveGood() {
        messages()
                .map(i -> {
                    try {
                        return process(i);
                    } catch (Throwable t) {
                        System.out.println("error encountered");
                        return 0;
                    }
                })
                .subscribe(i -> System.out.println(i));

    }

    static int process(int i) throws InterruptedException { // do error-prone work here:
        Thread.sleep(1000);
        return 5/(i-5);
    }

    static void wokeReceive() {
        messages()
                .map(i -> {
                    try {
                        return wokeProcess(i);
                    } catch (Throwable t) {
                        System.out.println("error encountered");
                        return 0;
                    }
                })
                .delayElements(Duration.ofSeconds(1))
                .subscribe(i -> System.out.println(i));

    }
    static int wokeProcess(int i) throws InterruptedException { // do error-prone work here:
        return 5/(i-5);
    }

    static WebClient wcSlowApi = WebClient.builder()
            .baseUrl("https://www.google.com/search?q=test")
            .build();

    static Flux<String> returnFlood() {
        return Flux.fromStream(IntStream.range(0,100).mapToObj(i -> Integer.toString(i)))
                .flatMap(ignore -> wcSlowApi.get()
                        .exchange()
                        .flatMap(cr -> cr.bodyToMono(String.class)));

    }
}
