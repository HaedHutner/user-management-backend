package dev.mvvasilev.controller;

import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<UserDTO> queryUsers(@NotNull Pageable pageable) {
        return userFacade.getAllUsersPaginated(pageable);
    }

    @PostMapping("/")
    public long createUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        return userFacade.createUser(registerUserDTO);
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable Long userId) {
        return userFacade.getUserById(userId);
    }

    @PutMapping("/{userId}")
    public UserDTO updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return userFacade.updateUserById(userId, updateUserDTO);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userFacade.deleteUserById(userId);
    }

}
