package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.RoleName;
import org.test.restaurant_service.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByTelegramUserEntityChatId(Long chatId);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName IN (:roles)")
    List<User> findUsersByRoles(@Param("roles") List<RoleName> roles);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN TelegramUserEntity tu ON u.telegramUserEntity.id = tu.id " +
            "WHERE LOWER(tu.firstname) LIKE LOWER(CONCAT('%', :query, '%')) " +
            " OR " +
            "LOWER(tu.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> search(String query, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.roleName IN (:roleNames)")
    List<User> findAllStaffUsers(@Param("roleNames") List<RoleName> roleNames);
}
