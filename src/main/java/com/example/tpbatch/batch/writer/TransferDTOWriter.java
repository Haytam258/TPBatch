package com.example.tpbatch.batch.writer;

import com.example.tpbatch.dto.TransferDto;
import com.example.tpbatch.entities.Transaction;
import com.example.tpbatch.repositories.CompteRepository;
import com.example.tpbatch.repositories.TransactionRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import javax.transaction.Transactional;
import java.util.List;

@Import({CompteRepository.class, TransactionRepository.class})
public class TransferDTOWriter implements ItemWriter<TransferDto> {

    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    @Transactional
    public void write(List<? extends TransferDto> chunk) throws Exception {
        for(TransferDto transferDto: chunk){
            Transaction transaction = new Transaction();
            transaction.setId(transferDto.getIdTransaction());
            transaction.setDateTransaction(transferDto.getDateTransation());
            transaction.setMontant(transferDto.getMontant());
            transaction.setCompte(compteRepository.getReferenceById(transferDto.getIdCompte()));

            transactionRepository.saveAndFlush(transaction);
        }
    }
}
