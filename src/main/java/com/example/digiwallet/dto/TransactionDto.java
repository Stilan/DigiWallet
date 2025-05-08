package com.example.digiwallet.dto;

import com.example.digiwallet.model.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TransactionDto {

   @NotNull(message = "Invalid field name")
   private UUID walletId;
   @NotNull(message = "Invalid field name")
   private Operation operationType;
   @NotNull(message = "Invalid field name")
   private Integer amount;
}
