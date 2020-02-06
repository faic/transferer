package goris;

import goris.dao.AccountHibernateDao;
import goris.dao.CurrencyRateHibernateDao;
import goris.dao.TransferHibernateDao;
import goris.rest.*;
import goris.service.AccountService;
import goris.service.CurrencyService;
import goris.service.TransferService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.SessionFactory;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;


public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8090);
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        AccountHibernateDao accountDao = new AccountHibernateDao();
        TransferHibernateDao transferHibernateDao = new TransferHibernateDao();
        CurrencyRateHibernateDao currencyRateHibernateDao = new CurrencyRateHibernateDao();

        ServletContextHandler handler = new ServletContextHandler(NO_SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);

        CurrencyService currencyService = new CurrencyService(currencyRateHibernateDao, sessionFactory);
        currencyService.initRates();
        AccountService accountService = new AccountService(accountDao, sessionFactory);
        TransferService transferService = new TransferService(
                accountDao,
                transferHibernateDao,
                currencyService,
                sessionFactory,
                accountService);

        handler.addServlet(
                new ServletHolder(new ServletContainer(new ResourceConfig()
                        .register(new AccountResource(accountService))
                        .register(new TransferResource(transferService))
                        .register(GenericExceptionMapper.class)
                        .register(AccountExceptionMapper.class)
                        .register(TransferExceptionMapper.class)
                )), "/*");
        server.start();
        server.join();
    }
}
