package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemRepositoryTest {

  @Autowired
  private ItemReactiveRepository itemReactiveRepository;

  @Before
  public void setup() {
    List<Item> itemsList = Arrays.asList(
      new Item(null, "Samsung Television", 400.0),
      new Item(null, "LG Television", 300.0),
      new Item(null, "Apple Watch", 500.0),
      new Item(null, "Beats Headphone", 200.0),
      new Item("bose_1001", "Bose Headphone", 200.0)
    );
    itemReactiveRepository.deleteAll()
      .thenMany(Flux.fromIterable(itemsList))
      .flatMap((item) -> {
        return itemReactiveRepository.save(item);
      })
      .doOnNext((item) -> {
        System.out.println("Inserted Item is "+item);
      })
      .blockLast();
  }

  @Test
  public void testGetAllItems() {
    Flux<Item> flux = itemReactiveRepository.findAll().log();

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNextCount(5)
      .verifyComplete();
  }

  @Test
  public void testGetItemById() {
    Mono<Item> mono = itemReactiveRepository.findById("bose_1001");

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNextMatches((item) -> {
        return item.getDescription().equals("Bose Headphone");
      })
      .verifyComplete();
  }

  @Test
  public void testFindByDescription() {
    Mono<Item> mono = itemReactiveRepository.findByDescription("Bose Headphone")
      .log("find item by description : ");

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNextCount(1)
      .verifyComplete();

  }

  @Test
  public void testSaveItem() {
    Item item = new Item(null, "Google Home Mini", 50.0);

    Mono<Item> mono = itemReactiveRepository.save(item).log("Saved Item : ");

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNextMatches(savedItem -> savedItem.getId() != null &&
        savedItem.getDescription().equals(item.getDescription()))
      .verifyComplete();
  }

  @Test
  public void testUpdateItem() {
    Mono<Item> mono = itemReactiveRepository.findByDescription("LG Television")
      .map(item -> {
        item.setPrice(500.0);
        return item;
      }).flatMap(item -> {
        return itemReactiveRepository.save(item);
      });

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNextMatches(item -> item.getPrice().equals(500.0))
      .verifyComplete();
  }

  @Test
  public void testDeleteItemById() {
    Mono<Void> mono = itemReactiveRepository.findById("bose_1001")
      .map(item -> item.getId()) // this step will convert the mono type to string
      .flatMap(id -> itemReactiveRepository.deleteById(id));

    StepVerifier.create(mono)
      .expectSubscription()
      .verifyComplete();

    StepVerifier.create(itemReactiveRepository.findAll().log("New Items :"))
      .expectNextCount(4)
      .verifyComplete();
  }

  @Test
  public void testDeleteItemByDescription() {
    Mono<Void> mono = itemReactiveRepository.findByDescription("LG Television")
      .flatMap(item -> {
         return itemReactiveRepository.delete(item);
      })
      .log();

    StepVerifier.create(mono)
      .expectSubscription()
      .verifyComplete();

    StepVerifier.create(itemReactiveRepository.findAll().log("New Items :"))
      .expectNextCount(4)
      .verifyComplete();
  }
}
