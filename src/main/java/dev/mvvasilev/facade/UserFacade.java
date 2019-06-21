package dev.mvvasilev.facade;

import dev.mvvasilev.dto.AuthenticateUserDTO;
import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.service.AuthenticationService;
import dev.mvvasilev.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Miroslav Vasilev
 */
@Component
public class UserFacade {

    private UserService userService;

    private ModelMapper modelMapper;

    private AuthenticationService tokenProvider;

    @Autowired
    public UserFacade(UserService userService, ModelMapper modelMapper, AuthenticationService tokenProvider) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.tokenProvider = tokenProvider;
    }

    public long createUser(RegisterUserDTO registerUserDTO) {
        Assert.notNull(registerUserDTO, "registerUserDTO cannot be null.");

        return userService.createUser(
                registerUserDTO.getEmail(),
                registerUserDTO.getRawPassword(),
                registerUserDTO.getFirstName(),
                registerUserDTO.getLastName(),
                registerUserDTO.getDateOfBirth()
        );
    }

    public UserDTO getUserById(long userId) {
        return modelMapper.map(
                userService.getUser(userId),
                UserDTO.class
        );
    }

    public UserDTO updateUserById(long userId, UpdateUserDTO updateUserDTO) {
        Assert.notNull(updateUserDTO, "updateUserDTO cannot be null.");

        return modelMapper.map(
                userService.updateUserById(
                        userId,
                        updateUserDTO.getEmail(),
                        updateUserDTO.getFirstName(),
                        updateUserDTO.getLastName(),
                        updateUserDTO.getDateOfBirth()
                ),
                UserDTO.class
        );
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
                        updateUserDTO.getDateOfBirth()
                ),
                UserDTO.class
        );
    }

    public void deleteUserFromRequest(HttpServletRequest request) {
        userService.deleteUserByEmail(tokenProvider.retrieveUsernameFromRequest(request));
    }
}
