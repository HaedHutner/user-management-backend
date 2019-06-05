package dev.mvvasilev.facade;

import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
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

    public void deleteUserById(long userId) {
        userService.deleteUser(userId);
    }
}
