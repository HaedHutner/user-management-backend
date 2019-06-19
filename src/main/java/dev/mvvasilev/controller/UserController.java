package dev.mvvasilev.controller;

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
@RequestMapping("/api/user")
@RestController
@CrossOrigin
public class UserController {

    private UserFacade userFacade;

    @Autowired
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('GET_USERS')")
    public Page<UserDTO> queryUsers(@NotNull Pageable pageable) {
        return userFacade.getAllUsersPaginated(pageable);
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public long createUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        return userFacade.createUser(registerUserDTO);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('GET_USERS')")
    public UserDTO getUser(@PathVariable Long userId) {
        return userFacade.getUserById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public UserDTO updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return userFacade.updateUserById(userId, updateUserDTO);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public void deleteUser(@PathVariable Long userId) {
        userFacade.deleteUserById(userId);
    }

}
