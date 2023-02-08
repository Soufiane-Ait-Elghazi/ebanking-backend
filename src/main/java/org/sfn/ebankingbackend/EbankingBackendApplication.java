package org.sfn.ebankingbackend;

import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.sfn.ebankingbackend.entities.*;
import org.sfn.ebankingbackend.enums.AccountStatus;
import org.sfn.ebankingbackend.enums.OperationType;
import org.sfn.ebankingbackend.exceptions.CustomerNotFountException;
import org.sfn.ebankingbackend.repositories.AccountOperationRepository;
import org.sfn.ebankingbackend.repositories.BankAccountRepository;
import org.sfn.ebankingbackend.repositories.CustomerRepository;
import org.sfn.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Salma","Lamiae","Kamal").forEach(name->{
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.customers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,customer.getId(),9000);
                    bankAccountService.saveSavingBankAccount(Math.random()*90000,customer.getId(),5.5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
    }
   @Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
            return args -> {
                Stream.of("Soufiane","Laila","Karim").forEach(name->{
                    Customer customer = new Customer();
                    customer.setName(name);
                    customer.setEmail(name+"@gmail.com");
                    customerRepository.save(customer);
                });
                customerRepository.findAll().forEach(cust -> {
                    CurrentAccount currentAccount = new CurrentAccount();
                    currentAccount.setBalance(Math.random()*90000);
                    currentAccount.setCreationDate(new Date());
                    currentAccount.setStatus(AccountStatus.CREATED);
                    currentAccount.setCustomer(cust);
                    currentAccount.setOverDraft(9000);
                    bankAccountRepository.save(currentAccount);
                    SavingAccount savingAccount = new SavingAccount();
                    savingAccount.setBalance(Math.random()*90000);
                    savingAccount.setCreationDate(new Date());
                    savingAccount.setStatus(AccountStatus.CREATED);
                    savingAccount.setCustomer(cust);
                    savingAccount.setInterestedRate(5.5);
                    bankAccountRepository.save(savingAccount);
                });
                bankAccountRepository.findAll().forEach(acc->{
                    for(int i = 0 ; i<10 ;i++){
                        AccountOperation accountOperation = new AccountOperation();
                        accountOperation.setOperationDate(new Date());
                        accountOperation.setAmount(Math.random()*12000);
                        accountOperation.setType(Math.random()>0.5? OperationType.CREDIT:OperationType.DEBIT);
                        accountOperation.setBankAccount(acc);
                        accountOperationRepository.save(accountOperation);
                    }
                });
                BankAccount bankAccount =
                        bankAccountRepository.findById((long) 1.0).orElse(null);
                System.out.println("____________________________________________");
                if(bankAccount instanceof  CurrentAccount){
                    System.out.println("Current");
                }else{

                    System.out.println("Saving");
                }
            };
    }


}
