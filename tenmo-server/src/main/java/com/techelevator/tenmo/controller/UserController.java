package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    private final UserDao userDao;

    public UserController (UserDao userDao) { this.userDao = userDao; }


    @GetMapping("/Users")
    public List<User> getUsers(@RequestParam(defaultValue = "") String username) {
        List<User> users = new ArrayList<>();
        if (!username.equals("")) {
            users.add(userDao.getUserByUsername(username));
            return users;
        }
        return userDao.getUsers();
    }

    @GetMapping("/Users/{id}")
    public User getUserById(@PathVariable("user_id") int id){
        return userDao.getUserById(id);
    }




}
