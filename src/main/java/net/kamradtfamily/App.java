package net.kamradtfamily;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@RestController
public class App
{
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
//        StringService.wokeReceive();
    }

    @GetMapping("/string")
    Mono<String> returnsAMono() {
        return Mono.just(StringService.returnsAString());
    }

    @GetMapping("/efficient")
    Mono<String> returnsAMonoEfficiently() {
        return StringService.returnsAMono();
    }

    @GetMapping("/subscribe")
    Mono<String> returnsAMonoBad() throws InterruptedException {
        return StringService.returnsAMonoBad();
    }
    @GetMapping("/numbersbad")
    Flux<String> returnsNumbersBad() throws InterruptedException {
        return StringService.returnStringsBad();
    }
    @GetMapping("/numbersstillbad")
    Flux<String> returnNumbersStillBad() throws InterruptedException {
        return StringService.returnStringsStillBad();
    }

    @GetMapping("/numbersgood")
    Flux<String> returnNumberGood() throws InterruptedException {
        return StringService.returnStringsGood();
    }

    @GetMapping("/flood")
    Flux<String> returnFlood() {
        return StringService.returnFlood();
    }
}
