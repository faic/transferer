package goris.rest;

import goris.model.AccountException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccountExceptionMapper implements ExceptionMapper<AccountException> {

    @Override
    public Response toResponse(AccountException exception) {
        return Response.status(400).entity(exception.getMessage()).build();
    }
}
