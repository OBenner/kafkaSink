package ru.neoflex.kafkasink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.messaging.support.GenericMessage;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@EnableBinding(Sink.class)
@Slf4j
public class KafkasinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkasinkApplication.class, args);
    }


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private DataSource dataSource;

    @StreamListener(Sink.INPUT)
    public void input(Account account) {
        log.info(String.valueOf(account));
        String subtraction = "UPDATE test set amount = amount-" + account.getAmount() + " WHERE phone =" + account.getPhone();
        String addition = "UPDATE test set amount = amount+" + account.getAmount() + " WHERE phone =" + account.getPhone();
        if (account.getOperation().equalsIgnoreCase("addition")) {
            jdbcTemplate.update(addition);
        } else {
            jdbcTemplate.update(subtraction);
        }

    }

    @PostConstruct
    public void initializeData() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(resourceLoader.getResource("classpath:sample-schema.sql"));
        populator.setContinueOnError(true);
        DatabasePopulatorUtils.execute(populator, dataSource);
    }

}

