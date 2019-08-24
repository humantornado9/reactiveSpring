package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

  @Test
  public void fluxIterableTest() {
    List<String> namesList = Arrays.asList("Rakesh", "Muraly");

    Flux<String> stringFlux = Flux.fromIterable(namesList);

    StepVerifier.create(stringFlux)
      .expectNext("Rakesh")
      .expectNext("Muraly")
      .verifyComplete();
  }

  @Test
  public void fluxArrayTest() {
    String[] namesArray = new String[] {"Rakesh", "Muraly"};

    Flux<String> stringFlux = Flux.fromArray(namesArray).log();

    StepVerifier.create(stringFlux)
      .expectNext("Rakesh", "Muraly")
      .verifyComplete();
  }

  @Test
  public void fluxStreamTest() {
    List<String> namesList = Arrays.asList("Rakesh", "Muraly");

    Flux<String> stringFlux = Flux.fromStream(namesList.stream()).log();

    StepVerifier.create(stringFlux)
      .expectNext("Rakesh", "Muraly")
      .verifyComplete();
  }

  @Test
  public void monoUsingJustOrEmpty() {

    Mono<String> stringMono = Mono.justOrEmpty(null);

    StepVerifier.create(stringMono.log())
      .verifyComplete();

  }

  @Test
  public void monoUsingSupplier() {

    Supplier<String> stringSupplier = () -> "Rakesh";

    Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

    StepVerifier.create(stringMono)
      .expectNext("Rakesh")
      .verifyComplete();
  }

  @Test
  public void fluxUsingRange() {
    Flux<Integer> rangeFlux = Flux.range(1,5).log();

    StepVerifier.create(rangeFlux)
      .expectNext(1,2,3,4,5)
      .verifyComplete();
  }
}
