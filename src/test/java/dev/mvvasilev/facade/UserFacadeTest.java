package dev.mvvasilev.facade;

import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.entity.User;
import dev.mvvasilev.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.Month;

@RunWith(MockitoJUnitRunner.class)
public class UserFacadeTest {

    private static final long USER_ID = 123L;

    private UserFacade userFacade;

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    private RegisterUserDTO registerUserDTO;

    private UpdateUserDTO updateUserDTO;

    private UserDTO expectedUserDTO;

    private UserDTO expectedUpdatedUserDTO;

    private User user;

    private User updatedUser;

    @Before
    public void init() {
        userFacade = new UserFacade(userService, modelMapper);

        updateUserDTO = new UpdateUserDTO();

        expectedUserDTO = new UserDTO();
        expectedUpdatedUserDTO = new UserDTO();

        registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setEmail("fake_email@fake_email.com");
        registerUserDTO.setFirstName("Fake");
        registerUserDTO.setLastName("Name");
        registerUserDTO.setDateOfBirth(LocalDate.of(1997, Month.DECEMBER, 6));
        registerUserDTO.setRawPassword("someRawPassword123asdf");

        updateUserDTO.setEmail("another_fake_email@fake_email.com");
        updateUserDTO.setFirstName("New");
        updateUserDTO.setLastName("Name");
        updateUserDTO.setDateOfBirth(LocalDate.of(1998, Month.AUGUST, 18));

        expectedUserDTO.setId(USER_ID);
        expectedUserDTO.setFirstName("Fake");
        expectedUserDTO.setLastName("Name");
        expectedUserDTO.setEmail("fake_email@fake_email.com");
        expectedUserDTO.setDateOfBirth(LocalDate.of(1997, Month.DECEMBER, 6));

        expectedUpdatedUserDTO.setId(USER_ID);
        expectedUpdatedUserDTO.setEmail(updateUserDTO.getEmail());
        expectedUpdatedUserDTO.setFirstName(updateUserDTO.getFirstName());
        expectedUpdatedUserDTO.setLastName(updateUserDTO.getLastName());
        expectedUpdatedUserDTO.setDateOfBirth(updateUserDTO.getDateOfBirth());

        user = new User();
        user.setId(USER_ID);
        user.setEmail(registerUserDTO.getEmail());
        user.setPasswordHash("a password hash");
        user.setFirstName(registerUserDTO.getFirstName());
        user.setLastName(registerUserDTO.getLastName());
        user.setDateOfBirth(registerUserDTO.getDateOfBirth());

        updatedUser = new User();
        updatedUser.setId(USER_ID);
        updatedUser.setEmail(updateUserDTO.getEmail());
        updatedUser.setFirstName(updateUserDTO.getFirstName());
        updatedUser.setLastName(updateUserDTO.getLastName());
        updatedUser.setDateOfBirth(updateUserDTO.getDateOfBirth());

        Mockito.when(userService.createUser(
                registerUserDTO.getEmail(),
                registerUserDTO.getRawPassword(),
                registerUserDTO.getFirstName(),
                registerUserDTO.getLastName(),
                registerUserDTO.getDateOfBirth()
        )).thenReturn(USER_ID);

        Mockito.when(userService.getUser(USER_ID)).thenReturn(user);

        Mockito.when(userService.updateUser(
                USER_ID,
                updateUserDTO.getEmail(),
                updateUserDTO.getFirstName(),
                updateUserDTO.getLastName(),
                updateUserDTO.getDateOfBirth()
        )).thenReturn(updatedUser);
    }

    @Test
    public void testCreateUser() {
        long userId = userFacade.createUser(registerUserDTO);

        Assert.assertEquals(USER_ID, userId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUser_withNullRegisterUserDTO() {
        userFacade.createUser(null);
    }

    @Test
    public void testGetUserById() {
        UserDTO userDTO = userFacade.getUserById(USER_ID);

        Assert.assertEquals(expectedUserDTO, userDTO);
    }

    @Test
    public void testUpdateUserById() {
        UserDTO userDTO = userFacade.updateUserById(USER_ID, updateUserDTO);

        Assert.assertEquals(expectedUpdatedUserDTO, userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserById_withNullUpdateUserDTO() {
        userFacade.updateUserById(USER_ID, null);
    }

    @Test
    public void testDeleteUserById() {
        userFacade.deleteUserById(USER_ID);
    }
}
