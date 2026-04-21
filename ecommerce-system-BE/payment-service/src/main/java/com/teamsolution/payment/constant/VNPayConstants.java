package com.teamsolution.payment.constant;

public final class VNPayConstants {

    private VNPayConstants() {}

    // Command
    public static final String COMMAND_PAY = "pay";
    public static final String COMMAND_REFUND = "refund";

    // Currency
    public static final String CURRENCY_VND = "VND";

    // Transaction type
    public static final String TRANSACTION_TYPE_REFUND = "02";

    // Response codes
    public static final String VNP_RESPONSE_CODE = "vnp_ResponseCode";
    public static final String RESPONSE_SUCCESS = "00";
    public static final String RESPONSE_ALREADY_PROCESSED = "02";
    public static final String RESPONSE_INVALID_SIGNATURE = "97";
    public static final String RESPONSE_INVALID_AMOUNT = "04";

    // IPN messages
    public static final String MSG_INVALID_SIGNATURE    = "Invalid signature";
    public static final String MSG_ALREADY_PROCESSED    = "Order already confirmed";
    public static final String MSG_INVALID_AMOUNT       = "Invalid amount";
    public static final String MSG_CONFIRM_SUCCESS      = "Confirm Success";

    // Params keys
    public static final String VNP_VERSION = "vnp_Version";
    public static final String VNP_COMMAND = "vnp_Command";
    public static final String VNP_TMN_CODE = "vnp_TmnCode";
    public static final String VNP_AMOUNT = "vnp_Amount";
    public static final String VNP_CURRENCY = "vnp_CurrCode";
    public static final String VNP_TXN_REF = "vnp_TxnRef";
    public static final String VNP_ORDER_INFO = "vnp_OrderInfo";
    public static final String VNP_ORDER_TYPE = "vnp_OrderType";
    public static final String VNP_LOCALE = "vnp_Locale";
    public static final String VNP_RETURN_URL = "vnp_ReturnUrl";
    public static final String VNP_IPN_URL = "vnp_IpnUrl";
    public static final String VNP_IP_ADDR = "vnp_IpAddr";
    public static final String VNP_CREATE_DATE = "vnp_CreateDate";

    // Refund keys
    public static final String VNP_REQUEST_ID = "vnp_RequestId";
    public static final String VNP_TRANSACTION_TYPE = "vnp_TransactionType";
    public static final String VNP_CREATE_BY = "vnp_CreateBy";
    public static final String VNP_SECURE_HASH = "vnp_SecureHash";
    public static final String VNP_SECURE_HASH_TYPE = "vnp_SecureHashType";

    // format
    public static final String DATE_PATTERN = "yyyyMMddHHmmss";

    public static final String SYSTEM = "system";
    public static final String LOCAL_IP = "127.0.0.1";
    public static final String REFUND_FORMAT = "REFUND_ORDER=%s|AMOUNT=%s";

    public static final String IPN_RESPONSE_FORMAT = "RspCode=%s&Message=%s";
}