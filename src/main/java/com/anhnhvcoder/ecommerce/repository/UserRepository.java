package com.anhnhvcoder.ecommerce.repository;

import com.anhnhvcoder.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    /** Runs on every authenticated request (JWT filter), so roles come in the same query. */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    User findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    /*
     * The role filter is an EXISTS sub-query instead of JOIN + DISTINCT, so there
     * are no duplicate rows to collapse and the count query stays a plain COUNT.
     * Roles for the page are batch-fetched by Hibernate when they are mapped.
     */
    @Query(value = """
            SELECT u FROM User u
            WHERE (:search IS NULL
                    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR u.phone LIKE CONCAT('%', :search, '%'))
              AND (:roleName IS NULL
                    OR EXISTS (SELECT 1 FROM u.roles r WHERE r.roleName = :roleName))
              AND (:active IS NULL OR u.active = :active)
            """,
            countQuery = """
            SELECT COUNT(u) FROM User u
            WHERE (:search IS NULL
                    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR u.phone LIKE CONCAT('%', :search, '%'))
              AND (:roleName IS NULL
                    OR EXISTS (SELECT 1 FROM u.roles r WHERE r.roleName = :roleName))
              AND (:active IS NULL OR u.active = :active)
            """)
    Page<User> searchUsers(@Param("search") String search,
                           @Param("roleName") String roleName,
                           @Param("active") Boolean active,
                           Pageable pageable);

    long countByCreatedAtGreaterThanEqual(LocalDate date);
}
