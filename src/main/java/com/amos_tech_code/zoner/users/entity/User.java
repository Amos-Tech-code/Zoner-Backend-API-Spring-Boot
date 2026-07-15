package com.amos_tech_code.zoner.users.entity;

import com.amos_tech_code.zoner.auth.entity.AuthAccount;
import com.amos_tech_code.zoner.auth.entity.EmailVerification;
import com.amos_tech_code.zoner.business.entity.BusinessProfile;
import com.amos_tech_code.zoner.common.entity.BaseEntity;
import com.amos_tech_code.zoner.common.enums.Visibility;
import com.amos_tech_code.zoner.media.entity.Media;
import com.amos_tech_code.zoner.users.enums.AccountStatus;
import com.amos_tech_code.zoner.users.enums.RegistrationStage;
import com.amos_tech_code.zoner.users.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_username", columnList = "username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column
    private String passwordHash;

    @Column(unique = true, length = 50)
    private String username;

    @Column(length = 100)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_picture_media_id")
    private Media profilePicture;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStage registrationStage =
            RegistrationStage.EMAIL_SUBMITTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus =
            AccountStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility =
            Visibility.PUBLIC;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean notificationsEnabled = true;

    @Column(nullable = false)
    private boolean twoFactorEnabled = false;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private BusinessProfile businessProfile;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<AuthAccount> authAccounts = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<EmailVerification> emailVerifications = new HashSet<>();

}