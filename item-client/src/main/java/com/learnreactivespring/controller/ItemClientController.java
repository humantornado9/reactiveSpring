package com.learnreactivespring.controller;

import com.learnreactivespring.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {


    private WebClient webClient = WebClient.create("http://localhost:8080");

    // curl http://localhost:8081/client/retrieve
    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
      return webClient.get().uri("/v1/items")
        .retrieve()
        .bodyToFlux(Item.class)
        .log("items in client project retrieve :");
    }


    // curl http://localhost:8081/client/exchange
    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {
      return webClient.get().uri("/v1/items")
        .exchange()
        .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
        .log("items in client project exchange : ");
    }

    // curl http://localhost:8081/client/exchange/singleItem/ABC
  @GetMapping("/client/exchange/singleItem/{id}")
  public Mono<Item> getItemUsingExchange(@PathVariable String id) {
    return webClient.get().uri("/v1/items/{id}", id)
      .exchange()
      .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
      .log("Item in client project exchange : ");
  }

  // curl http://localhost:8081/client/retrieve/singleItem/ABC
  @GetMapping("/client/retrieve/singleItem/{id}")
  public Mono<Item> getItemUsingRetrieve(@PathVariable String id) {
    return webClient.get().uri("/v1/items/{id}", id)
      .retrieve()
      .bodyToMono(Item.class)
      .log("Item in client project retrieve : ");
  }

  //curl -d '{"id":null, "description":"Apple IPhone X", "price":999.99}' -H "content-type :application/json" -X POST http://localhost:8081/client/createItem
  @PostMapping("/client/createItem")
  public Mono<Item> createItem(@RequestBody Item item) {
      Mono<Item> mono = Mono.just(item);
      return webClient.post().uri("/v1/items")
        .contentType(MediaType.APPLICATION_JSON)
        .body(mono, Item.class)
        .retrieve()
        .bodyToMono(Item.class)
        .log("Created Item is :");
  }

  //curl -d '{"id":null, "description":"Beats Microhones", "price":66.99}' -H
  // "content-type:application/json" -X PUT http://localhost:8081/client/updateItem
  @PutMapping("/client/updateItem/{id}")
  public Mono<Item> updateItem(@PathVariable String id, @RequestBody Item item) {
    Mono<Item> mono = Mono.just(item);
    return webClient.put().uri("/v1/items/{id}", id)
      .contentType(MediaType.APPLICATION_JSON)
      .body(mono, Item.class)
      .retrieve()
      .bodyToMono(Item.class)
      .log("Updated Item is :");
  }

  // curl -X "DELETE" http://localhost:8081/client/deleteItem/ABC
  @DeleteMapping("/client/deleteItem/{id}")
  public Mono<Void> deleteItem(@PathVariable String id) {
    return webClient.delete().uri("/v1/items/{id}", id)
      .retrieve()
      .bodyToMono(Void.class)
      .log("Deleted Item is :");
  }

  @GetMapping("/client/retrieve/error")
  public Flux<Item> errorRetrieve() {
    return webClient.get().uri("/v1/items/runtimeException")
      .retrieve()
      .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
          Mono<String> errorMono = clientResponse.bodyToMono(String.class);
          return errorMono.flatMap(errorMessage -> {
              log.error("Error Message is "+errorMessage);
              throw new RuntimeException(errorMessage);
          });
      }).bodyToFlux(Item.class);

  }

  @GetMapping("/client/exchange/error")
  public Flux<Item> errorExchange() {
    return webClient.get().uri("/v1/items/runtimeException")
      .exchange()
      .flatMapMany(clientResponse -> {
        if (clientResponse.statusCode().is5xxServerError()) {
          return clientResponse.bodyToMono(String.class)
            .flatMap(errorMessage -> {
              log.error("Error Message is "+errorMessage);
              throw new RuntimeException(errorMessage);
            });
        } else {
          return clientResponse.bodyToFlux(Item.class);
        }
      });
  }
}




