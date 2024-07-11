package com.konstantin.crypto_wallet.mapper;

import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionResponseDTO;
import com.konstantin.crypto_wallet.model.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TransactionMapper {
    @Mapping(target = "status", constant = "PENDING")
    public abstract Transaction map(TransactionRequestDTO dto);

    public abstract TransactionResponseDTO map(Transaction model);
}
