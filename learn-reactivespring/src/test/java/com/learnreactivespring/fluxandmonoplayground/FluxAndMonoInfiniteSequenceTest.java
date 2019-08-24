package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoInfiniteSequenceTest {

  @Test
  public void infiniteSequenceTest1() throws InterruptedException {

    Flux<Long> infinite = Flux.interval(Duration.ofMillis(100));

    infinite.subscribe((element) -> {
      System.out.println("Element is " + element);

    });

    Thread.sleep(3000);

  }

  @Test
  public void infiniteSequenceTest2() throws InterruptedException {

    Flux<Long> finite = Flux.interval(Duration.ofMillis(100))
      .take(3)
      .log();

    StepVerifier.create(finite)
      .expectNext(0L, 1L, 2L)
      .verifyComplete();

  }

  @Test
  public void infiniteSequenceTest3() throws InterruptedException {

    Flux<String> finite = Flux.interval(Duration.ofMillis(100))
      .take(3)
      .map((aLong) -> {
        return aLong+"a";
      })
      .log();

    StepVerifier.create(finite)
      .expectNext("0a", "1a", "2a")
      .verifyComplete();

  }

  @Test
  public void infiniteSequenceTest4() throws InterruptedException {

    Flux<String> finite = Flux.interval(Duration.ofMillis(100))
      .delayElements(Duration.ofSeconds(1))
      .take(3)
      .map((aLong) -> {
        return aLong+"a";
      })
      .log();

    StepVerifier.create(finite)
      .expectNext("0a", "1a", "2a")
      .verifyComplete();

  }
}
