package com.example.digiwallet.mapper;

import com.example.digiwallet.dto.WalletDto;
import com.example.digiwallet.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper extends AbstractMapper<WalletDto, Wallet>{
}
