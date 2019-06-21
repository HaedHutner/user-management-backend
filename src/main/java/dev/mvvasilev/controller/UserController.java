package dev.mvvasilev.controller;

import dev.mvvasilev.dto.AuthenticateUserDTO;
import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Miroslav Vasilev
 */
@RequestMapping("/api/users")
@RestController
@CrossOrigin
public class UserController {

    private UserFacade userFacade;

    @Autowired
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('UPDATE_OTHER_USER')")
    public Page<UserDTO> queryUsers(@NotNull Pageable pageable) {
        return userFacade.getAllUsersPaginated(pageable);
    }

    @PostMapping("/create")
    public long createUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        return userFacade.createUser(registerUserDTO);
    }

    @PostMapping("/authenticate")
    public String authenticateUser(@RequestBody @Valid AuthenticateUserDTO authenticateUserDTO) {
        return userFacade.authenticateUser(authenticateUserDTO);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('READ_OTHER_USER')")
    public UserDTO getUser(@PathVariable Long userId) {
        return userFacade.getUserById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('UPDATE_OTHER_USER')")
    public UserDTO updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return userFacade.updateUserById(userId, updateUserDTO);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('DELETE_OTHER_USER')")
    public void deleteUser(@PathVariable Long userId) {
        userFacade.deleteUserById(userId);
    }

}
