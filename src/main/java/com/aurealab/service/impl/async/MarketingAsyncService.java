package com.aurealab.service.impl.async;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Autor: William Castaño ;)
 * Fecha: 2/05/2025
 * Descripción:
 */

@Service
public class MarketingAsyncService {
    //@Autowired
    //MarketingCustomerRepository marketingCustomerRepository;

//    @Async("taskExecutor")
//    public CompletableFuture<Set<MarketingCustomerTableEntity>> fetchCustomers(int page, int rows, int lineUpId, int categoryId, int subCategoryId, List<String> city, String product, LocalDate dateStart, LocalDate dateEnd, int customerType){
//        return CompletableFuture.completedFuture(
//                marketingCustomerRepository.findList(page, rows, lineUpId, categoryId, subCategoryId, city, product, dateStart, dateEnd, customerType)
//        );
//    }
//
//    @Async("taskExecutor")
//    public CompletableFuture<Long> fetchCountCustomers(int lineUpId, int categoryId, int subCategoryId, List<String> city, String product, LocalDate dateStart, LocalDate dateEnd, int customerType) {
//        return CompletableFuture.completedFuture(
//                marketingCustomerRepository.countAllRecords(lineUpId, categoryId, subCategoryId, city, product, dateStart, dateEnd, customerType)
//        );
//    }
}
