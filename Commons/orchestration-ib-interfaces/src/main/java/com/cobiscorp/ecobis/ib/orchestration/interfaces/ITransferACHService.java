package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.TransferACHRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferACHresponse;

public interface ITransferACHService {
	
	/**  
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>
                   <li>TransferACHRequest aTransferACHRequest = TransferACHRequest();</li>
                   <li>aTransferACHRequest.setProductNumber("1234567890");</li>
                   <li>aTransferACHRequest.setInitialDate("01/01/2001");</li>
                   <li>aTransferACHRequest.setFinalDate("31/01/2001");</li>
                   <li>aTransferACHRequest.setDateFormatId(dd/mm/yyyy);</li>
                   <li>aTransferACHRequest.setSecuential(2);</li>
                   
    </ul>
    <b>
                   @return
        			-ParametrosDeSalida-
    </b>    
    <ul>
                   
                   	<li>TransferACHresponse transferACHresp = new TransferACHresponse ();</li>
		  			<li>List<TransferACH> atransferACHLIST = new ArrayList <TransferACH>();</li>
		  			<li>IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();</li>
					<li>atransferACH = new TransferACH();</li>
					<li>atransferACH.setPaymentDate(columns[FECHA].getValue());</li>
					<li>atransferACH.setProductType(Integer.parseInt(columns[CODIGO_TIPO_PRODUCTO].getValue()));</li>
					<li>atransferACH.setAccountAlias(columns[TIPO_PRODUCTO].getValue());</li>
					<li>atransferACH.setCreditAccount(columns[NUMERO_PRODUCTO ].getValue());</li>
					<li>atransferACH.setEntityName(columns[BANCO_DESTINO  ].getValue());</li>
					<li>atransferACH.setAmount(Double.parseDouble(columns[MONTO].getValue()));</li>
					<li>atransferACH.setNotes(columns[PROPOSITO].getValue());</li>
					<li>atransferACH.setCreationDate(columns[HORA].getValue());</li>
					<li>atransferACH.setSecuential(Integer.parseInt(columns[SECUENCIAL].getValue()));</li>
					<li>atransferACH.setBeneficiaryName(columns[BENEFICIARIO].getValue());</li>
					<li>atransferACH.setBeneficiaryId(Integer.parseInt(columns[CI_BENEFICIARIO].getValue()));</li>
					<li>atransferACH.setBeneficiaryPhone(columns[TELEFONO_BENEFICIARIO].getValue());</li>

    </ul>
    <b>
                   @throws
                   -ManejoDeErrores
    </b>
    <ul>
                   <li>CTSServiceException</li>
                   <li>CTSInfrastructureException</li>
    </ul>
    */

	public TransferACHresponse GetTransferACH(TransferACHRequest aTransferACHRequest)throws CTSServiceException, CTSInfrastructureException;
	
}
