package com.example.digiwallet.сontroller;

import com.example.digiwallet.dto.TransactionDto;
import com.example.digiwallet.dto.WalletDto;
import com.example.digiwallet.model.Operation;
import com.example.digiwallet.model.Wallet;
import com.example.digiwallet.servise.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void testCreateWallet() throws Exception {
        WalletDto dto = new WalletDto();
        dto.setAmount(0);

        Wallet wallet = new Wallet();
        wallet.setWalletId(UUID.fromString("2a1bf30b-b029-46ad-91da-fedd424d33aa"));
        wallet.setAmount(0);

        when(walletService.save(any(WalletDto.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "amount": 0
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.walletId")
                        .value("2a1bf30b-b029-46ad-91da-fedd424d33aa"))
                .andExpect(jsonPath("$.amount").value(0));
    }



    @Test
    void testUpdateBalance() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setWalletId(UUID.fromString("2a1bf30b-b029-46ad-91da-fedd424d33aa"));
        dto.setOperationType(Operation.DEPOSIT);
        dto.setAmount(100);

        WalletDto walletDto = new WalletDto();
        walletDto.setAmount(100);

        when(walletService.update(any(TransactionDto.class))).thenReturn(walletDto);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "walletId": "2a1bf30b-b029-46ad-91da-fedd424d33aa",
                                "operationType": "DEPOSIT",
                                "amount": 100
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100));
    }

    @Test
    void testGetBalance_WalletFound() throws Exception {
        UUID walletId = UUID.fromString("2a1bf30b-b029-46ad-91da-fedd424d33aa");
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setAmount(250);

        when(walletService.getById(walletId)).thenReturn(wallet);
        when(walletService.balance(walletId)).thenReturn(250);

        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(content().string("250"));
    }
}
