package goris.rest;

import goris.dto.AccountDto;
import goris.dto.AccountRequest;
import goris.dto.AccountUpdateRequest;
import goris.model.Account;
import goris.model.Currency;
import goris.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") String id) {
        Account account = accountService.getAccount(UUID.fromString(id)).get();
        AccountDto accountDto = new AccountDto(
                account.getExternalId().toString(),
                account.getAmount().toPlainString(),
                account.getCurrency().toString());
        return Response.status(200).entity(accountDto).build();
    }

    @POST
    public Response createAccount(AccountRequest request) {
        BigDecimal amount = new BigDecimal(request.getAmount());
        Currency currency = Currency.valueOf(request.getCurrency().toUpperCase());
        Account account = accountService.saveAccount(amount, currency);
        AccountDto accountDto = new AccountDto(
                account.getExternalId().toString(),
                account.getAmount().toPlainString(),
                account.getCurrency().toString());
        return Response.status(200).entity(accountDto).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAccount(@PathParam("id") String id, AccountUpdateRequest request) {
        Account account = accountService.updateAccount(UUID.fromString(id), new BigDecimal(request.getAmount()));
        AccountDto accountDto = new AccountDto(
                account.getExternalId().toString(),
                account.getAmount().toPlainString(),
                account.getCurrency().toString());
        return Response.status(200).entity(accountDto).build();
    }
}
