package ch.esb.sedex.sidecar;

import java.io.InputStream;

import org.apache.camel.BeanInject;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class MyRouteBuilder extends RouteBuilder {

    @BeanInject
    private ConsumerTemplate consumerTemplate;

    public void configure() {

        from("file:data?delete=true&antInclude=envl-*.xml").
            process(exchange -> {

                Message in = exchange.getIn();
                String envlFilename = in.getHeader(Exchange.FILE_NAME, String.class);
                String identifier = StringUtils.substringAfter(FilenameUtils.getBaseName(envlFilename), "envl-");
                String dataFilename = "data-" + identifier + ".*";
                InputStream dataStream = consumerTemplate.receiveBodyNoWait("file:data?delete=true&antInclude=" + dataFilename, InputStream.class);
                if (dataStream == null) {

                    exchange.setException(new RuntimeException("data not found"));

                } else {

                    System.out.println("receive all files");

                }
            });        
    }
}
