/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author mvelez
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class SentInternationalTransfer {
	private String   date_transaction;
    private String   id_referency;
    private String   account_debit;
    private String   account_type;
    private String   account_name;
    private Double   ammount;
    private String   money;
    private String   referency;
    private String   beneficiary_name;
    private String   beneficiary_address_complete;
    private String   beneficiary_country;
    private String   beneficiary_city;
    private String   beneficiary_address;
    private String   beneficiary_account;
    private String   bank_beneficiary_country;
    private String   bank_beneficiary_name;
    private String   bank_beneficiary_description;
    private String   bank_beneficiary_address;
    private String   bank_beneficiary_swift;
    private String   type_address;
    private String   bank_intermediary_country;
    private String   bank_intermediary_name;
    private String   bank_intermediary_description;
    private String   bank_intermediary_address;
    private String   bank_intermediary_swift;
    private String   type_address_intermediary;
    private Double   cost_transaction;
    private String   beneficiary_continent_code;
    private String   beneficiary_continent;
    private String   transaction_code;
    private String   message_type;
    private Integer  sucursal_code;
    private String   sucursal;
    private Integer  bank_beneficiary_id;    
    private Integer  beneficiary_country_id;
    private Integer  beneficiary_city_id;
    private String   payer_city;
    private String   payer_name;
    private Integer  id;
    private Integer  ben_country_id;
    private Integer  ben_city_id;
    private String   bco_swift_ben;
    private String   bco_swift_inter;
    private Integer  bco_pais_ben;
    private Integer  bco_pais_int;
    private Integer  bco_ben_id;
    private Integer  bco_int_id;
    private Integer  bco_dir_ben_id;
    private Integer  bco_dir_int_id;
    private String   beneficiaryFirstLastName;
    private String   beneficiarySecondLastName;
    private String   beneficiaryBusinessName;
    private String   beneficiaryTypeDocument;
    private String   beneficiaryDocumentNumber;
    private Integer  currencyIdUSD;
    private Double   quote;
    private String   beneficiaryTypeDocumentName;
    private Integer  codeNegotiation;
    private String   beneficiaryEmail1;
    private String   beneficiaryEmail2;

	/**
	 * @return the date_transaction
	 */
	public String getDate_transaction() {
		return date_transaction;
	}
	/**
	 * @param date_transaction the date_transaction to set
	 */
	public void setDate_transaction(String date_transaction) {
		this.date_transaction = date_transaction;
	}
	/**
	 * @return the id_referency
	 */
	public String getId_referency() {
		return id_referency;
	}
	/**
	 * @param id_referency the id_referency to set
	 */
	public void setId_referency(String id_referency) {
		this.id_referency = id_referency;
	}
	/**
	 * @return the account_debit
	 */
	public String getAccount_debit() {
		return account_debit;
	}
	/**
	 * @param account_debit the account_debit to set
	 */
	public void setAccount_debit(String account_debit) {
		this.account_debit = account_debit;
	}
	/**
	 * @return the account_type
	 */
	public String getAccount_type() {
		return account_type;
	}
	/**
	 * @param account_type the account_type to set
	 */
	public void setAccount_type(String account_type) {
		this.account_type = account_type;
	}
	/**
	 * @return the account_name
	 */
	public String getAccount_name() {
		return account_name;
	}
	/**
	 * @param account_name the account_name to set
	 */
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	/**
	 * @return the ammount
	 */
	public Double getAmmount() {
		return ammount;
	}
	/**
	 * @param ammount the ammount to set
	 */
	public void setAmmount(Double ammount) {
		this.ammount = ammount;
	}
	/**
	 * @return the money
	 */
	public String getMoney() {
		return money;
	}
	/**
	 * @param money the money to set
	 */
	public void setMoney(String money) {
		this.money = money;
	}
	/**
	 * @return the referency
	 */
	public String getReferency() {
		return referency;
	}
	/**
	 * @param referency the referency to set
	 */
	public void setReferency(String referency) {
		this.referency = referency;
	}
	/**
	 * @return the beneficiary_name
	 */
	public String getBeneficiary_name() {
		return beneficiary_name;
	}
	/**
	 * @param beneficiary_name the beneficiary_name to set
	 */
	public void setBeneficiary_name(String beneficiary_name) {
		this.beneficiary_name = beneficiary_name;
	}
	/**
	 * @return the beneficiary_address_complete
	 */
	public String getBeneficiary_address_complete() {
		return beneficiary_address_complete;
	}
	/**
	 * @param beneficiary_address_complete the beneficiary_address_complete to set
	 */
	public void setBeneficiary_address_complete(String beneficiary_address_complete) {
		this.beneficiary_address_complete = beneficiary_address_complete;
	}
	/**
	 * @return the beneficiary_country
	 */
	public String getBeneficiary_country() {
		return beneficiary_country;
	}
	/**
	 * @param beneficiary_country the beneficiary_country to set
	 */
	public void setBeneficiary_country(String beneficiary_country) {
		this.beneficiary_country = beneficiary_country;
	}
	/**
	 * @return the beneficiary_city
	 */
	public String getBeneficiary_city() {
		return beneficiary_city;
	}
	/**
	 * @param beneficiary_city the beneficiary_city to set
	 */
	public void setBeneficiary_city(String beneficiary_city) {
		this.beneficiary_city = beneficiary_city;
	}
	/**
	 * @return the beneficiary_address
	 */
	public String getBeneficiary_address() {
		return beneficiary_address;
	}
	/**
	 * @param beneficiary_address the beneficiary_address to set
	 */
	public void setBeneficiary_address(String beneficiary_address) {
		this.beneficiary_address = beneficiary_address;
	}
	/**
	 * @return the beneficiary_account
	 */
	public String getBeneficiary_account() {
		return beneficiary_account;
	}
	/**
	 * @param beneficiary_account the beneficiary_account to set
	 */
	public void setBeneficiary_account(String beneficiary_account) {
		this.beneficiary_account = beneficiary_account;
	}
	/**
	 * @return the bank_beneficiary_country
	 */
	public String getBank_beneficiary_country() {
		return bank_beneficiary_country;
	}
	/**
	 * @param bank_beneficiary_country the bank_beneficiary_country to set
	 */
	public void setBank_beneficiary_country(String bank_beneficiary_country) {
		this.bank_beneficiary_country = bank_beneficiary_country;
	}
	/**
	 * @return the bank_beneficiary_name
	 */
	public String getBank_beneficiary_name() {
		return bank_beneficiary_name;
	}
	/**
	 * @param bank_beneficiary_name the bank_beneficiary_name to set
	 */
	public void setBank_beneficiary_name(String bank_beneficiary_name) {
		this.bank_beneficiary_name = bank_beneficiary_name;
	}
	/**
	 * @return the bank_beneficiary_description
	 */
	public String getBank_beneficiary_description() {
		return bank_beneficiary_description;
	}
	/**
	 * @param bank_beneficiary_description the bank_beneficiary_description to set
	 */
	public void setBank_beneficiary_description(String bank_beneficiary_description) {
		this.bank_beneficiary_description = bank_beneficiary_description;
	}
	/**
	 * @return the bank_beneficiary_address
	 */
	public String getBank_beneficiary_address() {
		return bank_beneficiary_address;
	}
	/**
	 * @param bank_beneficiary_address the bank_beneficiary_address to set
	 */
	public void setBank_beneficiary_address(String bank_beneficiary_address) {
		this.bank_beneficiary_address = bank_beneficiary_address;
	}
	/**
	 * @return the bank_beneficiary_swift
	 */
	public String getBank_beneficiary_swift() {
		return bank_beneficiary_swift;
	}
	/**
	 * @param bank_beneficiary_swift the bank_beneficiary_swift to set
	 */
	public void setBank_beneficiary_swift(String bank_beneficiary_swift) {
		this.bank_beneficiary_swift = bank_beneficiary_swift;
	}
	/**
	 * @return the type_address
	 */
	public String getType_address() {
		return type_address;
	}
	/**
	 * @param type_address the type_address to set
	 */
	public void setType_address(String type_address) {
		this.type_address = type_address;
	}
	/**
	 * @return the bank_intermediary_country
	 */
	public String getBank_intermediary_country() {
		return bank_intermediary_country;
	}
	/**
	 * @param bank_intermediary_country the bank_intermediary_country to set
	 */
	public void setBank_intermediary_country(String bank_intermediary_country) {
		this.bank_intermediary_country = bank_intermediary_country;
	}
	/**
	 * @return the bank_intermediary_name
	 */
	public String getBank_intermediary_name() {
		return bank_intermediary_name;
	}
	/**
	 * @param bank_intermediary_name the bank_intermediary_name to set
	 */
	public void setBank_intermediary_name(String bank_intermediary_name) {
		this.bank_intermediary_name = bank_intermediary_name;
	}
	/**
	 * @return the bank_intermediary_description
	 */
	public String getBank_intermediary_description() {
		return bank_intermediary_description;
	}
	/**
	 * @param bank_intermediary_description the bank_intermediary_description to set
	 */
	public void setBank_intermediary_description(
			String bank_intermediary_description) {
		this.bank_intermediary_description = bank_intermediary_description;
	}
	/**
	 * @return the bank_intermediary_address
	 */
	public String getBank_intermediary_address() {
		return bank_intermediary_address;
	}
	/**
	 * @param bank_intermediary_address the bank_intermediary_address to set
	 */
	public void setBank_intermediary_address(String bank_intermediary_address) {
		this.bank_intermediary_address = bank_intermediary_address;
	}
	/**
	 * @return the bank_intermediary_swift
	 */
	public String getBank_intermediary_swift() {
		return bank_intermediary_swift;
	}
	/**
	 * @param bank_intermediary_swift the bank_intermediary_swift to set
	 */
	public void setBank_intermediary_swift(String bank_intermediary_swift) {
		this.bank_intermediary_swift = bank_intermediary_swift;
	}
	/**
	 * @return the type_address_intermediary
	 */
	public String getType_address_intermediary() {
		return type_address_intermediary;
	}
	/**
	 * @param type_address_intermediary the type_address_intermediary to set
	 */
	public void setType_address_intermediary(String type_address_intermediary) {
		this.type_address_intermediary = type_address_intermediary;
	}
	/**
	 * @return the cost_transaction
	 */
	public Double getCost_transaction() {
		return cost_transaction;
	}
	/**
	 * @param cost_transaction the cost_transaction to set
	 */
	public void setCost_transaction(Double cost_transaction) {
		this.cost_transaction = cost_transaction;
	}
	/**
	 * @return the beneficiary_continent_code
	 */
	public String getBeneficiary_continent_code() {
		return beneficiary_continent_code;
	}
	/**
	 * @param beneficiary_continent_code the beneficiary_continent_code to set
	 */
	public void setBeneficiary_continent_code(String beneficiary_continent_code) {
		this.beneficiary_continent_code = beneficiary_continent_code;
	}
	/**
	 * @return the beneficiary_continent
	 */
	public String getBeneficiary_continent() {
		return beneficiary_continent;
	}
	/**
	 * @param beneficiary_continent the beneficiary_continent to set
	 */
	public void setBeneficiary_continent(String beneficiary_continent) {
		this.beneficiary_continent = beneficiary_continent;
	}
	/**
	 * @return the transaction_code
	 */
	public String getTransaction_code() {
		return transaction_code;
	}
	/**
	 * @param transaction_code the transaction_code to set
	 */
	public void setTransaction_code(String transaction_code) {
		this.transaction_code = transaction_code;
	}
	/**
	 * @return the message_type
	 */
	public String getMessage_type() {
		return message_type;
	}
	/**
	 * @param message_type the message_type to set
	 */
	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}
	/**
	 * @return the sucursal_code
	 */
	public Integer getSucursal_code() {
		return sucursal_code;
	}
	/**
	 * @param sucursal_code the sucursal_code to set
	 */
	public void setSucursal_code(Integer sucursal_code) {
		this.sucursal_code = sucursal_code;
	}
	/**
	 * @return the sucursal
	 */
	public String getSucursal() {
		return sucursal;
	}
	/**
	 * @param sucursal the sucursal to set
	 */
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	/**
	 * @return the bank_beneficiary_id
	 */
	public Integer getBank_beneficiary_id() {
		return bank_beneficiary_id;
	}
	/**
	 * @param bank_beneficiary_id the bank_beneficiary_id to set
	 */
	public void setBank_beneficiary_id(Integer bank_beneficiary_id) {
		this.bank_beneficiary_id = bank_beneficiary_id;
	}
	/**
	 * @return the beneficiary_country_id
	 */
	public Integer getBeneficiary_country_id() {
		return beneficiary_country_id;
	}
	/**
	 * @param beneficiary_country_id the beneficiary_country_id to set
	 */
	public void setBeneficiary_country_id(Integer beneficiary_country_id) {
		this.beneficiary_country_id = beneficiary_country_id;
	}
	/**
	 * @return the beneficiary_city_id
	 */
	public Integer getBeneficiary_city_id() {
		return beneficiary_city_id;
	}
	/**
	 * @param beneficiary_city_id the beneficiary_city_id to set
	 */
	public void setBeneficiary_city_id(Integer beneficiary_city_id) {
		this.beneficiary_city_id = beneficiary_city_id;
	}
	/**
	 * @return the payer_city
	 */
	public String getPayer_city() {
		return payer_city;
	}
	/**
	 * @param payer_city the payer_city to set
	 */
	public void setPayer_city(String payer_city) {
		this.payer_city = payer_city;
	}
	/**
	 * @return the payer_name
	 */
	public String getPayer_name() {
		return payer_name;
	}
	/**
	 * @param payer_name the payer_name to set
	 */
	public void setPayer_name(String payer_name) {
		this.payer_name = payer_name;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the ben_country_id
	 */
	public Integer getBen_country_id() {
		return ben_country_id;
	}
	/**
	 * @param ben_country_id the ben_country_id to set
	 */
	public void setBen_country_id(Integer ben_country_id) {
		this.ben_country_id = ben_country_id;
	}
	/**
	 * @return the ben_city_id
	 */
	public Integer getBen_city_id() {
		return ben_city_id;
	}
	/**
	 * @param ben_city_id the ben_city_id to set
	 */
	public void setBen_city_id(Integer ben_city_id) {
		this.ben_city_id = ben_city_id;
	}
	/**
	 * @return the bco_swift_ben
	 */
	public String getBco_swift_ben() {
		return bco_swift_ben;
	}
	/**
	 * @param bco_swift_ben the bco_swift_ben to set
	 */
	public void setBco_swift_ben(String bco_swift_ben) {
		this.bco_swift_ben = bco_swift_ben;
	}
	/**
	 * @return the bco_swift_inter
	 */
	public String getBco_swift_inter() {
		return bco_swift_inter;
	}
	/**
	 * @param bco_swift_inter the bco_swift_inter to set
	 */
	public void setBco_swift_inter(String bco_swift_inter) {
		this.bco_swift_inter = bco_swift_inter;
	}
	/**
	 * @return the bco_pais_ben
	 */
	public Integer getBco_pais_ben() {
		return bco_pais_ben;
	}
	/**
	 * @param bco_pais_ben the bco_pais_ben to set
	 */
	public void setBco_pais_ben(Integer bco_pais_ben) {
		this.bco_pais_ben = bco_pais_ben;
	}
	/**
	 * @return the bco_pais_int
	 */
	public Integer getBco_pais_int() {
		return bco_pais_int;
	}
	/**
	 * @param bco_pais_int the bco_pais_int to set
	 */
	public void setBco_pais_int(Integer bco_pais_int) {
		this.bco_pais_int = bco_pais_int;
	}
	/**
	 * @return the bco_ben_id
	 */
	public Integer getBco_ben_id() {
		return bco_ben_id;
	}
	/**
	 * @param bco_ben_id the bco_ben_id to set
	 */
	public void setBco_ben_id(Integer bco_ben_id) {
		this.bco_ben_id = bco_ben_id;
	}
	/**
	 * @return the bco_int_id
	 */
	public Integer getBco_int_id() {
		return bco_int_id;
	}
	/**
	 * @param bco_int_id the bco_int_id to set
	 */
	public void setBco_int_id(Integer bco_int_id) {
		this.bco_int_id = bco_int_id;
	}
	/**
	 * @return the bco_dir_ben_id
	 */
	public Integer getBco_dir_ben_id() {
		return bco_dir_ben_id;
	}
	/**
	 * @param bco_dir_ben_id the bco_dir_ben_id to set
	 */
	public void setBco_dir_ben_id(Integer bco_dir_ben_id) {
		this.bco_dir_ben_id = bco_dir_ben_id;
	}
	/**
	 * @return the bco_dir_int_id
	 */
	public Integer getBco_dir_int_id() {
		return bco_dir_int_id;
	}
	/**
	 * @param bco_dir_int_id the bco_dir_int_id to set
	 */
	public void setBco_dir_int_id(Integer bco_dir_int_id) {
		this.bco_dir_int_id = bco_dir_int_id;
	}
	/**
	 * @return the beneficiaryFirstLastName
	 */
	public String getBeneficiaryFirstLastName() {
		return beneficiaryFirstLastName;
	}
	/**
	 * @param beneficiaryFirstLastName the beneficiaryFirstLastName to set
	 */
	public void setBeneficiaryFirstLastName(String beneficiaryFirstLastName) {
		this.beneficiaryFirstLastName = beneficiaryFirstLastName;
	}
	/**
	 * @return the beneficiarySecondLastName
	 */
	public String getBeneficiarySecondLastName() {
		return beneficiarySecondLastName;
	}
	/**
	 * @param beneficiarySecondLastName the beneficiarySecondLastName to set
	 */
	public void setBeneficiarySecondLastName(String beneficiarySecondLastName) {
		this.beneficiarySecondLastName = beneficiarySecondLastName;
	}
	/**
	 * @return the beneficiaryBusinessName
	 */
	public String getBeneficiaryBusinessName() {
		return beneficiaryBusinessName;
	}
	/**
	 * @param beneficiaryBusinessName the beneficiaryBusinessName to set
	 */
	public void setBeneficiaryBusinessName(String beneficiaryBusinessName) {
		this.beneficiaryBusinessName = beneficiaryBusinessName;
	}
	/**
	 * @return the beneficiaryTypeDocument
	 */
	public String getBeneficiaryTypeDocument() {
		return beneficiaryTypeDocument;
	}
	/**
	 * @param beneficiaryTypeDocument the beneficiaryTypeDocument to set
	 */
	public void setBeneficiaryTypeDocument(String beneficiaryTypeDocument) {
		this.beneficiaryTypeDocument = beneficiaryTypeDocument;
	}
	/**
	 * @return the beneficiaryDocumentNumber
	 */
	public String getBeneficiaryDocumentNumber() {
		return beneficiaryDocumentNumber;
	}
	/**
	 * @param beneficiaryDocumentNumber the beneficiaryDocumentNumber to set
	 */
	public void setBeneficiaryDocumentNumber(String beneficiaryDocumentNumber) {
		this.beneficiaryDocumentNumber = beneficiaryDocumentNumber;
	}
	/**
	 * @return the currencyIdUSD
	 */
	public Integer getCurrencyIdUSD() {
		return currencyIdUSD;
	}
	/**
	 * @param currencyIdUSD the currencyIdUSD to set
	 */
	public void setCurrencyIdUSD(Integer currencyIdUSD) {
		this.currencyIdUSD = currencyIdUSD;
	}
	/**
	 * @return the quote
	 */
	public Double getQuote() {
		return quote;
	}
	/**
	 * @param quote the quote to set
	 */
	public void setQuote(Double quote) {
		this.quote = quote;
	}
	/**
	 * @return the beneficiaryTypeDocumentName
	 */
	public String getBeneficiaryTypeDocumentName() {
		return beneficiaryTypeDocumentName;
	}
	/**
	 * @param beneficiaryTypeDocumentName the beneficiaryTypeDocumentName to set
	 */
	public void setBeneficiaryTypeDocumentName(String beneficiaryTypeDocumentName) {
		this.beneficiaryTypeDocumentName = beneficiaryTypeDocumentName;
	}
	/**
	 * @return the codeNegotiation
	 */
	public Integer getCodeNegotiation() {
		return codeNegotiation;
	}
	/**
	 * @param codeNegotiation the codeNegotiation to set
	 */
	public void setCodeNegotiation(Integer codeNegotiation) {
		this.codeNegotiation = codeNegotiation;
	}
	/**
	 * @return the beneficiaryEmail1
	 */
	public String getBeneficiaryEmail1() {
		return beneficiaryEmail1;
	}
	/**
	 * @param beneficiaryEmail1 the beneficiaryEmail1 to set
	 */
	public void setBeneficiaryEmail1(String beneficiaryEmail1) {
		this.beneficiaryEmail1 = beneficiaryEmail1;
	}
	/**
	 * @return the beneficiaryEmail2
	 */
	public String getBeneficiaryEmail2() {
		return beneficiaryEmail2;
	}
	/**
	 * @param beneficiaryEmail2 the beneficiaryEmail2 to set
	 */
	public void setBeneficiaryEmail2(String beneficiaryEmail2) {
		this.beneficiaryEmail2 = beneficiaryEmail2;
	}

}
