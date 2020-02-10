package goris;

import com.google.gson.Gson;
import goris.dao.AccountHibernateDao;
import goris.dao.CurrencyRateHibernateDao;
import goris.dao.TransferHibernateDao;
import goris.dto.*;
import goris.rest.*;
import goris.service.AccountService;
import goris.service.CurrencyService;
import goris.service.TransferService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class ApiTest extends JerseyTest {

    private final static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final static AccountHibernateDao accountDao = new AccountHibernateDao();
    private final static TransferHibernateDao transferHibernateDao = new TransferHibernateDao();
    private final static CurrencyRateHibernateDao currencyRateHibernateDao = new CurrencyRateHibernateDao();
    private final static CurrencyService currencyService = new CurrencyService(currencyRateHibernateDao, sessionFactory);
    private final static AccountService accountService = new AccountService(accountDao, sessionFactory);
    private final static TransferService transferService = new TransferService(
            accountDao,
            transferHibernateDao,
            currencyService,
            sessionFactory,
            accountService);
    @Override
    protected Application configure() {
        return new ResourceConfig()
                        .register(new AccountResource(accountService))
                        .register(new TransferResource(transferService))
                        .register(GenericExceptionMapper.class)
                        .register(AccountExceptionMapper.class)
                        .register(TransferExceptionMapper.class);
    }

    @BeforeClass
    public static void before() {
        currencyService.initRates();
    }

    @Test
    public void testPostAccountSuccess() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAmount("100.00");
        accountRequest.setCurrency("RUB");
        Response response = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest),
                        MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response.getStatus());
        AccountDto accountDto = new Gson().fromJson(response.readEntity(String.class), AccountDto.class);
        Assert.assertEquals("100.00", accountDto.getAmount());
        Assert.assertEquals("RUB", accountDto.getCurrency());
    }

    @Test
    public void testGetAccountSuccess() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAmount("100.00");
        accountRequest.setCurrency("RUB");

        Response response = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest),
                        MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response.getStatus());
        AccountDto accountDto = new Gson().fromJson(response.readEntity(String.class), AccountDto.class);
        String id = accountDto.getId();

        response = target("/accounts/").path(id).request().get();
        accountDto = new Gson().fromJson(response.readEntity(String.class), AccountDto.class);
        Assert.assertEquals("100.00", accountDto.getAmount());
        Assert.assertEquals("RUB", accountDto.getCurrency());
    }

    @Test
    public void testGetNotExistingAccount400() {
        String id = UUID.randomUUID().toString();
        Response response = target("/accounts/").path(id).request()
                .get();
        Assert.assertEquals(400, response.getStatus());
    }


    @Test
    public void testPutAccountSuccess() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAmount("100.00");
        accountRequest.setCurrency("RUB");

        Response response = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest), MediaType.APPLICATION_JSON_TYPE));
        AccountDto accountDto = new Gson().fromJson(response.readEntity(String.class), AccountDto.class);
        String id = accountDto.getId();
        AccountUpdateRequest updateRequest = new AccountUpdateRequest();
        updateRequest.setAmount("250");

        response = target("/accounts/").path(id).request()
                .put(Entity.entity(new Gson().toJson(updateRequest), MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response.getStatus());
        accountDto = new Gson().fromJson(response.readEntity(String.class), AccountDto.class);
        Assert.assertEquals("250.00", accountDto.getAmount());
        Assert.assertEquals("RUB", accountDto.getCurrency());
    }

    @Test
    public void testPutNotExistingAccount400() {
        String id = UUID.randomUUID().toString();
        AccountUpdateRequest updateRequest = new AccountUpdateRequest();
        updateRequest.setAmount("250");
        Response response = target("/accounts/").path(id).request()
                .put(Entity.entity(new Gson().toJson(updateRequest), MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }


    @Test
    public void testPostNegativeNumberAccount400() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAmount("-100.00");
        accountRequest.setCurrency("RUB");
        Response response = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest),
                        MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }


    @Test
    public void testPostOneCurrencyTransferSuccess() {
        AccountRequest accountRequest1 = new AccountRequest();
        accountRequest1.setAmount("100.00");
        accountRequest1.setCurrency("RUB");

        AccountRequest accountRequest2 = new AccountRequest();
        accountRequest2.setAmount("200.00");
        accountRequest2.setCurrency("RUB");

        Response response1 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest1), MediaType.APPLICATION_JSON_TYPE));

        Response response2 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest2), MediaType.APPLICATION_JSON_TYPE));

        AccountDto accountDto1 = new Gson().fromJson(response1.readEntity(String.class), AccountDto.class);
        AccountDto accountDto2 = new Gson().fromJson(response2.readEntity(String.class), AccountDto.class);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFrom(accountDto1.getId());
        transferRequest.setTo(accountDto2.getId());
        transferRequest.setAmount("25.00");

        Response response = target("/transfers").request()
                .post(Entity.entity(new Gson().toJson(transferRequest),
                        MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response.getStatus());
        TransferDto transfer = new Gson().fromJson(response.readEntity(String.class), TransferDto.class);
        Assert.assertEquals("25.00", transfer.getBaseAmount());
        Assert.assertEquals("25.00", transfer.getDstAmount());
        Assert.assertEquals(accountDto1.getId(), transfer.getFrom());
        Assert.assertEquals(accountDto2.getId(), transfer.getTo());
        Assert.assertEquals("RUB", transfer.getBaseCurrency());
        Assert.assertEquals("RUB", transfer.getDstCurrency());
    }


    @Test
    public void testPostAndGetDifferentCurrenciesTransferSuccess() {
        AccountRequest accountRequest1 = new AccountRequest();
        accountRequest1.setAmount("100.00");
        accountRequest1.setCurrency("RUB");

        AccountRequest accountRequest2 = new AccountRequest();
        accountRequest2.setAmount("200.00");
        accountRequest2.setCurrency("USD");

        Response response1 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest1),
                        MediaType.APPLICATION_JSON_TYPE));

        Response response2 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest2),
                        MediaType.APPLICATION_JSON_TYPE));


        AccountDto accountDto1 = new Gson().fromJson(response1.readEntity(String.class), AccountDto.class);
        AccountDto accountDto2 = new Gson().fromJson(response2.readEntity(String.class), AccountDto.class);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFrom(accountDto1.getId());
        transferRequest.setTo(accountDto2.getId());
        transferRequest.setAmount("25.00");

        Response response = target("/transfers").request()
                .post(Entity.entity(new Gson().toJson(transferRequest),
                        MediaType.APPLICATION_JSON_TYPE));

        Assert.assertEquals(200, response.getStatus());
        TransferDto transfer = new Gson().fromJson(response.readEntity(String.class), TransferDto.class);
        BigDecimal dstAmount = BigDecimal.valueOf(25.00).multiply(CurrencyService.RUB_USD)
                .setScale(2, RoundingMode.DOWN);
        Assert.assertEquals("25.00", transfer.getBaseAmount());
        Assert.assertEquals(dstAmount.toPlainString(), transfer.getDstAmount());
        Assert.assertEquals(accountDto1.getId(), transfer.getFrom());
        Assert.assertEquals(accountDto2.getId(), transfer.getTo());
        Assert.assertEquals("RUB", transfer.getBaseCurrency());
        Assert.assertEquals("USD", transfer.getDstCurrency());


        response = target("/accounts/").path(accountDto1.getId()).request()
                .get();
        accountDto1 = new Gson().fromJson(response.readEntity(String.class), AccountDto.class);
        Assert.assertEquals("75.00", accountDto1.getAmount());

        response = target("/accounts/").path(accountDto2.getId()).request()
                .get();
        accountDto2 = new Gson().fromJson(response.readEntity(String.class), AccountDto.class);
        Assert.assertEquals(dstAmount.add(BigDecimal.valueOf(200)).toPlainString(), accountDto2.getAmount());

        response = target("/transfers/").path(transfer.getId()).request()
                .get();
        Assert.assertEquals(200, response.getStatus());

        transfer = new Gson().fromJson(response.readEntity(String.class), TransferDto.class);

        Assert.assertEquals("25.00", transfer.getBaseAmount());
        Assert.assertEquals(dstAmount.toPlainString(), transfer.getDstAmount());
        Assert.assertEquals(accountDto1.getId(), transfer.getFrom());
        Assert.assertEquals(accountDto2.getId(), transfer.getTo());
        Assert.assertEquals("RUB", transfer.getBaseCurrency());
        Assert.assertEquals("USD", transfer.getDstCurrency());
    }


    @Test
    public void testPostSameAccountTransfer400() {
        AccountRequest accountRequest1 = new AccountRequest();
        accountRequest1.setAmount("100.00");
        accountRequest1.setCurrency("RUB");

        Response response1 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest1),
                        MediaType.APPLICATION_JSON_TYPE));
        AccountDto accountDto1 = new Gson().fromJson(response1.readEntity(String.class), AccountDto.class);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFrom(accountDto1.getId());
        transferRequest.setTo(accountDto1.getId());
        transferRequest.setAmount("25.00");

        Response response = target("/transfers").request()
                .post(Entity.entity(new Gson().toJson(transferRequest),
                        MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }


    @Test
    public void testPostIllegalAmountTransfer400() {
        AccountRequest accountRequest1 = new AccountRequest();
        accountRequest1.setAmount("100.00");
        accountRequest1.setCurrency("RUB");

        AccountRequest accountRequest2 = new AccountRequest();
        accountRequest2.setAmount("200.00");
        accountRequest2.setCurrency("USD");

        Response response1 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest1),
                        MediaType.APPLICATION_JSON_TYPE));

        Response response2 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest2),
                        MediaType.APPLICATION_JSON_TYPE));


        AccountDto accountDto1 = new Gson().fromJson(response1.readEntity(String.class), AccountDto.class);
        AccountDto accountDto2 = new Gson().fromJson(response2.readEntity(String.class), AccountDto.class);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFrom(accountDto1.getId());
        transferRequest.setTo(accountDto2.getId());
        transferRequest.setAmount("522.00");

        Response response = target("/transfers").request()
                .post(Entity.entity(new Gson().toJson(transferRequest),
                        MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }


    @Test
    public void testPostNegativeAmountTransfer400() {
        AccountRequest accountRequest1 = new AccountRequest();
        accountRequest1.setAmount("100.00");
        accountRequest1.setCurrency("RUB");

        AccountRequest accountRequest2 = new AccountRequest();
        accountRequest2.setAmount("200.00");
        accountRequest2.setCurrency("USD");

        Response response1 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest1),
                        MediaType.APPLICATION_JSON_TYPE));

        Response response2 = target("/accounts").request()
                .post(Entity.entity(new Gson().toJson(accountRequest2),
                        MediaType.APPLICATION_JSON_TYPE));


        AccountDto accountDto1 = new Gson().fromJson(response1.readEntity(String.class), AccountDto.class);
        AccountDto accountDto2 = new Gson().fromJson(response2.readEntity(String.class), AccountDto.class);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFrom(accountDto1.getId());
        transferRequest.setTo(accountDto2.getId());
        transferRequest.setAmount("-25.00");

        Response response = target("/transfers").request()
                .post(Entity.entity(new Gson().toJson(transferRequest),
                        MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testGetNotExistingTransfer400() {
        Response response = target("/transfers/").path(UUID.randomUUID().toString()).request()
                .get();
        Assert.assertEquals(400, response.getStatus());
    }
}
