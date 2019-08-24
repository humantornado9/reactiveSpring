package com.learnreactivespring.router.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.handler.v1.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemRouterFunctionConfig {

  @Bean
  public RouterFunction<ServerResponse> routeItems(ItemHandler itemHandler) {
    return RouterFunctions.route(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
      .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemHandler::getAllItems)
      .andRoute(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 +"/{id}")
        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemHandler::getItemById)
      .andRoute(RequestPredicates.POST(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
      .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemHandler::createItem)
      .andRoute(RequestPredicates.DELETE(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemHandler::deleteItem)
    .andRoute(RequestPredicates.PUT(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
      .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemHandler::updateItem);
  }
}
