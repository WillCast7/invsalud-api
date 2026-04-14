package com.aurealab.service.impl;

import com.aurealab.service.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    /*@Autowired
    private CustomerRepository customerRepository;

    @Autowired
    CustomerDataViewRepository customerDataViewRepository;

    //@Transactional("sapTrxManager")
    public ResponseEntity<APIResponseDTO<Set<CustomerTableEntity>>> getCustomersWithManualPagination(int rows, int page, String searchValue, String selectedFilter) {
        try {
            FilterType filterType = FilterType.fromString(selectedFilter);
            Set<CustomerTableEntity> customerList = getCustomerList(filterType, page, rows, searchValue);
            long totalRecords = getTotalRecords(filterType, searchValue);

            if (customerList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        APIResponseDTO.failure(constants.messages.noData, constants.descriptions.empty)
                );
            }

            PageableResponseDTO pageableResponse = new PageableResponseDTO(page, rows, totalRecords);

            return ResponseEntity.ok(
                    APIResponseDTO.withPageable(customerList,constants.success.findedSuccess,pageableResponse)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure(constants.messages.error, e.getMessage())
            );
        }
    }

    private Set<CustomerTableEntity> getCustomerList(FilterType filterType, int page, int rows, String searchValue) {
        return switch (filterType) {
            case ALL -> customerRepository.findList(page + 1, rows, searchValue);
            case COMPLETED -> customerRepository.findListCompleted(page + 1, rows, searchValue);
            case INITIALIZED -> customerRepository.findListInitialized(page + 1, rows, searchValue);
            case NOT_INITIALIZED -> customerRepository.findListWithOutInit(page + 1, rows, searchValue);
        };
    }

    private long getTotalRecords(FilterType filterType, String searchValue) {
        return switch (filterType) {
            case ALL -> customerRepository.countAllRecords(searchValue);
            case COMPLETED -> customerRepository.countAllRecordsCompleted(searchValue);
            case INITIALIZED -> customerRepository.countAllRecordsInitialized(searchValue);
            case NOT_INITIALIZED -> customerRepository.countAllRecordsWithOutInit(searchValue);
        };
    }

    private enum FilterType {
        ALL("todos"),
        COMPLETED("completos"),
        INITIALIZED("iniciados"),
        NOT_INITIALIZED("sinIniciar");

        private final String value;

        FilterType(String value) {
            this.value = value;
        }

        public static FilterType fromString(String value) {
            for (FilterType type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown filter type: " + value);
        }
    }

    //@Transactional("sapTrxManager")
    public CustomerDataViewEntity getCustomerDataView(String nit){
        if(nit==null){
            return null;
        }
        return customerDataViewRepository.findCustomerSapData(nit);
    }*/
}
