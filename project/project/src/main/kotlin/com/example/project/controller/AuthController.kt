package com.example.project.controller

import com.example.project.database.resources.AuthRequestDto
import com.example.project.database.resources.RefreshRequestDto
import com.example.project.database.security.AuthService
import com.example.project.database.security.TokenPair
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController (
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@RequestBody body: AuthRequestDto) {
        authService.register(body.email, body.password)
    }

    @PostMapping("/login")
    fun login(@RequestBody body: AuthRequestDto): TokenPair {
        return authService.login(body.email, body.password)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody body: RefreshRequestDto): TokenPair  {
        return authService.refresh(body.refreshToken)
    }
}