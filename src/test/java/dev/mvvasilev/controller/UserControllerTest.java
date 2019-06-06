package dev.mvvasilev.controller;

import dev.mvvasilev.dto.RegisterUserDTO;
import dev.mvvasilev.dto.UpdateUserDTO;
import dev.mvvasilev.dto.UserDTO;
import dev.mvvasilev.facade.UserFacade;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.Month;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final Long USER_ID = 123L;

    private UserController userController;

    @Mock
    private UserFacade userFacade;

    private RegisterUserDTO registerUserDTO;

    private UpdateUserDTO updateUserDTO;

    private UserDTO expectedUserDTO;

    private UserDTO updatedExpectedDTO;

    @Before
    public void init() {
        userController = new UserController(userFacade);

        updateUserDTO = new UpdateUserDTO();

        expectedUserDTO = new UserDTO();
        updatedExpectedDTO = new UserDTO();

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

        updatedExpectedDTO.setId(USER_ID);
        updatedExpectedDTO.setEmail(updateUserDTO.getEmail());
        updatedExpectedDTO.setFirstName(updateUserDTO.getFirstName());
        updatedExpectedDTO.setLastName(updateUserDTO.getLastName());
        updatedExpectedDTO.setDateOfBirth(updateUserDTO.getDateOfBirth());

        Mockito.when(userFacade.getUserById(USER_ID)).thenReturn(expectedUserDTO);
        Mockito.when(userFacade.updateUserById(USER_ID, updateUserDTO)).thenReturn(updatedExpectedDTO);
    }

    @Test
    public void testRegisterUser() {
        userController.createUser(registerUserDTO);
    }

    @Test
    public void testGetUser() {
        UserDTO userDTO = userController.getUser(USER_ID);

        Assert.assertEquals(userDTO, expectedUserDTO);
    }

    @Test
    public void testUpdateUser() {
        UserDTO userDTO = userController.updateUser(USER_ID, updateUserDTO);

        Assert.assertEquals(userDTO, updatedExpectedDTO);
    }

    @Test
    public void testDeleteUser() {
        userController.deleteUser(USER_ID);
    }

    @Test
    public void testQueryUsers() {
        userController.queryUsers(PageRequest.of(0, 5));
    }
}
