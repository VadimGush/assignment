package org.gush;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.gush.database.Database;

import java.io.IOException;

public class Server extends AbstractVerticle {

    private Router router;
    private HttpServer server;
    private Service service;

    @Override
    public void start(Promise<Void> start) throws Exception {
        router = Router.router(vertx);

        service = new Service(new Database("logs.txt"));

        router.route().handler(BodyHandler.create());

        router.route("/")
            .handler(context -> {

            HttpServerResponse response = context.response();
            response.putHeader("content-type", "text/html");

            final String query = context.body().asJsonObject().getString("text");
            try {
                final Service.Result result = service.handle(query);
                final JsonObject jsonResponse = new JsonObject();
                jsonResponse.put("value", result.value);
                jsonResponse.put("lexical", result.lexical);
                response.end(jsonResponse.encode());
            } catch (final IOException e) {
                response.setStatusCode(500);
                response.end("Failed due to: " + e);
            }

        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(config().getInteger("http.port", 8080))
                .onSuccess(server -> {
                    this.server = server;
                    start.complete();
                })
                .onFailure(start::fail);

    }

}

