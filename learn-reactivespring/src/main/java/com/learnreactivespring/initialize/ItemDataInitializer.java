package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

  @Autowired
  private ItemReactiveRepository itemReactiveRepository;

  @Override
  public void run(String... args) throws Exception {
    initialDataSetup();
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
