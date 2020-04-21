package com.cobiscorp.ecobis.orchestration.core.ib.utils;

import org.apache.commons.collections.Predicate;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;

public class ProductPredicate implements Predicate {

	private Product product;

	private static ILogger logger = LogFactory.getLogger(ProductPredicate.class);

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	public ProductPredicate(Product aProduct) {
		super();
		this.setProduct(aProduct);

	}

	@Override
	public boolean evaluate(Object arg0) {

		IResultSetRow row = (IResultSetRow) arg0;
		IResultSetRowColumnData[] columns = row.getColumnsAsArray();

		if (logger.isDebugEnabled()) {
			logger.logDebug("ROW TO FIND : " + row.toString());

			logger.logDebug("Tipo de producto :" + columns[2].getValue() + " - "
					+ this.getProduct().getProductType().toString());
			logger.logDebug("Moneda :" + columns[3].getValue() + " - "
					+ this.getProduct().getCurrency().getCurrencyId().toString());
			logger.logDebug("Producto :" + columns[4].getValue() + " - " + this.getProduct().getProductNumber());
		}

		return columns[2].getValue().trim().equals(this.getProduct().getProductType().toString().trim())
				&& columns[3].getValue().trim()
						.equals(this.getProduct().getCurrency().getCurrencyId().toString().trim())
				&& columns[4].getValue().trim().equals(this.getProduct().getProductNumber().trim());

	}

}
