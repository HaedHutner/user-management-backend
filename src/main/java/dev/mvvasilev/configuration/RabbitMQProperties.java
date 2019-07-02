package dev.mvvasilev.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eventlog")
public class RabbitMQProperties {

    @Value("{eventlog.exchange}")
    private String exchange;

    @Value("{eventlog.submit-event-routing-key}")
    private String submitEventRoutingKey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSubmitEventRoutingKey() {
        return submitEventRoutingKey;
    }

    public void setSubmitEventRoutingKey(String submitEventRoutingKey) {
        this.submitEventRoutingKey = submitEventRoutingKey;
    }
}
