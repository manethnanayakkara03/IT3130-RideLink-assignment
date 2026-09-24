package com.ridelink.account.service;

import com.ridelink.account.dto.*;
import com.ridelink.account.exception.DuplicateResourceException;
import com.ridelink.account.exception.ResourceNotFoundException;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for AccountService.
 * Primary Owner: Silva L.T.R.D (IT24102723)
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AccountService accountService;

    private User samplePassenger;
    private User sampleDriver;

    @BeforeEach
    void setUp() {
        samplePassenger = new User("John", "Doe", "john.doe@example.com", "0771234567",
                "encodedPassword", Role.PASSENGER, AccountStatus.ACTIVE);
        samplePassenger.setId(1L);
        samplePassenger.setCreatedAt(LocalDateTime.now());
        samplePassenger.setUpdatedAt(LocalDateTime.now());

        sampleDriver = new User("Kamal", "Perera", "kamal.driver@example.com", "0719876543",
                "encodedPassword", Role.DRIVER, AccountStatus.ACTIVE);
        sampleDriver.setId(2L);
        sampleDriver.setCreatedAt(LocalDateTime.now());
        sampleDriver.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully register a passenger")
    void testRegisterPassenger_Success() {
        RegisterPassengerRequest request = new RegisterPassengerRequest(
                "John", "Doe", "john.doe@example.com", "0771234567", "password123"
        );

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(samplePassenger);

        AccountResponse response = accountService.registerPassenger(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(Role.PASSENGER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when registering existing email")
    void testRegisterPassenger_DuplicateEmail() {
        RegisterPassengerRequest request = new RegisterPassengerRequest(
                "John", "Doe", "john.doe@example.com", "0771234567", "password123"
        );

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> accountService.registerPassenger(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully register a driver")
    void testRegisterDriver_Success() {
        RegisterDriverRequest request = new RegisterDriverRequest(
                "Kamal", "Perera", "kamal.driver@example.com", "0719876543", "securePass"
        );

        when(userRepository.existsByEmail("kamal.driver@example.com")).thenReturn(false);
        when(passwordEncoder.encode("securePass")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleDriver);

        AccountResponse response = accountService.registerDriver(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(Role.DRIVER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should authenticate and return JWT token on login")
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("john.doe@example.com", "password123");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(samplePassenger));
        when(tokenProvider.generateToken(auth, samplePassenger)).thenReturn("mocked.jwt.token");

        AuthResponse response = accountService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("john.doe@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Should retrieve account by ID")
    void testGetAccountById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(samplePassenger));

        AccountResponse response = accountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account ID not found")
    void testGetAccountById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(999L));
    }

    @Test
    @DisplayName("Should update account details successfully")
    void testUpdateAccount_Success() {
        AccountUpdateRequest updateRequest = new AccountUpdateRequest("Johnny", "Doer", "0770000000");
        when(userRepository.findById(1L)).thenReturn(Optional.of(samplePassenger));
        when(userRepository.save(any(User.class))).thenReturn(samplePassenger);

        AccountResponse response = accountService.updateAccount(1L, updateRequest);

        assertNotNull(response);
        verify(userRepository, times(1)).save(samplePassenger);
    }

    @Test
    @DisplayName("Should update account status")
    void testUpdateAccountStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(samplePassenger));
        when(userRepository.save(any(User.class))).thenReturn(samplePassenger);

        AccountResponse response = accountService.updateAccountStatus(1L, AccountStatus.SUSPENDED);

        assertNotNull(response);
        assertEquals(AccountStatus.SUSPENDED, samplePassenger.getStatus());
    }

    @Test
    @DisplayName("Should verify account existence correctly for interservice communication")
    void testVerifyAccountExists() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(sampleDriver));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ExistsResponse existsResponse = accountService.verifyAccountExists(2L);
        assertTrue(existsResponse.isExists());
        assertEquals(Role.DRIVER, existsResponse.getRole());

        ExistsResponse notExistsResponse = accountService.verifyAccountExists(999L);
        assertFalse(notExistsResponse.isExists());
    }
}
