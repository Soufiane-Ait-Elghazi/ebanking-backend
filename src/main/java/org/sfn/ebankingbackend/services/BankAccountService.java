package org.sfn.ebankingbackend.services;

import org.sfn.ebankingbackend.entities.BankAccount;
import org.sfn.ebankingbackend.entities.CurrentAccount;
import org.sfn.ebankingbackend.entities.Customer;
import org.sfn.ebankingbackend.entities.SavingAccount;
import org.sfn.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.sfn.ebankingbackend.exceptions.BankAccountNotFountException;
import org.sfn.ebankingbackend.exceptions.CustomerNotFountException;

import java.util.List;

public interface BankAccountService {


    Customer saveCustomer(Customer customer);

    public CurrentAccount  saveCurrentBankAccount(double initialBalance , Long customerId,double overDraft) throws CustomerNotFountException;
    public SavingAccount saveSavingBankAccount(double initialBalance , Long customerId, double interestRate ) throws CustomerNotFountException;
    public List<Customer> customers();
    public List<BankAccount> accounts();
    public BankAccount getAccont(Long id) throws BankAccountNotFountException;
    public void debit(Long accountId,double amount,String description) throws BankAccountNotFountException, BalanceNotSufficientException;
    public void credit(Long accountId,double amount,String description) throws BankAccountNotFountException, BalanceNotSufficientException;
    public void transfer(Long accountIdSource,Long accountIdDestination,double amount) throws BankAccountNotFountException, BalanceNotSufficientException;

}
