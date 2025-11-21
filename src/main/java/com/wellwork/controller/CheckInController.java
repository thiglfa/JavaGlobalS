package com.wellwork.controller;

import com.wellwork.dto.CheckInRequestDTO;
import com.wellwork.dto.CheckInResponseDTO;
import com.wellwork.model.entities.GeneratedMessage;
import com.wellwork.service.CheckInService;
import com.wellwork.service.GeneratedMessageService;
import com.wellwork.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    private final CheckInService checkInService;
    private final GeneratedMessageService generatedMessageService;
    private final UserService userService;

    public CheckInController(CheckInService checkInService,
                             GeneratedMessageService generatedMessageService,
                             UserService userService) {
        this.checkInService = checkInService;
        this.generatedMessageService = generatedMessageService;
        this.userService = userService;
    }

    // Create check-in. userId will be taken from authenticated token if not provided
    @PostMapping
    public ResponseEntity<CheckInResponseDTO> create(@AuthenticationPrincipal Jwt jwt,
                                                     @Valid @RequestBody CheckInRequestDTO dto) {
        // prefer subject username from jwt
        String username = jwt.getSubject();
        // find user id from username
        Long userId = userService.findEntityByUsername(username).getId();
        dto.setUserId(userId); // ensure dto has user id
        CheckInResponseDTO created = checkInService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    // list checkins of the authenticated user (paginated)
    @GetMapping
    public ResponseEntity<Page<CheckInResponseDTO>> listMine(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        String username = jwt.getSubject();
        Long userId = userService.findEntityByUsername(username).getId();
        Page<CheckInResponseDTO> page = checkInService.findByUser(userId, pageable);
        return ResponseEntity.ok(page);
    }

    // get check-in by id
    @GetMapping("/{id}")
    public ResponseEntity<CheckInResponseDTO> getById(@PathVariable Long id) {
        CheckInResponseDTO dto = checkInService.toResponseDTO(checkInService.findEntityById(id));
        return ResponseEntity.ok(dto);
    }

    // update notes/mood/energy (partial update)
    @PatchMapping("/{id}")
    public ResponseEntity<CheckInResponseDTO> patch(@PathVariable Long id,
                                                    @Valid @RequestBody CheckInRequestDTO patchDto,
                                                    @AuthenticationPrincipal Jwt jwt) {
        // authorization: ensure owner
        String username = jwt.getSubject();
        Long userId = userService.findEntityByUsername(username).getId();
        CheckInResponseDTO updated = checkInService.updatePartial(id, userId, patchDto);
        return ResponseEntity.ok(updated);
    }

    // Trigger AI generation for a check-in (separate endpoint)
    @PostMapping("/{id}/generate-message")
    public ResponseEntity<GeneratedMessage> generateMessage(@PathVariable Long id,
                                                            @AuthenticationPrincipal Jwt jwt) {
        // authorization: only owner can request generation
        String username = jwt.getSubject();
        Long userId = userService.findEntityByUsername(username).getId();

        // ensure check-in belongs to user
        var checkIn = checkInService.findEntityById(id);
        if (!checkIn.getUser().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        GeneratedMessage generated = generatedMessageService.generateForCheckIn(id);
        return ResponseEntity.status(201).body(generated);
    }
}
