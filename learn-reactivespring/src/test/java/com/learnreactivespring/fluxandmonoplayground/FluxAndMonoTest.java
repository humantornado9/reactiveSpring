package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

  @Test
  public void fluxTest1() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
      /*.concatWith(Flux.error(new RuntimeException("Exception Occurred")))*/
      .concatWith(Flux.just("After Exception"))
      .log();
    stringFlux
      .subscribe((output) -> System.out.println(output),
        (e) -> System.err.println(e),
        ()-> System.out.println("Complete!"));
  }

  @Test
  public void fluxTest2() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring");

    StepVerifier.create(stringFlux)
      .expectNext("Spring")
      .expectNext("Spring Boot")
      .expectNext("Reactive Spring")
      .expectComplete();
  }

  @Test
  public void fluxTest3() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
      .concatWith(Flux.error(new RuntimeException("Exception Occurred")));

    StepVerifier.create(stringFlux)
      .expectNext("Spring", "Spring Boot", "Reactive Spring")
      .expectError(RuntimeException.class)
      .verify();

  }

  @Test
  public void fluxTest4() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
      .log();

    StepVerifier.create(stringFlux)
      .expectNext("Spring")
      .expectNext("Spring Boot")
      .expectNext("Reactive Spring")
      .expectErrorMessage("Exception Occurred")
      .verify();

  }

  @Test
  public void monoTest1() {
    Mono<String> stringMono = Mono.just("Spring");

    StepVerifier.create(stringMono.log())
      .expectNext("Spring")
      .expectComplete();
  }

  @Test
  public void monoTest2() {
    StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
    .expectError(RuntimeException.class)
    .verify();

  }

}
