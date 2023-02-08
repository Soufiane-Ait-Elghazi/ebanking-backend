package org.sfn.ebankingbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@DiscriminatorValue(value = "SA")
public class SavingAccount extends BankAccount{
    private double interestedRate;
}
