package com.ecom.user.Service;

import com.ecom.user.client.OrderServiceClient;
import com.ecom.user.dto.UserDto;
import com.ecom.user.jpa.UserEntity;
import com.ecom.user.jpa.UserRepository;
import com.ecom.user.vo.ResponseOrder;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    BCryptPasswordEncoder pwdEncoder;

    Environment env;
    RestTemplate restTemplate;

    OrderServiceClient orderServiceClient;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder pwdEncoder, Environment env, RestTemplate restTemplate, OrderServiceClient orderServiceClient){
        this.userRepository = userRepository;
        this.pwdEncoder = pwdEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null)
            throw new UsernameNotFoundException(username + ": not found");

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(userDto.getPwd( )));

        userRepository.save(userEntity);


        // userDto 로 변환
        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity== null){
            throw new UsernameNotFoundException("user not found");
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        //List<ResponseOrder> orders = new ArrayList<>();

        // Rest-Template Example
//        String orderUrl = String.format(env.getProperty("order_service.url"));
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
//        List<ResponseOrder> orderList = orderListResponse.getBody();

        // Using Feignclient
        /*List<ResponseOrder> orderList = null;
        try{
            orderList = orderServiceClient.getOrders(userId);
        } catch (FeignException ex){
            log.error(ex.getMessage());
        }
        userDto.setOrders(orderList);*/

        // FeignClient Error Decoder 활용하기
        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }
}
