package com.teamsolution.payment.grpc.server;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.payment.exception.ErrorCode;
import com.teamsolution.payment.service.PaymentGatewayService;
import com.teamsolution.proto.grpc.payment.CreatePaymentRequest;
import com.teamsolution.proto.grpc.payment.CreatePaymentResponse;
import com.teamsolution.proto.grpc.payment.PaymentServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Map;

@GrpcService
@RequiredArgsConstructor
public class PaymentGrpcServer
        extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final Map<String, PaymentGatewayService> paymentGatewayServiceMap;

  @Override
  public void createPayment(CreatePaymentRequest request, StreamObserver<CreatePaymentResponse> observer) {

      PaymentGatewayService service = paymentGatewayServiceMap.get(request.getMethod());

      if (service == null) {
          throw new AppException(ErrorCode.UNSUPPORTED_PAYMENT_METHOD);
      }

      com.teamsolution.payment.dto.request.CreatePaymentRequest
              requestDto = new com.teamsolution.payment.dto.request.CreatePaymentRequest(
              request.getOrderId(),
              request.getAmount(),
              request.getMethod()
      );

      String paymentUrl = service.createPayment(
              requestDto,
              request.getClientIp(),
              UuidUtils.parse(request.getCustomerId()));

      observer.onNext(CreatePaymentResponse.newBuilder()
                      .setPaymentUrl(paymentUrl)
              .build());
      observer.onCompleted();
  }
}
