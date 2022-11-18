package HttpServer.protocol;

public enum HttpMethod {
    GET("GET"), POST("POST"), PUT("PUT"),
    DELETE("DELETE"), HEAD("HEAD"), OPTIONS("OPTIONS"),
    CONNECT("CONNECT"), TRACE("TRACE");

    HttpMethod(String name) {}
}
