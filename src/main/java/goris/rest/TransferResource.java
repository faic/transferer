package goris.rest;

import goris.dto.TransferDto;
import goris.dto.TransferRequest;
import goris.model.Transfer;
import goris.service.TransferService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {
    private TransferService transferService;

    public TransferResource(TransferService transferService) {
        this.transferService = transferService;
    }

    @GET
    @Path("/{id}")
    public Response getTransfer(@PathParam("id") String id) {
        Transfer transfer = transferService.getTransfer(UUID.fromString(id)).get();
        TransferDto transferDto = new TransferDto(
                transfer.getExternalId().toString(),
                transfer.getAccountFrom().getExternalId().toString(),
                transfer.getAccountTo().getExternalId().toString(),
                transfer.getBaseAmount().toPlainString(),
                transfer.getAccountFrom().getCurrency().name(),
                transfer.getDstAmount().toPlainString(),
                transfer.getAccountTo().getCurrency().name()
        );
        return Response.status(200).entity(transferDto).build();
    }

    @POST
    public Response createTransfer(TransferRequest request) {
        UUID from = UUID.fromString(request.getFrom());
        UUID to = UUID.fromString(request.getTo());
        BigDecimal amount = new BigDecimal(request.getAmount());
        Transfer transfer = transferService.transfer(from, to, amount);

        TransferDto transferDto = new TransferDto(
                transfer.getExternalId().toString(),
                transfer.getAccountFrom().getExternalId().toString(),
                transfer.getAccountTo().getExternalId().toString(),
                transfer.getBaseAmount().toPlainString(),
                transfer.getAccountFrom().getCurrency().name(),
                transfer.getDstAmount().toPlainString(),
                transfer.getAccountTo().getCurrency().name()
        );

        return Response.status(200).entity(transferDto).build();
    }
}
