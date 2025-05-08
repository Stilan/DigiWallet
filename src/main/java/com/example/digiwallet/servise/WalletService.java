package com.example.digiwallet.servise;

import com.example.digiwallet.dto.TransactionDto;
import com.example.digiwallet.dto.WalletDto;
import com.example.digiwallet.exception.InsufficientFundsException;
import com.example.digiwallet.mapper.TransactionMapper;
import com.example.digiwallet.mapper.WalletMapper;
import com.example.digiwallet.model.Operation;
import com.example.digiwallet.model.Wallet;
import com.example.digiwallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    public Wallet save(WalletDto walletDto) {
        return walletRepository.save(walletMapper.toEntity(walletDto));
    }

    public Wallet getById(UUID id) {
        return walletRepository.findById(id)
                .orElse(null);
    }

    public WalletDto update(TransactionDto dto) {
        Wallet updateWallet = getById(dto.getWalletId());
        if (updateWallet == null) {
            throw new IllegalArgumentException("Wallet not found with id: " + dto.getWalletId());
        }
        if (dto.getOperationType() == Operation.DEPOSIT)
            updateWallet.setAmount(updateWallet.getAmount() + dto.getAmount());
        else if (updateWallet.getAmount() < dto.getAmount()) {
            throw new InsufficientFundsException(
                    "Balance negative " + updateWallet.getAmount()
            );
        }
        else if (dto.getOperationType() == Operation.WITHDRAW) {
            updateWallet.setAmount(updateWallet.getAmount() - dto.getAmount());
        }
        return walletMapper.toDto(updateWallet);
    }

    public Integer balance(UUID uuid) {
        Wallet balanceWallet = getById(uuid);
        return balanceWallet.getAmount();
    }


}
