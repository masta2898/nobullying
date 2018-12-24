package ua.sumy.stpp.nobullying.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.sumy.stpp.nobullying.model.User;
import ua.sumy.stpp.nobullying.service.error.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private EntityManager entityManager;
    private UserService userService;

    @BeforeEach
    void setup() {
        entityManager = mock(EntityManager.class);
        userService = new UserService();
        userService.setEntityManager(entityManager);
    }

    @Test
    void getNotExistingUser() {
        when(entityManager.find(User.class, 1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void getExistingUser() {
        User testUser = new User("user", "qwerty", "Simple", "User");
        testUser.setId(1L);

        when(entityManager.find(User.class, 1L)).thenReturn(testUser);

        User user = assertDoesNotThrow(() -> userService.getById(1L));

        assertNotNull(user);
        assertEquals(testUser, user);
    }

    @Test
    void verifyNullCredentials() {
        assertThrows(BadParametersException.class, () -> userService.verify(null, "qwerty"));
        assertThrows(BadParametersException.class, () -> userService.verify("login", null));
        assertThrows(BadParametersException.class, () -> userService.verify(null, null));
    }

    @Test
    void verifyBadCredentials() {
        String login = "admin";
        String password = "qwerty";
        String queryText = "SELECT u FROM User u WHERE u.login = :login AND u.password = :password";
        Query query = mock(Query.class);

        when(entityManager.createQuery(queryText)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        assertFalse(assertDoesNotThrow(() -> userService.verify(login, password)));

        verify(entityManager).createNamedQuery(queryText);
        verify(query).setParameter("login", login);
        verify(query).setParameter("password", password);
    }

    @Test
    void verifyGoodCredentials() {
        String login = "admin";
        String password = "qwerty";
        String queryText = "SELECT u FROM User u WHERE u.login = :login AND u.password = :password";
        User user = new User("admin", "qwerty", "Simple", "User");
        user.setId(1L);
        Query query = mock(TypedQuery.class);

        when(entityManager.createQuery(queryText)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(user);

        assertTrue(assertDoesNotThrow(() -> userService.verify(login, password)));

        verify(entityManager).createNamedQuery(queryText);
        verify(query).setParameter("login", login);
        verify(query).setParameter("password", password);
        verify(query).getSingleResult();
    }

    @Test
    void registerUserWithNullCredentials() {
        // only simple tests, without complex combinations.
        assertThrows(BadParametersException.class, () -> userService.registerUser(null, "qwerty", "Max", "No"));
        assertThrows(BadParametersException.class, () -> userService.registerUser("admin", null, "Max", "No"));
        assertThrows(BadParametersException.class, () -> userService.registerUser("admin", "qwerty", null, "No"));
        assertThrows(BadParametersException.class, () -> userService.registerUser("admin", "qwerty", "Max", null));
        assertThrows(BadParametersException.class, () -> userService.registerUser(null, null, null, null));
    }

    @Test
    void registerUserWithExistingLogin() {
        String login = "admin";
        String password = "qwerty";
        String name = "Simple";
        String surname = "User";
        String queryText = "SELECT u FROM User u WHERE u.login = :login";
        User user = new User("user", "qwerty", "Simple", "User");
        user.setId(1L);
        Query query = mock(Query.class);

        when(entityManager.createQuery(queryText)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(user);

        assertThrows(UserIsAlreadyRegisteredException.class, () -> userService.registerUser(login, password, name,
                surname));

        verify(entityManager).createNamedQuery(queryText);
        verify(query).setParameter("login", login);
        verify(query).getSingleResult();
    }

    @Test
    void registerNewUser() {
        String login = "admin";
        String password = "qwerty";
        String name = "Simple";
        String surname = "User";
        String queryText = "SELECT u FROM User u WHERE u.login = :login";
        Query query = mock(Query.class);

        when(entityManager.createQuery(queryText)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        assertDoesNotThrow(() -> userService.registerUser(login, password, name, surname));

        verify(entityManager).createNamedQuery(queryText);
        verify(query).setParameter("login", login);
        verify(query).getSingleResult();
    }

    @Test
    void getAllUsersFromEmptyDatabase() {
        Query query = mock(Query.class);

        when(query.getResultList()).thenReturn(null);
        when(entityManager.createNamedQuery("fetchAllUsers")).thenReturn(query);

        List<User> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertTrue(allUsers.isEmpty());
    }

    @Test
    void getAllUsers() {
        List<User> testUsers = new LinkedList<>();
        testUsers.add(new User("admin", "qwerty", "Administrator", "Main"));
        testUsers.add(new User("user", "qwerty", "Simple", "User"));
        testUsers.add(new User("guest", "qwerty", "Just", "Guest"));

        Query query = mock(Query.class);

        when(query.getResultList()).thenReturn(testUsers);
        when(entityManager.createNamedQuery("fetchAllUsers")).thenReturn(query);

        List<User> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(testUsers, allUsers);
    }

    @Test
    void isNotExistingUserAdmin() {
        when(entityManager.find(User.class, 1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.isUserAdmin(1L));
    }

    @Test
    void isUserAdmin() {
        User user = new User("user", "qwerty", "Simple", "User");
        user.setId(1L);

        when(entityManager.find(User.class, 1L)).thenReturn(user);

        boolean isAdmin = assertDoesNotThrow(() -> userService.isUserAdmin(1L));
        assertFalse(isAdmin);
    }

    @Test
    void isAdminUserAdmin() {
        User user = new User("admin", "qwerty", "Administrator", "Main");
        user.setId(1L);
        user.setAdmin(true);

        when(entityManager.find(User.class, 1L)).thenReturn(user);

        boolean isAdmin = assertDoesNotThrow(() -> userService.isUserAdmin(1L));
        assertTrue(isAdmin);
    }

    @Test
    void promoteNotExistingUser() {
        when(entityManager.find(User.class, 1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.promoteUser(1L));
    }

    @Test
    void promoteAdminUser() {
        User admin = new User("admin", "qwerty", "Administrator", "Main");
        admin.setId(1L);
        admin.setAdmin(true);

        when(entityManager.find(User.class, 1L)).thenReturn(admin);

        assertThrows(UserIsAlreadyAdminException.class, () -> userService.promoteUser(1L));
    }

    @Test
    void promoteUser() {
        User user = new User("user", "qwerty", "Simple", "User");
        user.setId(1L);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(User.class, 1L)).thenReturn(user);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertThrows(UserIsNotAdminException.class, () -> userService.promoteUser(1L));
        assertTrue(user.isAdmin());

        verify(entityTransaction).begin();
        verify(entityManager).persist(user);
        verify(entityTransaction).commit();
    }

    @Test
    void degradeNotExistingUser() {
        when(entityManager.find(User.class, 1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.degradeUser(1L));
    }

    @Test
    void degradeUser() {
        User user = new User("user", "qwerty", "Simple", "User");
        user.setId(1L);

        when(entityManager.find(User.class, 1L)).thenReturn(user);

        assertThrows(UserIsNotAdminException.class, () -> userService.degradeUser(1L));
    }

    @Test
    void degradeAdminUser() {
        User admin = new User("admin", "qwerty", "Administrator", "Main");
        admin.setId(1L);
        admin.setAdmin(true);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(User.class, 1L)).thenReturn(admin);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> userService.degradeUser(1L));
        assertFalse(admin.isAdmin());

        verify(entityTransaction).begin();
        verify(entityManager).persist(admin);
        verify(entityTransaction).commit();
    }

    @Test
    void deleteNotExistingUser() {
        when(entityManager.find(User.class, 1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void deleteUser() {
        User user = new User("user", "qwerty", "Simple", "User");
        user.setId(1L);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(User.class, 1L)).thenReturn(user);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> userService.degradeUser(1L));

        verify(entityTransaction).begin();
        verify(entityManager).remove(entityManager.merge(user));
        verify(entityTransaction).commit();
    }
}