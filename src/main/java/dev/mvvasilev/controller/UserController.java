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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;

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
    @PreAuthorize("hasAuthority('UPDATE_OTHER_USER')")
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
    @PreAuthorize("hasAuthority('READ_OTHER_USER')")
    public UserDTO getUser(@PathVariable Long userId) {
        return userFacade.getUserById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('UPDATE_OTHER_USER')")
    public UserDTO updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return userFacade.updateUserById(userId, updateUserDTO);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('DELETE_OTHER_USER')")
    public void deleteUser(@PathVariable Long userId) {
        userFacade.deleteUserById(userId);
    }

    @GetMapping("/self")
    @PreAuthorize("hasAuthority('READ_SELF')")
    public UserDTO getSelf(HttpServletRequest request) {
        return userFacade.getUserFromRequest(request);
    }

    @PutMapping("/self")
    @PreAuthorize("hasAuthority('UPDATE_SELF')")
    public UserDTO updateSelf(HttpServletRequest request, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return userFacade.updateUserFromRequest(request, updateUserDTO);
    }

    @DeleteMapping("/self")
    @PreAuthorize("hasAuthority('DELETE_SELF')")
    public void deleteSelf(HttpServletRequest request) {
        userFacade.deleteUserFromRequest(request);
    }

}
