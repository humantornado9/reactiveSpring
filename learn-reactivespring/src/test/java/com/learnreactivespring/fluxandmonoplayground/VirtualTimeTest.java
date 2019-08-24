package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

  @Test
  public void testWithoutVirtualTime() {
    Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
      .take(3)
      .log();

    StepVerifier.create(longFlux)
      .expectSubscription()
      .expectNext(0l, 1l, 2l)
      .verifyComplete();
  }

  @Test
  public void testWithVirtualTime() {
    VirtualTimeScheduler.getOrSet();

    Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
      .take(3)
      .log();

    StepVerifier.withVirtualTime(()-> longFlux)
      .expectSubscription()
      .thenAwait(Duration.ofSeconds(3))
      .expectNext(0l, 1l, 2l)
      .verifyComplete();
  }
}
