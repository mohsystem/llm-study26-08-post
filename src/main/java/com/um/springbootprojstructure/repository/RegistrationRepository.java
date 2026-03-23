package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

public interface RegistrationRepository extends Repository<UserAccount, java.util.UUID> {

    @PreAuthorize("permitAll()")
    @Query("select (count(u) > 0) from UserAccount u where lower(u.username) = lower(:username)")
    boolean usernameExists(@Param("username") String username);

    @PreAuthorize("permitAll()")
    @Query("select (count(u) > 0) from UserAccount u where lower(u.email) = lower(:email)")
    boolean emailExists(@Param("email") String email);
}
