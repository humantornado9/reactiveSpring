package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

  @Test
  public void backPressureTest1() {

    Flux<Integer> flux = Flux.range(1,10)
      .log();

    StepVerifier.create(flux)
      .expectSubscription()
      .thenRequest(1)
      .expectNext(1)
      .thenRequest(1)
      .expectNext(2)
      .thenCancel()
      .verify();

  }

  @Test
  public void backPressureTest2() {

    Flux<Integer> flux = Flux.range(1,10)
      .log();

    flux.subscribe((element) -> {
      System.out.println("Element is "+ element);
    }
    ,(e) -> {
      System.err.println("Error is " + e);
    }
    ,() -> {
      System.out.println("Completed");
      }
      , (subscription) -> {
        subscription.request(2);
      });
  }

  @Test
  public void backPressureTest4() {

    Flux<Integer> flux = Flux.range(1,10)
      .log();

    flux.subscribe((element) -> {
        System.out.println("Element is "+ element);
      }
      ,(e) -> {
        System.err.println("Error is " + e);
      }
      ,() -> {
        System.out.println("Completed");
      });
  }

  @Test
  public void backPressureTest3() {

    Flux<Integer> flux = Flux.range(1,10)
      .log();

    flux.subscribe((element) -> {
        System.out.println("Element is "+ element);
      }
      ,(e) -> {
        System.err.println("Error is " + e);
      }
      ,() -> {
        System.out.println("Completed");
      }
      , (subscription) -> {
        subscription.cancel();
      });
  }

  @Test
  public void backPressureTest5() {

    Flux<Integer> flux = Flux.range(1,10)
      .log();

    flux.subscribe(new BaseSubscriber<Integer>() {
      @Override protected void hookOnNext(Integer value) {
        request(1);
        System.out.println("Value is " + value);

        if (value == 4)
          cancel();
      }
    });
  }
}
