package org.sfn.ebankingbackend.services;


import org.sfn.ebankingbackend.dtos.CustomerDto;
import org.sfn.ebankingbackend.entities.*;
import org.sfn.ebankingbackend.enums.OperationType;
import org.sfn.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.sfn.ebankingbackend.exceptions.BankAccountNotFountException;
import org.sfn.ebankingbackend.exceptions.CustomerNotFountException;
import org.sfn.ebankingbackend.mappers.CustomerMapper;
import org.sfn.ebankingbackend.repositories.AccountOperationRepository;
import org.sfn.ebankingbackend.repositories.BankAccountRepository;
import org.sfn.ebankingbackend.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BankAccountServiceImpl  implements  BankAccountService{
    private CustomerRepository customerRepository ;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private CustomerMapper customerMapper ;

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    BankAccountServiceImpl(CustomerRepository customerRepository,
                           BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository,
                           CustomerMapper customerMapper){
        this.customerRepository = customerRepository ;
        this.bankAccountRepository = bankAccountRepository ;
        this.accountOperationRepository = accountOperationRepository;
        this.customerMapper = customerMapper;
    }
    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("Saving a new Customer ...");
        return this.customerRepository.save(customer);
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance, Long customerId, double overDraft) throws CustomerNotFountException {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if(customer == null){
                throw new CustomerNotFountException("Customer not found !!");
            }
            CurrentAccount bankAccount = new CurrentAccount();
            bankAccount.setCreationDate(new Date());
            bankAccount.setBalance(initialBalance);
            bankAccount.setCustomer(customer);
            bankAccount.setOverDraft(overDraft);
            return bankAccountRepository.save(bankAccount);
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, Long customerId, double interestRate) throws CustomerNotFountException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null){
            throw new CustomerNotFountException("Customer not found !!");
        }
        SavingAccount bankAccount = new SavingAccount();
        bankAccount.setCreationDate(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCustomer(customer);
        bankAccount.setInterestedRate(interestRate);
        return bankAccountRepository.save(bankAccount);
    }


    @Override
    public List<CustomerDto> customers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDto> collect = customers.stream().map(cust -> customerMapper.fromCustomer(cust)).collect(Collectors.toList());
        return  collect ;
    }

    @Override
    public BankAccount getAccont(Long id) throws BankAccountNotFountException {
         BankAccount bankAccount = bankAccountRepository.findById(id)
                 .orElseThrow(()-> new BankAccountNotFountException("Bank Account not found !!"));
        return bankAccount;
    }

    @Override
    public void debit(Long accountId, double amount, String description) throws BankAccountNotFountException, BalanceNotSufficientException {
        BankAccount bankAccount  = this.getAccont(accountId);
        if(bankAccount.getBalance() < amount)
            throw  new BalanceNotSufficientException("Balance not Sufficient !!");
        AccountOperation accountOperation  = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(Long accountId, double amount, String description) throws BankAccountNotFountException {
        BankAccount bankAccount  = this.getAccont(accountId);
        AccountOperation accountOperation  = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(Long accountIdSource, Long accountIdDestination, double amount) throws BankAccountNotFountException, BalanceNotSufficientException {
         this.debit(accountIdSource,amount,"Transfert to "+ accountIdDestination);
         this.credit(accountIdDestination,amount,"Transfert from "+accountIdSource);
    }

    @Override
    public List<BankAccount> accounts() {
        return bankAccountRepository.findAll();
    }


}
