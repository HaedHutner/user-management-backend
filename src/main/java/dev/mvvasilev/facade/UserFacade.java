package dev.mvvasilev.facade;

import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The purpose of the UserFacade is to act as the connecting point between Controllers and Services.
 * A Facade should only work with DTOs and other raw inputs from controllers, and any deeper business-related logic is delegated to service methods.
 * Facades should not access the database directly, instead they should be relying on services for database access.
 *
 * @author Miroslav Vasilev
 */
@Component
public class UserFacade {

    private UserService userService;

    private ModelMapper modelMapper;

    @Autowired
    public UserFacade(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public long createUser(RegisterUserDTO registerUserDTO) {
        return userService.createUser(
                registerUserDTO.getEmail(),
                registerUserDTO.getRawPassword(),
                registerUserDTO.getFirstName(),
                registerUserDTO.getLastName(),
                registerUserDTO.getDateOfBirth()
        );
    }

    public UserDTO getUserById(Long userId) {
        return modelMapper.map(
                userService.getUser(userId),
                UserDTO.class
        );
    }

    public UserDTO updateUserById(Long userId, UpdateUserDTO updateUserDTO) {
        return modelMapper.map(
                userService.updateUser(
                        userId,
                        updateUserDTO.getEmail(),
                        updateUserDTO.getFirstName(),
                        updateUserDTO.getLastName(),
                        updateUserDTO.getDateOfBirth()
                ),
                UserDTO.class
        );
    }

    public void deleteUserById(Long userId) {
        userService.deleteUserById(userId);
    }
}
