package org.sfn.ebankingbackend.repositories;

import org.sfn.ebankingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
}
