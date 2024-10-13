package top.hcode.hoj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Bean
    public Queue emailQueue(){
        return QueueBuilder
                .durable("examPasswordEmail")
                .build();
    }

    @Bean
    public Queue questionImportFileQueue(){
        return QueueBuilder
                .durable("questionImportFile")
                .build();
    }

    @Bean
    public Queue examImageShotQueue(){
        return QueueBuilder
                .durable("examImageShot")
                .build();
    }
//    @Bean
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
