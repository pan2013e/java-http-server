package HttpServer.protocol;

public enum HttpResponseType {
    OK(200),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    NOT_MODIFIED(304),
    TEMPORARY_REDIRECT(307),
    PERMANENTLY_REDIRECT(308),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    REQUEST_TIMEOUT(408),
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503);

    HttpResponseType(int code){
    }

    public int getCode() {
        return switch (this) {
            case OK -> 200;
            case MOVED_PERMANENTLY -> 301;
            case FOUND -> 302;
            case NOT_MODIFIED -> 304;
            case TEMPORARY_REDIRECT -> 307;
            case PERMANENTLY_REDIRECT -> 308;
            case BAD_REQUEST -> 400;
            case UNAUTHORIZED -> 401;
            case FORBIDDEN -> 403;
            case NOT_FOUND -> 404;
            case METHOD_NOT_ALLOWED -> 405;
            case REQUEST_TIMEOUT -> 408;
            case INTERNAL_SERVER_ERROR -> 500;
            case NOT_IMPLEMENTED -> 501;
            case BAD_GATEWAY -> 502;
            case SERVICE_UNAVAILABLE -> 503;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case OK -> "OK";
            case MOVED_PERMANENTLY -> "Moved Permanently";
            case FOUND -> "Found";
            case NOT_MODIFIED -> "Not Modified";
            case TEMPORARY_REDIRECT -> "Temporary Redirect";
            case PERMANENTLY_REDIRECT -> "Permanently Redirect";
            case BAD_REQUEST -> "Bad Request";
            case UNAUTHORIZED -> "Unauthorized";
            case FORBIDDEN -> "Forbidden";
            case NOT_FOUND -> "Not Found";
            case METHOD_NOT_ALLOWED -> "Method Not Allowed";
            case REQUEST_TIMEOUT -> "Request Timeout";
            case INTERNAL_SERVER_ERROR -> "Internal Server Error";
            case NOT_IMPLEMENTED -> "Not Implemented";
            case BAD_GATEWAY -> "Bad Gateway";
            case SERVICE_UNAVAILABLE -> "Service Unavailable";
        };
    }

}
