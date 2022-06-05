package com.ecom.user.Service;

import com.ecom.user.dto.UserDto;
import com.ecom.user.jpa.UserEntity;
import com.ecom.user.jpa.UserRepository;
import com.netflix.discovery.converters.Auto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    BCryptPasswordEncoder pwdEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder pwdEncoder){
        this.userRepository = userRepository;
        this.pwdEncoder = pwdEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);


        // userDto 로 변환
        return mapper.map(userEntity, UserDto.class);
    }
}
