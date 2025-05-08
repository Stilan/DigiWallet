package com.example.digiwallet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletDto {

    @NotNull(message = "Invalid field name")
    private Integer amount;
}
