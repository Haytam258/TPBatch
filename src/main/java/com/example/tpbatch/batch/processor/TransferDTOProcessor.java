package com.example.tpbatch.batch.processor;

import com.example.tpbatch.dto.TransferDto;
import org.springframework.batch.item.ItemProcessor;

public class TransferDTOProcessor implements ItemProcessor<TransferDto, TransferDto> {
    @Override
    public TransferDto process(TransferDto transferDto) throws Exception {
        return transferDto;
    }
}
