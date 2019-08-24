package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

  @Test
  public void fluxErrorHandling() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
      .concatWith(Flux.just("D"))
      .onErrorResume(
        (e) -> {
          System.err.println("Exception is "+ e);
          return Flux.just("Default", "Default1");
        }
      )
      .log();

    StepVerifier.create(stringFlux)
      .expectSubscription()
      .expectNext("A", "B", "C")
      //.expectError(RuntimeException.class)
      .expectNext("Default", "Default1")
      .verifyComplete();
  }

  @Test
  public void fluxErrorHandlingOnErrorReturn() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
      .concatWith(Flux.just("D"))
      .onErrorReturn("Default")
      .log();

    StepVerifier.create(stringFlux)
      .expectSubscription()
      .expectNext("A", "B", "C")
      //.expectError(RuntimeException.class)
      .expectNext("Default")
      .verifyComplete();
  }

  @Test
  public void fluxErrorHandlingOnErrorMap() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
      .concatWith(Flux.just("D"))
      .onErrorMap((e) -> {
        return new CustomException(e);
      })
      .log();

    StepVerifier.create(stringFlux)
      .expectSubscription()
      .expectNext("A", "B", "C")
      .expectError(CustomException.class)
     // .expectNext("Default")
      .verify();
  }

  @Test
  public void fluxErrorHandlingOnErrorMapRetry() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
      .concatWith(Flux.just("D"))
      .onErrorMap((e) -> {
        return new CustomException(e);
      })
      .retry(1)
      .log();

    StepVerifier.create(stringFlux)
      .expectSubscription()
      .expectNext("A", "B", "C")
      .expectNext("A", "B", "C")
      .expectError(CustomException.class)
      // .expectNext("Default")
      .verify();
  }

  @Test
  public void fluxErrorHandlingOnErrorMapRetryBackoff() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
      .concatWith(Flux.just("D"))
      .onErrorMap((e) -> {
        return new CustomException(e);
      })
      .retryBackoff(1, Duration.ofSeconds(5))
      .log();

    StepVerifier.create(stringFlux)
      .expectSubscription()
      .expectNext("A", "B", "C")
      .expectNext("A", "B", "C")
      .expectError(IllegalStateException.class)
      // .expectNext("Default")
      .verify();
  }

}
