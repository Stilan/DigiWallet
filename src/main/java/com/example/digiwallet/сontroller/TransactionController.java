package com.example.digiwallet.сontroller;

import com.example.digiwallet.dto.TransactionDto;
import com.example.digiwallet.dto.WalletDto;
import com.example.digiwallet.model.Wallet;
import com.example.digiwallet.servise.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TransactionController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody WalletDto dto) {
        Wallet wallet = walletService.save(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(wallet);
    }

    @PostMapping("/wallet")
    public ResponseEntity<?> updateBalance(@Valid @RequestBody TransactionDto dto) {
        WalletDto result = walletService.update(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/wallets/{id}")
    public ResponseEntity<?> getBalance(@PathVariable UUID id) {
        Wallet wallet = walletService.getById(id);
        if (wallet == null) {
            String message = "Wallet with id " + id + " not found";
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(message);
        }
        return ResponseEntity.ok(walletService.balance(id));
    }

}