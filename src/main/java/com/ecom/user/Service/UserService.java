package com.ecom.user.Service;

import com.ecom.user.dto.UserDto;
import com.ecom.user.jpa.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
