package com.ecom.user.jpa;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository <UserEntity, Long>{

}