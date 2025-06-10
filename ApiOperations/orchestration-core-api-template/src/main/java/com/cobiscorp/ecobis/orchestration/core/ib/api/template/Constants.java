package com.cobiscorp.ecobis.orchestration.core.ib.api.template;

public class Constants {
    public static final String CASHI = "CASHI";
    public static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
    public static final String RESPONSE_ERROR_HANDLER = "responseErrorHandler";
    public static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
    public static final String RESPONSE_BV_TRANSACTION = "RESPONSE_BV_TRANSACTION";
    public static final String IS_ONLINE = "isOnline";
    public static final String IS_REENTRY = "flowRty";
    public static final String IS_ERRORS = "isErrors";
    public static final String PROCESS_OPERATION = "processOperation";
    public static final String FINISH_OPERATION = "FINISH_OPERATION";
    public static final String COLUMNS_RETURN = "columnsToReturn";
    public static final String PROCESS_DATE = "transaccionDate";
    public static final String PARAMETERS_VALIDATE = "parametersValidate";
    public static final String COBIS_CONTEXT = "COBIS";
    public static final String CAUSA = "causa";
    public static final String MXN = "MXN";
    public static final String CREDIT_AT_STORE = "CREDIT_AT_STORE";
    public static final String ATM_DEBIT = "ATM_DEBIT";
    public static final String DEBIT_AT_STORE = "DEBIT_AT_STORE";
    public static final String PURCHASE_AT_STORE = "PURCHASE_AT_STORE";
    public static final String PURCHASE_ONLINE = "PURCHASE_ONLINE";
    public static final String REVERSAL_PHYSICAL = "REVERSAL PHYSICAL";
    public static final String REVERSAL_ONLINE = "REVERSAL ONLINE";
    public static final String TRANSACTION_SUCCESS = "TRANSACTION_SUCCESS";
    public static final String P2P_CREDIT = "P2P_CREDIT";
    public static final String P2P_DEBIT = "P2P_DEBIT";
    public static final String CARD_DELIVERY_FEE = "CARD_DELIVERY_FEE";
    public static final String FALSE_CHARGEBACK = "FALSE_CHARGEBACK";
    public static final String FALSE_CHARGEBACK_PENALTY = "FALSE_CHARGEBACK_PENALTY";
    public static final String COMMISSION = "COMMISSION";
    public static final String BONUS = "BONUS";
    public static final String SPEI_CREDIT = "SPEI_CREDIT";
    public static final String SPEI_DEBIT = "SPEI_DEBIT";
    public static final String RETIRO_ATM = "RETIRO_ATM";
    public static final String SPEI_RETURN = "SPEI_RETURN";
    public static final String ACCOUNT_CREDIT = "ACCOUNT_CREDIT";
    public static final String CREDIT_REVERSAL = "CREDIT_REVERSAL";
    
    public static final String AUTHORIZE_PURCHASE = "Authorize Purchase";
    public static final String AUTHORIZE_WITHDRAWAL = "Authorize Withdrawal";
    public static final String AUTHORIZE_DEPOSIT = "Authorize Deposit";
    public static final String AUTHORIZE_PURCHASE_DOCK = "Authorize Purchase Dock";
    public static final String AUTHORIZE_WITHDRAWAL_DOCK = "Authorize Withdrawal Dock";
    public static final String AUTHORIZE_DEPOSIT_DOCK = "Authorize Deposit Dock";
    
    public static final String T_TRN = "@t_trn";
    public static final String CONSIGNMENT_CREDIT = "credit";
    public static final String CONSIGNMENT_UNLOCK = "unlock";
    public static final String CONSIGNMENT_REFUND = "refund";
    public static final String CENTRAL_RESPONSE_OP = "centralResponseOp";
    public static final String LOCAL_RESPONSE = "localResponse";
    public static final String DEFAULT_CURRENCY = "0";
    public static final String DEFAULT_CANAL= "0";
    public static final String SSN_HOST = "SSN_HOST";
    public static final String CENTRAL_ERROR_CODE_OP = "centralErrorCodeOp";
    public static final String CENTRAL_ERROR_MSG_OP = "centralErrorMsgOp";
    public static final String LOCAL_ERROR_CODE = "localErrorCode";
    public static final String LOCAL_ERROR_MSG = "localErrorMsg";

    public static final int DEAULT_ERROR = 50061;
    public static final String DEAULT_ERROR_MSG = "Error in consignment credit operation.";
}
