package com.cobiscorp.ecobis.orchestration.core.ib.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Card;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CardType;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Payment;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductConsolidate;
import com.cobiscorp.ecobis.orchestration.core.ib.model.ConsolidateAccountsDto;
import com.cobiscorp.ecobis.orchestration.core.ib.model.ConsolidateCardsDto;

public class UtilityTransform {
	public List<ConsolidateAccountsDto> getConsolidateInDto(ConsolidateResponse consolidateResponse) {
		try {
			if (consolidateResponse == null)
				return null;

			List<ConsolidateAccountsDto> records = new ArrayList<ConsolidateAccountsDto>();

			for (ProductConsolidate row : consolidateResponse.getProductCollection()) {
				BalanceProduct balance = row.getBalance();
				BalanceProduct balancePrevious = row.getPreviousBalance();
				Product product = row.getProduct();
				Currency currency = row.getCurrency();
				ConsolidateAccountsDto record = new ConsolidateAccountsDto();

				if (product != null) {
					record.setProductId(product.getProductType());
					record.setProductDescription(product.getProductDescription());
					record.setProductNemonic(product.getProductNemonic());
					record.setProductNumber(product.getProductNumber());
					record.setAccountAlias(product.getProductAlias());
				}
				if (currency != null) {
					record.setCurrencyId(currency.getCurrencyId());
					record.setCurrencyNemonic(currency.getCurrencyNemonic());
					record.setCurrencyDescription(currency.getCurrencyDescription());
				}
				if (balance != null) {
					record.setBalanceRotate(balance.getRotateBalance());
					record.setEquityBalance(balance.getEquityBalance());
				}
				if (balancePrevious != null) {
					record.setBalanceRotatePrevious(balancePrevious.getRotateBalance());
					record.setEquityBalancePrevious(balancePrevious.getEquityBalance());
				}

				records.add(record);
			}

			return records;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Get information of consolidate account convert in DTO from ResultSet
	 *
	 * @param summaryCreditCardResponse
	 * @return List<ConsolidateAccountsDto> - List objects with summary accounts
	 */
	public List<ConsolidateCardsDto> getConsolidateCreditCardInDto(
			SummaryCreditCardResponse summaryCreditCardResponse) {
		List<ConsolidateCardsDto> records = new ArrayList<ConsolidateCardsDto>();
		try {
			if (summaryCreditCardResponse == null)
				return records;

			for (Card row : summaryCreditCardResponse.getListCard()) {
				ConsolidateCardsDto record = new ConsolidateCardsDto();
				Payment payment = row.getPayment();
				Currency currency = row.getCurrency();
				CardType cardType = row.getCardType();
				Product product = row.getProduct();

				if (payment != null) {
					record.setDatePayment(payment.getPaymentDate().toString());
					record.setAmmountPayment(payment.getPaymentAmmount());
					record.setCardAP(payment.getPaymentAP());
				}
				if (currency != null) {
					record.setCurrencyId(currency.getCurrencyId());
					record.setCurrencyNemonic(currency.getCurrencyNemonic());
					record.setCurrencyDescription(currency.getCurrencyDescription());
				}
				if (cardType != null) {
					record.setType(cardType.getCardType());
					record.setTypeName(cardType.getCardTypeDescription());
				}

				record.setNumber(row.getCardNumber());
				record.setName(row.getCardName());

				records.add(record);
			}

			return records;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void verifyMapService(String keyMap, String keyMapListContext, Map<String, Object> aBagSPJavaOrchestration) {
		List<String> listMapNameObj = (List<String>) aBagSPJavaOrchestration.get(keyMapListContext);
		if (listMapNameObj == null)
			listMapNameObj = new ArrayList<String>();
		listMapNameObj.add(keyMap);
		aBagSPJavaOrchestration.put(keyMapListContext, listMapNameObj);
	}

}
