package dev.mvvasilev.facade;

import dev.mvvasilev.common.enums.EventType;
import dev.mvvasilev.common.service.EventLogService;
import dev.mvvasilev.dto.*;
import dev.mvvasilev.service.AuthenticationService;
import dev.mvvasilev.service.UserService;
import dev.mvvasilev.util.Address;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * @author Miroslav Vasilev
 */
@Component
public class UserFacade {

    private static final String EVENT_SOURCE = "user-management-service";

    private UserService userService;

    private ModelMapper modelMapper;

    private AuthenticationService tokenProvider;

    private EventLogService eventLogService;

    @Autowired
    public UserFacade(UserService userService, ModelMapper modelMapper, AuthenticationService tokenProvider, EventLogService eventLogService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.tokenProvider = tokenProvider;
        this.eventLogService = eventLogService;
    }

    public long createUser(RegisterUserDTO registerUserDTO) {
        Assert.notNull(registerUserDTO, "registerUserDTO cannot be null.");

        UserDTO result = modelMapper.map(userService.createUser(
                registerUserDTO.getEmail(),
                registerUserDTO.getRawPassword(),
                registerUserDTO.getFirstName(),
                registerUserDTO.getLastName(),
                registerUserDTO.getDateOfBirth(),
                registerUserDTO.getAddresses().stream().map(dto -> modelMapper.map(dto, Address.class)).collect(Collectors.toSet())
        ), UserDTO.class);

        eventLogService.submitEvent(EventType.USER_CREATED, EVENT_SOURCE, LocalDateTime.now(), 0, result);

        return result.getId();
    }

    public UserDTO getUserById(long userId) {
        return modelMapper.map(
                userService.getUser(userId),
                UserDTO.class
        );
    }

    public UserDTO updateUserById(long userId, UpdateUserDTO updateUserDTO) {
        Assert.notNull(updateUserDTO, "updateUserDTO cannot be null.");

        UserDTO result = modelMapper.map(
                userService.updateUserById(
                        userId,
                        updateUserDTO.getEmail(),
                        updateUserDTO.getFirstName(),
                        updateUserDTO.getLastName(),
                        updateUserDTO.getDateOfBirth(),
                        updateUserDTO.getAddresses().stream().map(dto -> modelMapper.map(dto, Address.class)).collect(Collectors.toSet())
                ),
                UserDTO.class
        );

        eventLogService.submitEvent(EventType.USER_UPDATED, EVENT_SOURCE, LocalDateTime.now(), 0, result);

        return result;
    }

    public void deleteUserById(long userId) {
        userService.deleteUserById(userId);
    }

    public Page<UserDTO> getAllUsersPaginated(Pageable pageable) {
        Assert.notNull(pageable, "pageable cannot be null");

        return userService.getUsers(pageable).map((user) -> modelMapper.map(user, UserDTO.class));
    }

    public String authenticateUser(AuthenticateUserDTO authenticateUserDTO) {
        return userService.fetchUserJWT(authenticateUserDTO.getEmail(), authenticateUserDTO.getRawPassword());
    }

    public UserDTO getUserFromRequest(HttpServletRequest request) {
        return modelMapper.map(
                userService.getUser(tokenProvider.retrieveUsernameFromRequest(request)),
                UserDTO.class
        );
    }

    public UserDTO updateUserFromRequest(HttpServletRequest request, UpdateUserDTO updateUserDTO) {
        return modelMapper.map(
                userService.updateUserByEmail(
                        tokenProvider.retrieveUsernameFromRequest(request),
                        updateUserDTO.getEmail(),
                        updateUserDTO.getFirstName(),
                        updateUserDTO.getLastName(),
                        updateUserDTO.getDateOfBirth(),
                        updateUserDTO.getAddresses().stream().map(dto -> modelMapper.map(dto, Address.class)).collect(Collectors.toSet())
                ),
                UserDTO.class
        );
    }

    public void deleteUserFromRequest(HttpServletRequest request) {
        userService.deleteUserByEmail(tokenProvider.retrieveUsernameFromRequest(request));
    }
}
