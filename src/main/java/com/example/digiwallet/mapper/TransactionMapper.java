package com.example.digiwallet.mapper;

import com.example.digiwallet.dto.TransactionDto;
import com.example.digiwallet.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends AbstractMapper<TransactionDto, Wallet> {

}
