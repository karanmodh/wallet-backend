package com.navi.wallet;


import static org.assertj.core.api.Assertions.assertThat;

import com.navi.wallet.domain.Transaction;
import com.navi.wallet.domain.Wallet;
import com.navi.wallet.domain.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = WalletApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class WalletApplicationTest {

    @Autowired
    WalletRepository walletRepository;

    TestRestTemplate restTemplate = new TestRestTemplate();
    @LocalServerPort
    private int port;

    @Test
    void expectsToCreateWallet() {
        Integer walletId = createWallet();
        assertThat(walletRepository.findById(walletId))
                .hasValue(new Wallet(walletId, "walter", 100));

    }

    @Test
    void expectsToAddTransactionToWallet() {
        Integer walletId = createWallet();

        HttpEntity<Transaction> wallet = new HttpEntity<>(Transaction.credit(50, "Remark here"));

        ResponseEntity<Transaction> response = restTemplate.exchange(
                createURLWithPort("/wallets/" + walletId + "/transactions"), HttpMethod.POST, wallet, Transaction.class);

        Integer transactionId = response.getBody().getId();

        assertThat(walletRepository.findById(walletId)).hasValueSatisfying(
                savedWallet -> {
                    assertThat(savedWallet.getBalance()).isEqualTo(150);
                    assertThat(savedWallet.getTransactions()).hasSize(1).extracting(Transaction::getId).contains(transactionId);
                }
        );

    }

    private Integer createWallet() {
        HttpEntity<Wallet> wallet = new HttpEntity<>(new Wallet("walter", 100));

        ResponseEntity<Wallet> response = restTemplate.exchange(
                createURLWithPort("/wallets"), HttpMethod.POST, wallet, Wallet.class);

        assertThat(response.getBody()).isNotNull();
        return response.getBody().getId();
    }

    private String createURLWithPort(String path) {
        return "http://localhost:" + port + path;
    }
}