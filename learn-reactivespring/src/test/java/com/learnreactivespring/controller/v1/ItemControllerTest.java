package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
public class ItemControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ItemReactiveRepository itemReactiveRepository;

  @Before
  public void init()  {
    itemReactiveRepository.deleteAll()
      .thenMany(Flux.fromIterable(data()))
      .flatMap(itemReactiveRepository::save)
      .doOnNext(item -> {
        System.out.println("Item Inserted is : "+ item);
      })
      .blockLast();
  }

  private List<Item> data() {

    return Arrays.asList(
      new Item(null, "Samsung Television", 500.0),
      new Item(null, "LG Television", 400.0),
      new Item(null, "Apple Watch", 299.99),
      new Item("ABC", "Beats Headphones", 199.99)
    );

  }

  @Test
  public void testGetAllItems1() {
    webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBodyList(Item.class)
      .hasSize(4);
  }

  @Test
  public void testGetAllItems2() {
    webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBodyList(Item.class)
      .consumeWith(listEntityExchangeResult -> {
        List<Item> items = listEntityExchangeResult.getResponseBody();
        items.forEach(item -> {
          Assert.assertNotNull(item.getId());
        });
      });
  }

  @Test
  public void testGetAllItems3() {
    Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .returnResult(Item.class)
      .getResponseBody().log();

    StepVerifier.create(itemsFlux)
      .expectSubscription()
      .expectNextCount(4)
      .verifyComplete();
  }

  @Test
  public void testGetItemById1() {
    webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.price", 199.99);
  }

  @Test
  public void testGetItemById2() {
    webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "DEF")
      .exchange()
      .expectStatus().isNotFound();
  }

  @Test
  public void testCreateItem() {
    Item item = new Item(null, "Bose Headphones", 399.99);

    webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
      .body(Mono.just(item), Item.class)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.id").isNotEmpty()
      .jsonPath("$.description").isEqualTo("Bose Headphones")
      .jsonPath("$.price").isEqualTo(399.99);
  }

  @Test
  public void testDeleteItem() {
    webTestClient.delete().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
      .accept(MediaType.APPLICATION_JSON_UTF8)
      .exchange()
      .expectStatus().isOk()
      .expectBody(Void.class);
  }

  @Test
  public void testUpdateItem1() {
    Item item = new Item(null, "Beats Headphones", 299.99);

    webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
      .body(Mono.just(item), Item.class)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.price", 299.99);
  }

  @Test
  public void testUpdateItem2() {
    Item item = new Item(null, "Beats Headphones", 299.99);

    webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "DEF")
      .body(Mono.just(item), Item.class)
      .exchange()
      .expectStatus().isNotFound();
  }

  @Test
  public void testRuntimeException() {
    webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/runtimeException"))
      .exchange().expectStatus().is5xxServerError()
      .expectBody(String.class)
      .isEqualTo("Runtime Exception occurred");
  }
}
