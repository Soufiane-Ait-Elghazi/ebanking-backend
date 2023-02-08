package org.sfn.ebankingbackend.exceptions;

public class BankAccountNotFountException extends Exception{
    public BankAccountNotFountException(String message) {
        super(message);
    }
}
