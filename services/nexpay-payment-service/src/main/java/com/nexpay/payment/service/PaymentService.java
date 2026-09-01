package com.nexpay.payment.service;

import com.nexpay.payment.dto.PaymentRequest;
import com.nexpay.payment.dto.PaymentResponse;
import com.nexpay.payment.entity.Payment;
import com.nexpay.payment.entity.PaymentStatus;
import com.nexpay.payment.exception.PaymentException;
import com.nexpay.payment.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;

    public PaymentService(
            PaymentRepository paymentRepository,
            RestClient restClient) {

        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
    }

    public PaymentResponse makePayment(PaymentRequest request) {

        validatePaymentRequest(request);

        // 1. Check whether sender is connected to receiver
        checkConnection(
                request.getSenderId(),
                request.getReceiverId()
        );

        // 2. Get sender's bank account
        Long senderAccountId =
                getBankAccountId(request.getSenderId());

        // 3. Get receiver's bank account
        Long receiverAccountId =
                getBankAccountId(request.getReceiverId());

        try {

            // 4. Debit sender
            restClient.post()
                    .uri(
                            "http://localhost:8081/api/v1/bank-accounts/"
                                    + senderAccountId
                                    + "/debit?amount="
                                    + request.getAmount()
                    )
                    .retrieve()
                    .toBodilessEntity();

            // 5. Credit receiver
            restClient.post()
                    .uri(
                            "http://localhost:8081/api/v1/bank-accounts/"
                                    + receiverAccountId
                                    + "/credit?amount="
                                    + request.getAmount()
                    )
                    .retrieve()
                    .toBodilessEntity();

            // 6. Create transaction
            createTransaction(request);

            // 7. Save successful payment
            return savePayment(
                    request,
                    PaymentStatus.SUCCESS
            );

        } catch (Exception exception) {

            throw new PaymentException(
                    "Payment processing failed: "
                            + exception.getMessage()
            );
        }
    }

    private void validatePaymentRequest(
            PaymentRequest request) {

        if (request == null) {
            throw new PaymentException(
                    "Payment request cannot be null"
            );
        }

        if (request.getSenderId() == null ||
                request.getReceiverId() == null) {

            throw new PaymentException(
                    "Sender and receiver are required"
            );
        }

        if (request.getSenderId()
                .equals(request.getReceiverId())) {

            throw new PaymentException(
                    "Sender and receiver cannot be the same"
            );
        }

        if (request.getAmount() == null ||
                request.getAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new PaymentException(
                    "Amount must be greater than zero"
            );
        }
    }

    private void checkConnection(
            Long senderId,
            Long receiverId) {

        try {

            Boolean connected = restClient.get()
                    .uri(
                            "http://localhost:8083/connections/user/"
                                    + senderId
                                    + "/connected/"
                                    + receiverId
                    )
                    .retrieve()
                    .body(Boolean.class);

            if (!Boolean.TRUE.equals(connected)) {

                throw new PaymentException(
                        "Sender is not connected to receiver"
                );
            }

        } catch (PaymentException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new PaymentException(
                    "Unable to contact Connection Service"
            );
        }
    }

    private Long getBankAccountId(Long userId) {

        try {

            BankAccountPageResponse response =
                    restClient.get()
                            .uri(
                                    "http://localhost:8081/api/v1/"
                                            + "bank-accounts/user/"
                                            + userId
                            )
                            .retrieve()
                            .body(BankAccountPageResponse.class);

            if (response == null ||
                    response.content == null ||
                    response.content.length == 0) {

                throw new PaymentException(
                        "No bank account found for user: "
                                + userId
                );
            }

            return response.content[0].accountId;

        } catch (PaymentException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new PaymentException(
                    "Unable to contact Bank Account Service"
            );
        }
    }

    private void createTransaction(
            PaymentRequest request) {

        try {

            restClient.post()
                    .uri(
                            "http://localhost:8084/transactions"
                    )
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception exception) {

            throw new PaymentException(
                    "Unable to create transaction"
            );
        }
    }

    private PaymentResponse savePayment(
            PaymentRequest request,
            PaymentStatus status) {

        Payment payment = new Payment();

        payment.setSenderId(
                request.getSenderId()
        );

        payment.setReceiverId(
                request.getReceiverId()
        );

        payment.setAmount(
                request.getAmount()
        );

        payment.setStatus(status);

        payment.setCreatedAt(
                LocalDateTime.now()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        return new PaymentResponse(
                savedPayment.getPaymentId(),
                savedPayment.getSenderId(),
                savedPayment.getReceiverId(),
                savedPayment.getAmount(),
                savedPayment.getStatus(),
                savedPayment.getCreatedAt()
        );
    }

    /*
     * Minimal response structure required from
     * Bank Account Service pagination response.
     */
    private static class BankAccountPageResponse {

        public BankAccountResponse[] content;
    }

    private static class BankAccountResponse {

        public Long accountId;
    }
}