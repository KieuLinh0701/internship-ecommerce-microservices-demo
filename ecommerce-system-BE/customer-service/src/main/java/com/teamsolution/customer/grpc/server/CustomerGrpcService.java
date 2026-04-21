package com.teamsolution.customer.grpc.server;

import com.google.protobuf.Empty;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.customer.entity.Address;
import com.teamsolution.customer.entity.Customer;
import com.teamsolution.customer.exception.ErrorCode;
import com.teamsolution.customer.service.AddressService;
import com.teamsolution.customer.service.CustomerService;
import com.teamsolution.proto.grpc.customer.CreateCustomerRequest;
import com.teamsolution.proto.grpc.customer.CustomerServiceGrpc;
import com.teamsolution.proto.grpc.customer.GetAddressByCustomerIdAndAddressIdRequest;
import com.teamsolution.proto.grpc.customer.GetAddressByCustomerIdAndAddressIdResponse;
import com.teamsolution.proto.grpc.customer.GetCustomerIdByAccountIdRequest;
import com.teamsolution.proto.grpc.customer.GetCustomerIdByAccountIdResponse;
import com.teamsolution.proto.grpc.customer.GetCustomerNameByCustomerIdRequest;
import com.teamsolution.proto.grpc.customer.GetCustomerNameByCustomerIdResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CustomerGrpcService
        extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerService customerService;
    private final AddressService addressService;

    @Override
    @Transactional
    public void createCustomer(CreateCustomerRequest request, StreamObserver<Empty> observer) {
        UUID accountId = UuidUtils.parse(request.getAccountId());

        if (customerService.existsByAccountId(accountId)) {
            throw new AppException(ErrorCode.CUSTOMER_ACCOUNT_ALREADY_EXISTS);
        }

        customerService.createCustomer(
                accountId, request.getFullName(), request.getPhone(), request.getAvatarUrl());

        observer.onNext(Empty.getDefaultInstance());
        observer.onCompleted();
    }

    @Override
    @Transactional
    public void getCustomerIdByAccountId(
            GetCustomerIdByAccountIdRequest request,
            StreamObserver<GetCustomerIdByAccountIdResponse> observer) {

        log.info("Getting customer by accountId: {}", request.getAccountId());


        UUID accountId = UuidUtils.parse(request.getAccountId());

        Customer customer = customerService.findByAccountId(accountId);

        GetCustomerIdByAccountIdResponse response =
                GetCustomerIdByAccountIdResponse.newBuilder()
                        .setCustomerId(customer.getId()
                                .toString())
                        .build();

        observer.onNext(response);
        observer.onCompleted();
    }

    @Override
    @Transactional
    public void getAddressByCustomerIdAndAddressId(
            GetAddressByCustomerIdAndAddressIdRequest request,
            StreamObserver<GetAddressByCustomerIdAndAddressIdResponse> observer) {

        UUID addressId = UuidUtils.parse(request.getAddressId());
        UUID customerId = UuidUtils.parse(request.getCustomerId());

        Address address = addressService.findByIdAndCustomerId(addressId, customerId);

        GetAddressByCustomerIdAndAddressIdResponse response =
                GetAddressByCustomerIdAndAddressIdResponse.newBuilder()
                        .setId(address.getId()
                                .toString())
                        .setName(address.getName())
                        .setPhone(address.getPhone())
                        .setCityCode(address.getCityCode())
                        .setCityName(address.getCityName())
                        .setWardCode(address.getWardCode())
                        .setWardName(address.getWardName())
                        .setDetail(address.getDetail())
                        .build();

        observer.onNext(response);
        observer.onCompleted();
    }

    @Override
    @Transactional
    public void getCustomerNameByCustomerId(
            GetCustomerNameByCustomerIdRequest request,
            StreamObserver<GetCustomerNameByCustomerIdResponse> observer) {

        UUID customerId = UuidUtils.parse(request.getCustomerId());

        Customer customer = customerService.findById(customerId);

        GetCustomerNameByCustomerIdResponse response =
                GetCustomerNameByCustomerIdResponse.newBuilder()
                        .setName(customer.getFullName())
                        .build();

        observer.onNext(response);
        observer.onCompleted();
    }
}
