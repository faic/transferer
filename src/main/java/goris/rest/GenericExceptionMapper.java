package goris.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof IllegalArgumentException) {
            return Response.status(400).build();
        }
        return Response.status(500).build();
    }
}
