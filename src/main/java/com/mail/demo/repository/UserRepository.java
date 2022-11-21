package com.mail.demo.repository;


import com.mail.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Class UserRepository - класс для основных действий с БД
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Метод findByUsername для поиска пользователя по имени
     * param username - имя пользователя
     * author - Nikita
     **/
    User findByUsername(String username);

    User findUserById(Long id);

    User findUserByEmail(String email);

}
