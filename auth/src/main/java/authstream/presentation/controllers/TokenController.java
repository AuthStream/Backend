package authstream.presentation.controllers;

import authstream.application.dtos.TokenDto;
import authstream.application.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<TokenDto> createToken(@RequestBody TokenDto tokenDto) {
        TokenDto createdToken = tokenService.createToken(tokenDto);
        return ResponseEntity.ok(createdToken);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TokenDto> getTokenById(@PathVariable UUID id) {
        TokenDto token = tokenService.getTokenById(id);
        return ResponseEntity.ok(token);
    }
    @GetMapping
    public ResponseEntity<List<TokenDto>> getAllTokens() {
        List<TokenDto> tokens = tokenService.getAllToken();
        return ResponseEntity.ok(tokens);
    }
}