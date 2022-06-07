package com.ecom.user.Service;

import com.ecom.user.dto.UserDto;
import com.ecom.user.jpa.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();


    UserDto getUserDetailsByEmail(String userName);
}
