package com.wealth.demo.service;

import com.wealth.demo.exception.EmailAlreadyExistsException;
import com.wealth.demo.exception.PasswordMismatchException;
import com.wealth.demo.exception.UserAlreadyExistsException;
import com.wealth.demo.exception.UsernameNotFoundException;
import com.wealth.demo.model.dto.UserLoginDTO;
import com.wealth.demo.model.dto.UserRegisterDTO;
import com.wealth.demo.model.entity.User;
import com.wealth.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 建構子
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 驗證用戶的登錄資料 (用於登入邏輯)
     *
     * @param username 用戶名
     * @param password 密碼
     * @return 驗證通過的用戶
     */
    public User validateUser(String username, String password) {
        logger.info("驗證用戶名和密碼: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用戶名不存在！"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("密碼錯誤，用戶: {}", username);
            throw new PasswordMismatchException("密碼錯誤！");
        }

        logger.info("用戶 {} 驗證成功！", username);
        return user;
    }

    /**
     * 用戶註冊
     *
     * @param userRegisterDTO 用戶註冊資料
     */
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        logger.info("開始註冊新用戶: {}", userRegisterDTO.getUsername());

        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(userRegisterDTO.getUsername())) {
            logger.warn("用戶名已存在: {}", userRegisterDTO.getUsername());
            throw new UserAlreadyExistsException("用戶名已存在！");
        }

        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            logger.warn("電子郵件已存在: {}", userRegisterDTO.getEmail());
            throw new EmailAlreadyExistsException("電子郵件已存在！");
        }

        // 新增用戶
        User newUser = new User();
        newUser.setUsername(userRegisterDTO.getUsername());
        newUser.setEmail(userRegisterDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

        userRepository.save(newUser);
        logger.info("用戶 {} 註冊成功！", userRegisterDTO.getUsername());
    }

    /**
     * 根據用戶名查詢用戶
     *
     * @param username 用戶名
     * @return 用戶實體
     */
    public User getUserByUsername(String username) {
        logger.info("查詢用戶名: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用戶名不存在！"));
    }

    /**
     * 根據用戶 ID 查詢用戶
     *
     * @param id 用戶 ID
     * @return 用戶實體 (選填)
     */
    public Optional<User> getUserById(Long id) {
        logger.info("查詢用戶 ID: {}", id);

        return userRepository.findById(id).map(user -> {
            logger.info("用戶查詢成功: {}", user.getUsername());
            return user;
        });
    }

    /**
     * 更新用戶資料
     *
     * @param id          用戶 ID
     * @param updatedUser 更新後的用戶資料
     * @return 更新後的用戶
     */
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        logger.info("更新用戶 ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用戶不存在！"));

        // 更新屬性
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(existingUser);
        logger.info("用戶 ID: {} 更新成功！", id);
        return existingUser;
    }

    /**
     * 刪除用戶
     *
     * @param id 用戶 ID
     */
    @Transactional
    public void deleteUser(Long id) {
        logger.info("刪除用戶 ID: {}", id);

        if (!userRepository.existsById(id)) {
            logger.warn("用戶 ID 不存在: {}", id);
            throw new IllegalArgumentException("用戶不存在！");
        }

        userRepository.deleteById(id);
        logger.info("用戶 ID: {} 已刪除！", id);
    }

    @Transactional
    public void updateProfile(Long userId, String username, String email, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));

        // 驗證當前密碼是否正確
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("當前密碼不正確");
        }

        // 更新用戶名和電子郵件
        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("用戶名已存在");
            }
            user.setUsername(username);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("電子郵件已存在");
            }
            user.setEmail(email);
        }

        // 更新密碼
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }


}
