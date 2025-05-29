package com.travelhub.travelhub_api.data.mysql.entity.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_keys")
public class ServiceKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sk_idx")
    private Long skIdx;

    @Column(name = "sk_map_key")
    private String skMapKey;

    @Column(name = "sk_oauth_client_id")
    private String skOauthClientId;

    @Column(name = "sk_oauth_client_key")
    private String skOauthClientKey;
}
