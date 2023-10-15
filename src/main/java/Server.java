import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class Server extends AbstractVerticle {

    private Router router;
    private HttpServer server;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        router = Router.router(vertx);

        router.route("/").handler(context -> {
            HttpServerResponse response = context.response();
            response.putHeader("content-type", "text/html")
                    .end("<h1>Hello world</h1>");
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(config().getInteger("http.port", 8080))
                .onSuccess(server -> {
                    this.server = server;
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }
}
