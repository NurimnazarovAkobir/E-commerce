package com.ecommerce.controller;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTRequest {
    private String email;
    private String password;
}
