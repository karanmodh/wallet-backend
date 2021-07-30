package com.navi.wallet.service;

import com.navi.wallet.domain.Transaction;
import com.navi.wallet.domain.Wallet;
import com.navi.wallet.domain.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(Wallet wallet) {
        int amount = wallet.getBalance();
        wallet.setBalance(0);
        Wallet newWallet = walletRepository.save(wallet);
        Transaction transaction = new Transaction(Transaction.TransactionType.CREDIT, amount, "Created Wallet");
        createTransaction(newWallet.getId(), transaction);
        return newWallet;
    }

    public Optional<Transaction> createTransaction(int walletID, Transaction transaction) {

        return walletRepository.findById(walletID).map(wallet->{
            wallet.addTransaction(transaction);
            walletRepository.save(wallet);
            List<Transaction> transactions = wallet.getTransactions();
            return transactions.get(transactions.size() - 1);
        });
    }

    public Optional<Wallet> getWallet(int walletID) {
        return walletRepository.findById(walletID);
    }

    public Optional<Wallet> removeWallet(int id) {
        walletRepository.deleteById(id);
        return Optional.empty();
    }

    public Iterable<Wallet> allWallets() {
        return walletRepository.findAll();
    }

    public Optional<HashMap<String, Object>> allTransactions(int walletID) {
        return walletRepository.findById(walletID).map(wallet -> {
            return new HashMap<String, Object>() {{
                put("wallet", wallet);
                put("transactions", wallet.getTransactions());
            }};
        });
    }
}
