package com.ecommerce.controller;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTResponse {
    private String jwt;
    private String message;
}
