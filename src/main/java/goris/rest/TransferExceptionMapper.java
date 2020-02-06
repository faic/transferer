package goris.rest;

import goris.model.TransferException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TransferExceptionMapper implements ExceptionMapper<TransferException> {

    @Override
    public Response toResponse(TransferException exception) {
        return Response.status(400).entity(exception.getMessage()).build();
    }
}
