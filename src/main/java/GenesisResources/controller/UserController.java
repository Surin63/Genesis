package com.engeto.GenesisResources.controller;


import com.engeto.GenesisResources.model.ApiErrorDTO;
import com.engeto.GenesisResources.model.UserInfoDTO;
import com.engeto.GenesisResources.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserInfoService userInfoService;

    public UserController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody UserInfoDTO userInfoDTO) {
        try {
            UserInfoDTO createdUserInfo = userInfoService.createUser(userInfoDTO);
            return new ResponseEntity<>(createdUserInfo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiErrorDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfoDTO> getUserById(@PathVariable Long id, @RequestParam(name = "detail", defaultValue = "false") boolean detail) {
        UserInfoDTO userInfoById;
        if (detail) {
            userInfoById = userInfoService.getUserByIdDetail(id);
        }else {
            userInfoById = userInfoService.getUserById(id);
        }
        if (userInfoById == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(userInfoById, HttpStatus.OK);
        }

    }
    @GetMapping("/users")
    public ResponseEntity<List<UserInfoDTO>> getAllSortedById(@RequestParam(name = "detail", defaultValue = "false") boolean detail){
        List<UserInfoDTO> usersList;
        if (detail){
            usersList = userInfoService.findAllUsersDetail();
        }else {
            usersList = userInfoService.findAllUsers();
        }
        if (CollectionUtils.isEmpty(usersList)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(usersList, HttpStatus.OK);
        }
    }

    @PutMapping("/user")
    public ResponseEntity<HttpStatus> updateUserById(@RequestBody UserInfoDTO updateUserInfoDTO){
        if(userInfoService.getUserByIdDetail(updateUserInfoDTO.getId()) != null){
            userInfoService.updateUserById(updateUserInfoDTO.getId(), updateUserInfoDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        userInfoService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
