package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.mongodb.Mongo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;


public class ItemStreamControllerTest {

  @Autowired
  private ItemReactiveCappedRepository itemReactiveCappedRepository;

  @Autowired
  private MongoOperations mongoOperations;

  @Autowired
  private WebTestClient webTestClient;

  @Before
  public  void setup() {
    mongoOperations.dropCollection(ItemCapped.class);
    mongoOperations.createCollection(ItemCapped.class,
      CollectionOptions.empty().size(20).maxDocuments(50000).capped());

    //Every Second Create a Item and insert into Repo
    Flux<ItemCapped> itemFlux = Flux.interval(Duration.ofMillis(100))
      .map(aLong -> new ItemCapped(null, "Random Item "+ aLong, 100.00+aLong))
      .take(5);

    itemReactiveCappedRepository.insert(itemFlux)
      .blockLast();
  }

  //@Test
  public void testStreamAllItems() {
    Flux<ItemCapped> itemCappedFlux =
      webTestClient.get().uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
      .exchange()
      .expectStatus().isOk()
      .returnResult(ItemCapped.class)
      .getResponseBody()
      .take(5);

    StepVerifier.create(itemCappedFlux)
      .expectNextCount(5)
      .thenCancel()
      .verify();
  }


}
