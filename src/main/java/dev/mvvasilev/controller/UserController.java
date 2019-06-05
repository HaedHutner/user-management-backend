package dev.mvvasilev.controller;

import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Miroslav Vasilev
 */
@RequestMapping("/api/user")
@RestController
public class UserController {

    private UserFacade userFacade;

    @Autowired
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("/create")
    public long createUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        return userFacade.createUser(registerUserDTO);
    }

    @GetMapping("/get")
    public UserDTO getUser(@RequestParam Long userId) {
        return userFacade.getUserById(userId);
    }

    @PutMapping("/update")
    public UserDTO updateUser(@RequestParam Long userId, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return userFacade.updateUserById(userId, updateUserDTO);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam Long userId) {
        userFacade.deleteUserById(userId);
    }

}
