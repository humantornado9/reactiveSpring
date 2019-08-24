package com.learnreactivespring.handler.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemHandler {

  @Autowired
  private ItemReactiveRepository itemReactiveRepository;

  public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
      .body(itemReactiveRepository.findAll(), Item.class);

  }

  public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {

    Mono<Item> mono = itemReactiveRepository.findById(serverRequest.pathVariable("id"));

    return mono.flatMap(item -> {
      return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(item));
    }).switchIfEmpty(ServerResponse.notFound().build());

  }

  public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
    Mono<Item> itemToBeInserted = serverRequest.bodyToMono(Item.class);

    return itemToBeInserted.flatMap(item -> ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(itemReactiveRepository.save(item), Item.class));

  }

  public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {

    Mono<Void> itemToBeDeleted =
      itemReactiveRepository.deleteById((serverRequest.pathVariable("id")));

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
      .body(itemToBeDeleted, Void.class);

  }

  public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {

    Mono<Item> itemToUpdate = serverRequest.bodyToMono(Item.class);

    Mono<Item> updated = itemToUpdate.flatMap(item -> {
        Mono<Item> currentItemMono = itemReactiveRepository.findById(serverRequest.pathVariable(
          "id"));

        return currentItemMono.flatMap(currentItem -> {
          currentItem.setPrice(item.getPrice());
          currentItem.setDescription(item.getDescription());
          return itemReactiveRepository.save(currentItem);
        });

    });
    return updated.flatMap(item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromObject(item))).switchIfEmpty(ServerResponse.notFound().build());
  }
}
