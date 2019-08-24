package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombineTest {

  @Test
  public void fluxTest1() {
    Flux<String> stringFlux1 = Flux.just("A", "B", "C");
    Flux<String> stringFlux2 = Flux.just("D", "E", "F");

    Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2).log();

    StepVerifier.create(mergedFlux)
      .expectNext("A", "B", "C", "D", "E", "F")
      .verifyComplete();
  }

  @Test
  public void fluxTest2() {
    Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
    Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

    Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2).log();

    StepVerifier.create(mergedFlux)
      .expectNextCount(6)
      .verifyComplete();
  }

  @Test
  public void fluxTest3() {
    Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
    Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

    Flux<String> concatinatedFlux = Flux.concat(stringFlux1, stringFlux2).log();

    StepVerifier.create(concatinatedFlux)
      .expectNextCount(6)
      .verifyComplete();
  }

  @Test
  public void fluxTest4() {
    Flux<String> stringFlux1 = Flux.just("A", "B", "C");
    Flux<String> stringFlux2 = Flux.just("D", "E", "F");

    Flux<String> zipedFlux = Flux.zip(stringFlux1, stringFlux2, (s, s2) -> {
      return s.concat(s2);
    }).log();

    StepVerifier.create(zipedFlux)
      .expectNextCount(3)
      .verifyComplete();
  }
}
