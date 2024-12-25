package com.wealth.demo.repository;

import com.wealth.demo.model.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用戶數據庫操作接口，提供用戶查詢與重複性檢查功能。
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根據用戶名查找用戶。
     *
     * @param username 用戶名
     * @return 用戶對象的 Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 檢查用戶名是否已存在。
     *
     * @param username 用戶名
     * @return 若存在返回 true，否則返回 false
     */
    boolean existsByUsername(String username);

    /**
     * 根據電子郵件查找用戶。
     *
     * @param email 電子郵件
     * @return 用戶對象的 Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * 檢查電子郵件是否已存在。
     *
     * @param email 電子郵件
     * @return 若存在返回 true，否則返回 false
     */
    boolean existsByEmail(String email);

    Optional<User> findByUsernameAndEmail(String username, String email);
}