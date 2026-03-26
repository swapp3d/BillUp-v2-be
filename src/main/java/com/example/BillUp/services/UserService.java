package com.example.BillUp.services;

import com.example.BillUp.dto.user.CreateUserRequestDTO;
import com.example.BillUp.dto.user.CreateUserResponseDTO;
import com.example.BillUp.dto.user.UpdateUserRequestDTO;
import com.example.BillUp.dto.user.UserResponseDTO;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.Role;
import com.example.BillUp.exceptions.EmailAlreadyExistsException;
import com.example.BillUp.exceptions.PhoneNumberAlreadyExistsException;
import com.example.BillUp.repositories.ResidenceRepository;
import com.example.BillUp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ResidenceRepository residenceRepository;

    /* GET Users*/
    public List<UserResponseDTO> getAllUsers(User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can view all users");
        }

        return userRepository.findAllIncludingDeleted()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<UserResponseDTO> getDeletedUsers(User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can view deleted users");
        }

        return userRepository.findAllDeleted()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public User getUserById(Long id, User currentUser) {

        User user = userRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Admin can see anyone
        if (currentUser.getRole() == Role.ADMIN) {
            return user;
        }

        //Otherwise user can only see themselves
        if (!user.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not allowed");
        }

        return user;
    }

    /* Deleting Users */

    public void deleteUser(Long id, User currentUser) {

        //Admin can delete anyone
        if (currentUser.getRole() == Role.ADMIN) {
            userRepository.deleteById(id);
            return;
        }

        //User can delete themselves (not implemented in FE yet)
        if (currentUser.getId().equals(id)) {
            userRepository.deleteById(id);
            return;
        }

        throw new AccessDeniedException("Not allowed");
    }

    /* Restoring Users (ADMIN)*/

    public User restoreUser(Long id, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can restore users");
        }

        User user = userRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDeleted(false);

        // Restore all residences that were soft-deleted along with the user
        List<Residence> residences = residenceRepository.findByUserIdIncludingDeleted(id);
        residences.forEach(r -> r.setDeleted(false));
        residenceRepository.saveAll(residences);

        return userRepository.save(user);
    }

    /* Creating Users (ADMIN)*/

    public CreateUserResponseDTO createUser(CreateUserRequestDTO request, User currentUser) {

        // Only admin can create users
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can create users");
        }

        validateCreateUserRequest(request);
        checkIfUserExists(request);

        User user = buildUserEntity(request);

        User savedUser = userRepository.save(user);

        return CreateUserResponseDTO.builder()
                .id(savedUser.getId())
                .role(savedUser.getRole())
                .name(savedUser.getName())
                .surname(savedUser.getSurname())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .build();

    }

    private User buildUserEntity(CreateUserRequestDTO request) {

        return User.builder()
                .role(request.getRole())
                .name(request.getName())
                .surname(request.getRole() == Role.CLIENT ? request.getSurname() : null)
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    private void validateCreateUserRequest(CreateUserRequestDTO request) {

        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        // CLIENT role requires surname
        if (request.getRole() == Role.CLIENT) {
            if (request.getSurname() == null || request.getSurname().isBlank()) {
                throw new IllegalArgumentException("Surname is required for CLIENT role");
            }
        }
    }

    private void checkIfUserExists(CreateUserRequestDTO request) {

        String email = request.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "User with email " + email + " already exists"
            );
        }

        String phone = request.getPhoneNumber();

        if (userRepository.existsByPhoneNumber(phone)) {
            throw new PhoneNumberAlreadyExistsException(
                    "User with phone number " + phone + " already exists"
            );
        }
    }


    /* Editing Users (ADMIN)*/

    public UserResponseDTO updateUser(Long id,
                                      UpdateUserRequestDTO request,
                                      User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can update users");
        }

        User user = userRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getSurname() != null) {
            user.setSurname(request.getSurname());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        // Change primary residence
        if (request.getPrimaryResidenceId() != null) {

            user.getResidences().forEach(res -> {
                if (res.getId().equals(request.getPrimaryResidenceId())) {
                    res.setPrimary(true);
                } else {
                    res.setPrimary(false);
                }
            });

        }

        User updated = userRepository.save(user);

        return mapToResponse(updated);
    }

    public UserResponseDTO mapToResponse(User user) {
        String primaryAddress = residenceRepository
                .findPrimaryByUserIdIncludingDeleted(user.getId())
                .map(Residence::getFullAddress)
                .orElse("No primary residence");

        return UserResponseDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .primaryAddress(primaryAddress)
                .deleted(user.isDeleted())
                .build();
    }
}
