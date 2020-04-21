/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.util.Map;

import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;

/**
 * @author schancay
 * @since Aug 28, 2014
 * @version 1.0.0
 */
public interface ICoreServiceNotification {
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>IProcedureRequest requestNotification = initProcedureRequest(anOriginalRequest);</li>
		
		<li>requestNotification.addInputParam("@i_ente_ib", anOriginalRequest.readParam("@s_cliente").getDataType(), anOriginalRequest.readValueParam("@s_cliente"));</li>		
		<li>requestNotification.addInputParam("@i_producto", anOriginalRequest.readParam("@i_prod").getDataType(), anOriginalRequest.readValueParam("@i_prod"));		</li>
		<li>requestNotification.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");</li>
		<li>requestNotification.addInputParam("@i_transaccion_id", ICTSTypes.SYBINTN, t_trn);</li>
		<li>requestNotification.addInputParam("@i_oficial_cli", ICTSTypes.SQLVARCHAR, officerResponse.getOfficer().getAcountEmailAdress());</li>
		<li>requestNotification.addInputParam("@i_oficial_cta", ICTSTypes.SQLVARCHAR, officerResponse.getOfficer().getOfficerEmailAdress());</li>
		<li>requestNotification.addInputParam("@i_m", anOriginalRequest.readParam("@i_mon").getDataType(), anOriginalRequest.readValueParam("@i_mon"));</li>
		<li>if ("1800005".equals(anOriginalRequest.readValueParam("@t_trn"))) {</li>
		<ul>
			<li>requestNotification.addInputParam("@i_num_producto", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));</li>
			<li>requestNotification.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR,responseTransaction.readValueParam("@o_tipo_chequera"));</li>
			<li>requestNotification.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nchqs"));</li>
			<li>requestNotification.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_dia_entrega"));</li>
			<li>requestNotification.addInputParam("@i_aux4", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombre_entrega"));</li>
			<li>requestNotification.addInputParam("@i_aux5", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_id_entrega"));			</li>
		</ul>
		<li>}</li>		
		<li>else if ("1800120".equals(anOriginalRequest.readValueParam("@t_trn"))) {</li>
		<ul>
			<li>requestNotification.addInputParam("@i_num_producto", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));</li>
			<li>requestNotification.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@o_oficina"));</li>
			<li>requestNotification.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_beneficiario"));</li>
			<li>requestNotification.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_retira_id"));</li>
			<li>requestNotification.addInputParam("@i_aux4", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_retira_nombre"));</li>
			<li>requestNotification.addInputParam("@i_aux5", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_proposito"));			</li>
			<li>requestNotification.addInputParam("@i_md", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));</li>
			<li>requestNotification.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_monto"));</li>
			<li>requestNotification.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_login"));</li>
		</ul>
		<li>}</li>
		<li>else if ("1800023".equals(anOriginalRequest.readValueParam("@t_trn"))) {</li>
		<ul>
			<li>requestNotification.addInputParam("@i_num_producto", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));</li>
			<li>requestNotification.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cheque_ini"));</li>
			<li>requestNotification.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@o_chq_hasta"));</li>
			<li>requestNotification.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@o_causal"));</li>
			<li>requestNotification.addInputParam("@i_m2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));</li>
			<li>requestNotification.addInputParam("@i_v3", ICTSTypes.SQLVARCHAR, responseUpdateLocal.readValueParam("@o_comision"));</li>
		</ul>
		<li>}</li>
		<li>requestNotification.addInputParam("@i_c1", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));</li>
		<li>requestNotification.addInputParam("@i_f", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));</li>
		<li>requestNotification.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_val"));</li>
		<li>requestNotification.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_concepto"));</li>
		<li>requestNotification.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_ssn_branch"));</li>
		
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>IProcedureResponse iProcedureResponse = new IProcedureResponse();</li>
	</ul>
	 */
	IProcedureResponse registerSendNotification(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);
}
