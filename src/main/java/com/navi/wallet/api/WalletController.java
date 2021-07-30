package com.navi.wallet.api;

import com.navi.wallet.domain.Transaction;
import com.navi.wallet.domain.Wallet;
import com.navi.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/wallets")
public class WalletController {
    final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    @PostMapping("")
    @ResponseBody
    @Valid
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        if(wallet.getBalance()<0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(walletService.createWallet(wallet));
    }

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<Iterable<Wallet>> getWallets() {
        return ResponseEntity.status(HttpStatus.OK).body(walletService.allWallets());
    }

    @DeleteMapping("")
    @ResponseBody
    public ResponseEntity<Integer> deleteWallet(@RequestParam(name = "id") int id) {
        walletService.removeWallet(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @GetMapping("/{walletID}/transactions")
    @ResponseBody
    public ResponseEntity<?> getTransaction(@PathVariable(value="walletID") int walletID){
        return walletService.allTransactions(walletID)
                .map(transactions->{
                    return ResponseEntity.status(HttpStatus.CREATED).body(
                            transactions
                    );
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{walletID}/transactions")
    @ResponseBody
    public ResponseEntity<Transaction> addTransaction(@PathVariable(value="walletID") int walletID,@RequestBody Transaction transaction){
        transaction.setTimestamp();
        return walletService.createTransaction(walletID, transaction)
        .map(addedTransaction->{
            return ResponseEntity.status(HttpStatus.CREATED).body(addedTransaction);
        }).orElse(ResponseEntity.notFound().build());
    }
}



