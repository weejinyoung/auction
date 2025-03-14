package com.ourfantasy.auction.user.model;

import com.ourfantasy.auction.config.exception.CustomException;
import com.ourfantasy.auction.config.persistence.BaseTimeEntity;
import com.ourfantasy.auction.config.response.ResponseCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Builder(builderMethodName = "builderWithValidate")
    private User(String nickname, String email) {
        validateCreatingUser(email, nickname);
        this.nickname = nickname;
        this.email = email;
        this.status = UserStatus.ACTIVE;
    }

    private void validateCreatingUser(String email, String nickname) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new CustomException(ResponseCode.INVALID_EMAIL);
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new CustomException(ResponseCode.INVALID_NICKNAME);
        }
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    public boolean isInactive() {
        return this.status == UserStatus.INACTIVE || this.status == UserStatus.SUSPENDED;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}