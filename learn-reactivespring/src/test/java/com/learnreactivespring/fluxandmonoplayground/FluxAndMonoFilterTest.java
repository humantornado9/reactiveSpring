package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

  @Test
  public void fluxFilterTest1() {
    List<String> names = Arrays.asList("Rakesh", "Muralydharan");

    Flux<String> namesFlux = Flux.fromIterable(names)
      .filter(s -> s.startsWith("R")).log();

    StepVerifier.create(namesFlux)
      .expectNext("Rakesh")
      .verifyComplete();
  }

  @Test
  public void fluxFilterTest2() {
    List<String> names = Arrays.asList("Rakesh", "Muralydharan");

    Flux<String> namesFlux = Flux.fromIterable(names)
      .filter(s -> s.length() > 6).log();

    StepVerifier.create(namesFlux)
      .expectNext("Muralydharan")
      .verifyComplete();
  }
}
