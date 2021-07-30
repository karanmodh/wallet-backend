package com.navi.wallet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {
    public static Transaction credit(int amount, String remark) {
        return new Transaction(TransactionType.CREDIT, amount, remark);
    }

    public enum TransactionType{
        CREDIT, DEBIT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    @JsonBackReference
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column
    int amount;

    @Column(nullable = true)
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

    public Transaction() {
    }

    public Transaction(TransactionType type, int amount, String remark) {
        this.type = type;
        this.amount = amount;
        this.timestamp = new Date();
        this.remark = remark;
    }

    public Transaction(Integer id, Wallet wallet, TransactionType type, String remark) {
        this.id = id;
        this.wallet = wallet;
        this.type = type;
        this.timestamp = new Date();
        this.remark = remark;
    }

    public Integer getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public int actualAmount() {
        return TransactionType.CREDIT.equals(type)?amount:-amount;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setTimestamp() { this.timestamp = new Date();}
}
