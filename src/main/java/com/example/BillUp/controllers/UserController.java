package com.example.BillUp.controllers;

import com.example.BillUp.dto.residence.ResidenceDropdown;
import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.dto.user.CreateUserRequestDTO;
import com.example.BillUp.dto.user.CreateUserResponseDTO;
import com.example.BillUp.dto.user.UpdateUserRequestDTO;
import com.example.BillUp.dto.user.UserResponseDTO;
import com.example.BillUp.entities.User;
import com.example.BillUp.services.UserService;
import com.example.BillUp.services.ResidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ResidenceService residenceService;
    private final UserService userService;

    /* Create User (ADMIN) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CreateUserResponseDTO createUser(
            @RequestBody CreateUserRequestDTO request,
            Authentication authentication
    ) {

        User currentUser = (User) authentication.getPrincipal();

        return userService.createUser(request, currentUser);
    }

    /* GET All Users (ADMIN)*/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDTO> getAllUsers(Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        return userService.getAllUsers(currentUser);
    }

    /* GET Deleted Users (ADMIN)*/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public List<UserResponseDTO> getDeletedUsers(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return userService.getDeletedUsers(currentUser);
    }

    /* GET Users by ID*/
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','COMPANY')")
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id,
                        Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        return userService.getUserById(id, currentUser);
    }

    /* GET Residences by USER ID*/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}/residences")
    public ResponseEntity<List<ResidenceDropdown>> getUserResidences(@PathVariable Long userId) {

        return ResponseEntity.ok(
                residenceService.getUserResidencesByUserId(userId)
        );
    }

    /* Deleting Users */
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','COMPANY')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id,
                           Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        userService.deleteUser(id, currentUser);
    }

    /* Restoring Users (ADMIN)*/
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/restore")
    public User restoreUser(@PathVariable Long id,
                            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        return userService.restoreUser(id, currentUser);
    }

    /* Update User (ADMIN) */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDTO request,
            Authentication authentication
    ) {

        User currentUser = (User) authentication.getPrincipal();

        return userService.updateUser(id, request, currentUser);
    }
}
