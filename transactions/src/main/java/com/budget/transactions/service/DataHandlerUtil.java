package com.budget.transactions.service;

import com.budget.transactions.model.FetchDashDTO;
import com.budget.transactions.model.TransactionDTO;
import com.budget.transactions.model.TransactionEntity;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DataHandlerUtil {

    public DataHandlerUtil (){}

    protected void validateTxn (List<TransactionEntity> data){
        for (TransactionEntity txn : data){
            if (txn.getTimestamp() == null)
                throw new IllegalArgumentException ("Missing date at transaction");
        }
    }

    protected List<FetchDashDTO> generateCacheKeys (List<TransactionEntity> userData){
        List<TransactionEntity> data = new ArrayList<>(userData);
        data.sort(Comparator.comparing(TransactionEntity::getTimestamp));
        List<FetchDashDTO> keys = new ArrayList<>();
        YearMonth month = null;
        for (TransactionEntity txn : data){
            YearMonth txnMonth = YearMonth.from(txn.getTimestamp());
            if (month == null || !month.equals(txnMonth))
                keys.add(new FetchDashDTO(
                        txn.getUsername(),
                        txnMonth.atDay(1).atStartOfDay(),
                        txnMonth.plusMonths(1).atDay(1).atStartOfDay()
                ));
            month = txnMonth;
        }
        return keys;
    }
}
