package com.navi.wallet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String name;
    @PositiveOrZero
    @Column
    private int balance;

    @JsonIgnore
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Transaction> transactions = new ArrayList<>();

    public Wallet(){

    }
    public Wallet(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public Wallet(Integer id, String name, int balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Wallet addBalance(int amount){
        balance += amount;
        return this;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id);
    }

    public void addTransaction(Transaction transaction) {
        transaction.setWallet(this);
        transactions.add(transaction);
        addBalance(transaction.actualAmount());
    }

    public List<Transaction> getTransactions() {
        List<Transaction> reverse_transactions = transactions;
        Collections.reverse(reverse_transactions);
        return reverse_transactions;
    }
}
