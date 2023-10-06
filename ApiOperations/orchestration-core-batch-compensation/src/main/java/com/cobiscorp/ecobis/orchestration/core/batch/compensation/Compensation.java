package com.cobiscorp.ecobis.orchestration.core.batch.compensation;

import java.util.List;

public class Compensation {
    private String FILE_ID;
    private String ISSUER_ID;
    private String CLIENT_ID;
    private String ID_SUBEMISSOR;
    private String BRAND;
    private String FILENAME_BASE2;
    private String FILENAME;
    private Integer SEQUENCE;
    private Integer FILE_NUMBER;
    private Integer TOTAL_FILES;
    private String REFERENCE_DATE;
    private Integer RECORDS_TOTAL;
    private Double RECORDS_AMNT;
    private Integer CREDIT_TOTAL;
    private Double CREDIT_AMNT;
    private Integer DEBIT_TOTAL;
    private Double DEBIT_AMNT;
    private Integer UNKNOWN_TOTAL;
    private Double UNKNOWN_AMNT;
    private Integer REJECTED_TOTAL;
    private Double REJECTED_AMNT;
    private Integer OCCURRENCE_TOTAL;
    private Double OCCURRENCE_AMNT;
    private Integer EXPIRED_TOTAL;
    private List<Content> CONTENT;


    public static class Content {
        private String ID;
        private Integer RECORD_CODE;
        private Transaction TRANSACTION;
        private Clearing CLEARING;

        public String getID() {
			return ID;
		}

		public Integer getRECORD_CODE() {
			return RECORD_CODE;
		}

		public Transaction getTRANSACTION() {
			return TRANSACTION;
		}

		public Clearing getCLEARING() {
			return CLEARING;
		}

		public static class Transaction {
            private String ARN;
            private String ID_CARDBRAND;
            private String EXTERNAL_ID;
            private Integer VERSION;
            private String PAN;
            private String BIN_CARD;
            private String CARD_ID;
            private String PRODUCT_REFERENCE_ID;
            private String AUTHORIZATION;
            private String LOCAL_DATE;
            private String GMT_DATE;
            private Integer INSTALLMENT_NBR;
            private Integer MCC;
            private Integer SOURCE_CURRENCY;
            private Double SOURCE_VALUE;
            private Integer DEST_CURRENCY;
            private Double DEST_VALUE;
            private Double PURCHASE_VALUE;
            private InstallmentData INSTALLMENT_DATA;
            private Double INSTALLMENT_VALUE_1;
            private Double INSTALLMENT_VALUE_N;
            private Double BOARDING_FEE;
            private String MERCHANT;
            private BusinessArrangement BUSINESS_ARRANGEMENT;
            private MerchantData MERCHANT_DATA;
            private Integer ENTRY_MODE;
            private String AUTHORIZATION_DATE;
            private Integer STATUS;
            private String TRANSACTION_QUALIFIER;
            private List<String> CLASSIFICATION;
            private Integer OPERATION_TYPE;
            private String POS_ENTRY_MODE;
            private Double ISSUER_EXCHANGE_RATE;
            private Double CDT_AMOUNT;
            private String PRODUCT_CODE;
            private String TRANSACTION_TYPE_INDICATOR;
            private String REASON_CODE;
            private String UUID;
            private String OPERATION_CODE;
            private String AGENCY;
            private String ACCOUNT_NUMBER;
            private Boolean LATE_PRESENTATION;
            private String ERROR_CODE;
            private CardholderBillingData CARDHOLDER_BILLING_DATA;
            private Double RECEIVED_CHANGE;
			public String getARN() {
				return ARN;
			}
			public String getID_CARDBRAND() {
				return ID_CARDBRAND;
			}
			public String getEXTERNAL_ID() {
				return EXTERNAL_ID;
			}
			public Integer getVERSION() {
				return VERSION;
			}
			public String getPAN() {
				return PAN;
			}
			public String getBIN_CARD() {
				return BIN_CARD;
			}
			public String getCARD_ID() {
				return CARD_ID;
			}
			public String getPRODUCT_REFERENCE_ID() {
				return PRODUCT_REFERENCE_ID;
			}
			public String getAUTHORIZATION() {
				return AUTHORIZATION;
			}
			public String getLOCAL_DATE() {
				return LOCAL_DATE;
			}
			public String getGMT_DATE() {
				return GMT_DATE;
			}
			public Integer getINSTALLMENT_NBR() {
				return INSTALLMENT_NBR;
			}
			public Integer getMCC() {
				return MCC;
			}
			public Integer getSOURCE_CURRENCY() {
				return SOURCE_CURRENCY;
			}
			public Double getSOURCE_VALUE() {
				return SOURCE_VALUE;
			}
			public Integer getDEST_CURRENCY() {
				return DEST_CURRENCY;
			}
			public Double getDEST_VALUE() {
				return DEST_VALUE;
			}
			public Double getPURCHASE_VALUE() {
				return PURCHASE_VALUE;
			}
			public InstallmentData getINSTALLMENT_DATA() {
				return INSTALLMENT_DATA;
			}
			public Double getINSTALLMENT_VALUE_1() {
				return INSTALLMENT_VALUE_1;
			}
			public Double getINSTALLMENT_VALUE_N() {
				return INSTALLMENT_VALUE_N;
			}
			public Double getBOARDING_FEE() {
				return BOARDING_FEE;
			}
			public String getMERCHANT() {
				return MERCHANT;
			}
			public BusinessArrangement getBUSINESS_ARRANGEMENT() {
				return BUSINESS_ARRANGEMENT;
			}
			public MerchantData getMERCHANT_DATA() {
				return MERCHANT_DATA;
			}
			public Integer getENTRY_MODE() {
				return ENTRY_MODE;
			}
			public String getAUTHORIZATION_DATE() {
				return AUTHORIZATION_DATE;
			}
			public Integer getSTATUS() {
				return STATUS;
			}
			public String getTRANSACTION_QUALIFIER() {
				return TRANSACTION_QUALIFIER;
			}
			public List<String> getCLASSIFICATION() {
				return CLASSIFICATION;
			}
			public Integer getOPERATION_TYPE() {
				return OPERATION_TYPE;
			}
			public String getPOS_ENTRY_MODE() {
				return POS_ENTRY_MODE;
			}
			public Double getISSUER_EXCHANGE_RATE() {
				return ISSUER_EXCHANGE_RATE;
			}
			public Double getCDT_AMOUNT() {
				return CDT_AMOUNT;
			}
			public String getPRODUCT_CODE() {
				return PRODUCT_CODE;
			}
			public String getTRANSACTION_TYPE_INDICATOR() {
				return TRANSACTION_TYPE_INDICATOR;
			}
			public String getREASON_CODE() {
				return REASON_CODE;
			}
			public String getUUID() {
				return UUID;
			}
			public String getOPERATION_CODE() {
				return OPERATION_CODE;
			}
			public String getAGENCY() {
				return AGENCY;
			}
			public String getACCOUNT_NUMBER() {
				return ACCOUNT_NUMBER;
			}
			public Boolean isLATE_PRESENTATION() {
				return LATE_PRESENTATION;
			}
			public String getERROR_CODE() {
				return ERROR_CODE;
			}
			public CardholderBillingData getCARDHOLDER_BILLING_DATA() {
				return CARDHOLDER_BILLING_DATA;
			}
			public Double getRECEIVED_CHANGE() {
				return RECEIVED_CHANGE;
			}
            
        }

        public static class Clearing {
            private Integer VERSION;
            private Integer INSTALLMENT;
            private Integer CURRENCY;
            private Double VALUE;
            private Boolean BOARDING_FEE;
            private Double COMMISSION;
            private String INTERCHANGE_FEE_SIGN;
            private String SETTLEMENT_DATE;
            private Boolean IS_INTERNATIONAL;
            private Integer PRESENTATION;
            private PresentationData PRESENTATION_DATA;
            private Integer ACTION_CODE;
            private List<String> REASON_LIST;
            private Integer TOTAL_PARTIAL_TRANSACTION;
            private Boolean FLAG_PARTIAL_SETTLEMENT;
            private Boolean CANCEL;
            private Boolean CONFIRM;
            private Boolean ADD;
            private Boolean CREDIT;
            private Boolean DEBIT;
            
			public Integer getVERSION() {
				return VERSION;
			}
			public Integer getINSTALLMENT() {
				return INSTALLMENT;
			}
			public Integer getCURRENCY() {
				return CURRENCY;
			}
			public Double getVALUE() {
				return VALUE;
			}
			public Boolean isBOARDING_FEE() {
				return BOARDING_FEE;
			}
			public Double getCOMMISSION() {
				return COMMISSION;
			}
			public String getINTERCHANGE_FEE_SIGN() {
				return INTERCHANGE_FEE_SIGN;
			}
			public String getSETTLEMENT_DATE() {
				return SETTLEMENT_DATE;
			}
			public Boolean isIS_INTERNATIONAL() {
				return IS_INTERNATIONAL;
			}
			public Integer getPRESENTATION() {
				return PRESENTATION;
			}
			public PresentationData getPRESENTATION_DATA() {
				return PRESENTATION_DATA;
			}
			public Integer getACTION_CODE() {
				return ACTION_CODE;
			}
			public List<String> getREASON_LIST() {
				return REASON_LIST;
			}
			public Integer getTOTAL_PARTIAL_TRANSACTION() {
				return TOTAL_PARTIAL_TRANSACTION;
			}
			public Boolean isFLAG_PARTIAL_SETTLEMENT() {
				return FLAG_PARTIAL_SETTLEMENT;
			}
			public Boolean isCANCEL() {
				return CANCEL;
			}
			public Boolean isCONFIRM() {
				return CONFIRM;
			}
			public Boolean isADD() {
				return ADD;
			}
			public Boolean isCREDIT() {
				return CREDIT;
			}
			public Boolean isDEBIT() {
				return DEBIT;
			}            
        }

        public static class InstallmentData {
            private String INSTALLMENT_TYPE;
            private Integer GRACE_PERIOD;
            
			public String getINSTALLMENT_TYPE() {
				return INSTALLMENT_TYPE;
			}
			public Integer getGRACE_PERIOD() {
				return GRACE_PERIOD;
			}

        }

        public static class BusinessArrangement {
            private String CARD_ACCEPTOR_TAX_ID;

			public String getCARD_ACCEPTOR_TAX_ID() {
				return CARD_ACCEPTOR_TAX_ID;
			}

        }

        public static class MerchantData {
            private String SERVICE_LOCATION;

			public String getSERVICE_LOCATION() {
				return SERVICE_LOCATION;
			}
        }

        public static class PresentationData {
            private Integer DAY_COUNTER;

			public Integer getDAY_COUNTER() {
				return DAY_COUNTER;
			}
            
        }

        public static class CardholderBillingData {
        	private Double BILLING_VALUE;
        	private String BILLING_CURRENCY;
        	private Double BILLING_CONVERSION_RATE;
			public Double getBILLING_VALUE() {
				return BILLING_VALUE;
			}
			public String getBILLING_CURRENCY() {
				return BILLING_CURRENCY;
			}
			public Double getBILLING_CONVERSION_RATE() {
				return BILLING_CONVERSION_RATE;
			}
            
        }

    }

	public String getFILE_ID() {
		return FILE_ID;
	}

	public String getISSUER_ID() {
		return ISSUER_ID;
	}

	public String getCLIENT_ID() {
		return CLIENT_ID;
	}

	public String getID_SUBEMISSOR() {
		return ID_SUBEMISSOR;
	}

	public String getBRAND() {
		return BRAND;
	}

	public String getFILENAME_BASE2() {
		return FILENAME_BASE2;
	}

	public String getFILENAME() {
		return FILENAME;
	}

	public Integer getSEQUENCE() {
		return SEQUENCE;
	}

	public Integer getFILE_NUMBER() {
		return FILE_NUMBER;
	}

	public Integer getTOTAL_FILES() {
		return TOTAL_FILES;
	}

	public String getREFERENCE_DATE() {
		return REFERENCE_DATE;
	}

	public Integer getRECORDS_TOTAL() {
		return RECORDS_TOTAL;
	}

	public Double getRECORDS_AMNT() {
		return RECORDS_AMNT;
	}

	public Integer getCREDIT_TOTAL() {
		return CREDIT_TOTAL;
	}

	public Double getCREDIT_AMNT() {
		return CREDIT_AMNT;
	}

	public Integer getDEBIT_TOTAL() {
		return DEBIT_TOTAL;
	}

	public Double getDEBIT_AMNT() {
		return DEBIT_AMNT;
	}

	public Integer getUNKNOWN_TOTAL() {
		return UNKNOWN_TOTAL;
	}

	public Double getUNKNOWN_AMNT() {
		return UNKNOWN_AMNT;
	}

	public Integer getREJECTED_TOTAL() {
		return REJECTED_TOTAL;
	}

	public Double getREJECTED_AMNT() {
		return REJECTED_AMNT;
	}

	public Integer getOCCURRENCE_TOTAL() {
		return OCCURRENCE_TOTAL;
	}

	public Double getOCCURRENCE_AMNT() {
		return OCCURRENCE_AMNT;
	}

	public Integer getEXPIRED_TOTAL() {
		return EXPIRED_TOTAL;
	}

	public List<Content> getCONTENT() {
		return CONTENT;
	}

	@Override
	public String toString() {
		return "Compensation [FILE_ID=" + FILE_ID + ", ISSUER_ID=" + ISSUER_ID + ", CLIENT_ID=" + CLIENT_ID
				+ ", ID_SUBEMISSOR=" + ID_SUBEMISSOR + ", BRAND=" + BRAND + ", FILENAME_BASE2=" + FILENAME_BASE2
				+ ", FILENAME=" + FILENAME + ", SEQUENCE=" + SEQUENCE + ", FILE_NUMBER=" + FILE_NUMBER
				+ ", TOTAL_FILES=" + TOTAL_FILES + ", REFERENCE_DATE=" + REFERENCE_DATE + ", RECORDS_TOTAL="
				+ RECORDS_TOTAL + ", RECORDS_AMNT=" + RECORDS_AMNT + ", CREDIT_TOTAL=" + CREDIT_TOTAL + ", CREDIT_AMNT="
				+ CREDIT_AMNT + ", DEBIT_TOTAL=" + DEBIT_TOTAL + ", DEBIT_AMNT=" + DEBIT_AMNT + ", UNKNOWN_TOTAL="
				+ UNKNOWN_TOTAL + ", UNKNOWN_AMNT=" + UNKNOWN_AMNT + ", REJECTED_TOTAL=" + REJECTED_TOTAL
				+ ", REJECTED_AMNT=" + REJECTED_AMNT + ", OCCURRENCE_TOTAL=" + OCCURRENCE_TOTAL + ", OCCURRENCE_AMNT="
				+ OCCURRENCE_AMNT + ", EXPIRED_TOTAL=" + EXPIRED_TOTAL + ", CONTENT=" + CONTENT + "]";
	}

}
