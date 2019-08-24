package com.learnreactivespring.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class FluxAndMonoControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void testGetFlux1() {
    Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
      .accept(MediaType.APPLICATION_JSON_UTF8)
      .exchange()
      .expectStatus().isOk()
      .returnResult(Integer.class)
      .getResponseBody();

    StepVerifier.create(integerFlux)
      .expectSubscription()
      .expectNext(1,2,3,4)
      .verifyComplete();
  }

  @Test
  public void testGetFlux2() {
    webTestClient.get().uri("/flux")
      .accept(MediaType.APPLICATION_JSON_UTF8)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBodyList(Integer.class)
      .hasSize(4);
  }

  @Test
  public void testGetFlux3() {

    List<Integer> expected = Arrays.asList(1,2,3,4);

    EntityExchangeResult<List<Integer>> result = webTestClient.get().uri("/flux")
      .accept(MediaType.APPLICATION_JSON_UTF8)
      .exchange()
      .expectStatus().isOk()
      .expectBodyList(Integer.class)
      .returnResult();

    Assert.assertEquals(expected, result.getResponseBody());
  }

  @Test
  public void testGetFlux4() {

    List<Integer> expected = Arrays.asList(1,2,3,4);

    webTestClient.get().uri("/flux")
      .accept(MediaType.APPLICATION_JSON_UTF8)
      .exchange()
      .expectStatus().isOk()
      .expectBodyList(Integer.class)
      .consumeWith((listEntityExchangeResult) -> {
          Assert.assertEquals(expected, listEntityExchangeResult.getResponseBody());
      });
  }

  @Test
  public void tesstGetFluxStream1() {
    Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxAsStream")
      .accept(MediaType.APPLICATION_STREAM_JSON)
      .exchange()
      .expectStatus().isOk()
      .returnResult(Long.class)
      .getResponseBody();

    StepVerifier.create(longStreamFlux)
      .expectSubscription()
      .expectNext(0l, 1l, 2l)
      .thenCancel()
      .verify();
  }

  @Test
  public void testGetMono() {
    Integer expected = 1;
    webTestClient.get().uri("/mono")
      .accept(MediaType.APPLICATION_JSON_UTF8)
      .exchange()
      .expectStatus().isOk()
      .expectBody(Integer.class)
      .consumeWith((integerEntityExchangeResult) -> {
        Assert.assertEquals(expected, integerEntityExchangeResult.getResponseBody());
      });
  }
}
