package dev.mvvasilev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mvvasilev.configuration.RabbitMQProperties;
import dev.mvvasilev.dto.SubmitLoggedEventDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.enums.UserEventType;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventLogService {

    private static final String USER_MANAGER_SOURCE = "user-management-service";

    private ModelMapper modelMapper;

    private ObjectMapper objectMapper;

    private RabbitTemplate rabbitTemplate;

    private RabbitMQProperties rabbitMQProperties;

    public EventLogService(ModelMapper modelMapper, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate, RabbitMQProperties rabbitMQProperties) {
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQProperties = rabbitMQProperties;
    }

    public void submitUserCreatedEvent(UserDTO userDTO) {
        SubmitLoggedEventDTO<UserDTO> dto = new SubmitLoggedEventDTO<>();
        dto.setEventType(UserEventType.USER_CREATED);
        dto.setSource(USER_MANAGER_SOURCE);
        dto.setSubmittedAt(LocalDateTime.now());
        dto.setVersion(1);
        dto.setData(userDTO);

        try {
            rabbitTemplate.convertAndSend(
                    rabbitMQProperties.getExchange(),
                    rabbitMQProperties.getSubmitEventRoutingKey(),
                    objectMapper.writeValueAsString(dto)
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
