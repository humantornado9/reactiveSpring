package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

  @Autowired
  private ItemReactiveRepository itemReactiveRepository;

  @Autowired
  private ItemReactiveCappedRepository itemReactiveCappedRepository;

  @Autowired
  private MongoOperations mongoOperations;

  @Override
  public void run(String... args) throws Exception {
    initialDataSetup();
    createCappedCollection();
    dataSetupForCappedCollection();;
  }

  private void createCappedCollection() {
    mongoOperations.dropCollection(ItemCapped.class);
    mongoOperations.createCollection(ItemCapped.class,
      CollectionOptions.empty().maxDocuments(20).size(50000).capped());
  }

  public void dataSetupForCappedCollection() {
    //Every Second Create a Item and insert into Repo
    Flux<ItemCapped> itemFlux = Flux.interval(Duration.ofSeconds(1))
      .map(aLong -> new ItemCapped(null, "Random Item "+ aLong, 100.00+aLong));

    itemReactiveCappedRepository.insert(itemFlux)
    .subscribe(itemCapped -> {
      System.out.println("Inserted ITEM is " + itemCapped);
    });

  }

  private void initialDataSetup() {
    itemReactiveRepository.deleteAll()
      .thenMany(Flux.fromIterable(data()))
      .flatMap(item -> {
        return itemReactiveRepository.save(item);
      })
      .thenMany(itemReactiveRepository.findAll())
      .subscribe(item -> {
        System.out.println("Item inserted from Commandline Runner " + item);
      });
  }

  private List<Item> data() {

    return Arrays.asList(
      new Item(null, "Samsung Television", 500.0),
      new Item(null, "LG Television", 400.0),
      new Item(null, "Apple Watch", 299.99),
      new Item("ABC", "Beats Headphones", 199.99)
    );

  }
}
