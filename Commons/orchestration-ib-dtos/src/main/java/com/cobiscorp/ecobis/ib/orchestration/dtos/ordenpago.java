package com.cobiscorp.ecobis.ib.orchestration.dtos;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class ordenpago implements Serializable{
	
	public ordenpago() {}
	
	public ordenpago(String opFechaOper, int opFolio, int opInsClave, BigDecimal opMonto, int opTpClave,
			String opCveRastreo, String opEstado, String opTipoOrden, int opPrioridad, String opTopologia,
			int opMeClave, String opUsuClave, String opNomOrd, int opTcClaveOrd, String opCuentaOrd,
			String opRfcCurpOrd, String opNomBen, int opTcClaveBen, String opCuentaBen, String opNomBen2,
			int opTcClaveBen2, String opCuentaBen2, String opConceptoPago, int opRefNumerica, String opFirmaDig,
			int opToClave, String opConceptoPag2, String Id) {
		super();
		this.OpFechaOper = opFechaOper;
		this.OpFolio = opFolio;
		this.OpInsClave = opInsClave;
		this.OpMonto = opMonto;
		this.OpTpClave = opTpClave;
		this.OpCveRastreo = opCveRastreo;
		this.OpEstado = opEstado;
		this.OpTipoOrden = opTipoOrden;
		this.OpPrioridad = opPrioridad;
		this.OpTopologia = opTopologia;
		this.OpMeClave = opMeClave;
		this.OpUsuClave = opUsuClave;
		this.OpNomOrd = opNomOrd;
		this.OpTcClaveOrd = opTcClaveOrd;
		this.OpCuentaOrd = opCuentaOrd;
		this.OpRfcCurpOrd = opRfcCurpOrd;
		this.OpNomBen = opNomBen;
		this.OpTcClaveBen = opTcClaveBen;
		this.OpCuentaBen = opCuentaBen;
		this.OpNomBen2 = opNomBen2;
		this.OpTcClaveBen2 = opTcClaveBen2;
		this.OpCuentaBen2 = opCuentaBen2;
		this.OpConceptoPago = opConceptoPago;
		this.OpRefNumerica = opRefNumerica;
		this.opFirmaDig = opFirmaDig;
		this.OpToClave = opToClave;
		this.OpConceptoPag2 = opConceptoPag2;
		this.Id = Id;
	}
	@XmlAttribute(name="Id")
	private String Id;
	
	@XmlElement
	private String OpFechaOper;
	
	@XmlElement
	private int OpFolio;
	
	@XmlElement
	private int OpInsClave;
	
	@XmlElement
	private BigDecimal OpMonto;
	
	@XmlElement
	private BigDecimal OpIva;
	
	@XmlElement
	private int paqFolioOri;
	
	@XmlElement
	private int opFolioOri;
	
	@XmlElement
	private int OpTpClave;
	
	@XmlElement
	private String OpCveRastreo;
	
	@XmlElement
	private String OpEstado;
	
	@XmlElement
	private String OpTipoOrden;
	
	@XmlElement
	private int OpPrioridad;
	
	@XmlElement
	private String OpTopologia;
	
	@XmlElement
	private int OpMeClave;
	
	@XmlElement
	private String OpUsuClave;
	
	@XmlElement
	private String OpNomOrd;
	
	@XmlElement
	private int OpTcClaveOrd;
	
	@XmlElement
	private String OpCuentaOrd;
	
	@XmlElement
	private String OpRfcCurpOrd;
	
	@XmlElement
	private String OpNomBen;
	
	@XmlElement
	private int OpTcClaveBen;
	
	@XmlElement
	private String OpCuentaBen;
	
	@XmlElement
	private String OpNomBen2;
	
	@XmlElement
	private int OpTcClaveBen2;
	
	@XmlElement
	private String OpCuentaBen2;
	
	@XmlElement
	private String OpConceptoPago;
	
	@XmlElement
	private int OpRefNumerica;
	
	@XmlElement
	private String opFirmaDig;
	//
	
	@XmlElement
	private int OpToClave;
	//
	
	@XmlElement
    private String OpConceptoPag2;
    //
	
	@XmlElement
    private int OpCdClave;
    //
	
	@XmlElement
    private String opCuentaParticipanteOrd;
	//
	
	@XmlElement
    private String opCuentaEmisorRemesa;
	//
	
	@XmlElement
    private String opNomParticipanteOrd;
	
	@XmlElement
    private String opRfcParticipanteOrd;
	
	@XmlElement
    private String opIdRemesa;
	
	@XmlElement
    private String opPais;
	
	@XmlElement
    private String opDivisa;
	
	@XmlElement
    private String opNomEmisorRemesa;
	
	@XmlElement
    private String opRfcCurpEmisorRemesa;
	
	@XmlElement
    private String opNomBenRemesa;
	
	@XmlElement
    private String opNomProvRemesaNacional;
    
	@XmlElement
    private String opNomProvRemesaExtranjera;
	
	/**
	 * @return the opNomProvRemesaNacional
	 */
	public synchronized String getOpRfcCurpEmisorRemesa() {
		return opRfcCurpEmisorRemesa;
	}
	/**
	 * @param opNomProvRemesaExtranjera the opNomProvRemesaExtranjera to set
	 */
	public synchronized void setOpRfcCurpEmisorRemesa(String opRfcCurpEmisorRemesa) {
		this.opRfcCurpEmisorRemesa = opRfcCurpEmisorRemesa;
	}
	/**
	 * @return the opNomProvRemesaNacional
	 */
	public synchronized String getOpNomProvRemesaExtranjera() {
		return opNomProvRemesaExtranjera;
	}
	/**
	 * @param opNomProvRemesaExtranjera the opNomProvRemesaExtranjera to set
	 */
	public synchronized void setOpNomProvRemesaExtranjera(String opNomProvRemesaExtranjera) {
		this.opNomProvRemesaExtranjera = opNomProvRemesaExtranjera;
	}
	/**
	 * @return the opNomProvRemesaNacional
	 */
	public synchronized String getOpNomProvRemesaNacional() {
		return opNomProvRemesaNacional;
	}
	/**
	 * @param opNomProvRemesaNacional the opNomProvRemesaNacional to set
	 */
	public synchronized void setOpNomProvRemesaNacional(String opNomProvRemesaNacional) {
		this.opNomProvRemesaNacional = opNomProvRemesaNacional;
	}
	/**
	 * @return the opNomBenRemesa
	 */
	public synchronized String getOpNomBenRemesa() {
		return opNomEmisorRemesa;
	}
	/**
	 * @param opNomBenRemesa the opNomBenRemesa to set
	 */
	public synchronized void setOpNomBenRemesa(String opNomBenRemesa) {
		this.opNomBenRemesa = opNomBenRemesa;
	}
	/**
	 * @return the opNomEmisorRemesa
	 */
	public synchronized String getOpNomEmisorRemesa() {
		return opNomEmisorRemesa;
	}
	/**
	 * @param opNomEmisorRemesa the opNomEmisorRemesa to set
	 */
	public synchronized void setOpNomEmisorRemesa(String opNomEmisorRemesa) {
		this.opNomEmisorRemesa = opNomEmisorRemesa;
	}
	/**
	 * @return the opPais
	 */
	public synchronized String getOpDivisa() {
		return opDivisa;
	}
	/**
	 * @param opRfcParticipanteOrd the opRfcParticipanteOrd to set
	 */
	public synchronized void setOpDivisa(String opDivisa) {
		this.opDivisa = opDivisa;
	}
	/**
	 * @return the opPais
	 */
	public synchronized String getOpPais() {
		return opPais;
	}
	/**
	 * @param opRfcParticipanteOrd the opRfcParticipanteOrd to set
	 */
	public synchronized void setOpPais(String opPais) {
		this.opPais = opPais;
	}
	/**
	 * @return the opIdRemesa
	 */
	public synchronized String getOpIdRemesa() {
		return opIdRemesa;
	}
	/**
	 * @param opRfcParticipanteOrd the opRfcParticipanteOrd to set
	 */
	public synchronized void setOpIdRemesa(String opIdRemesa) {
		this.opIdRemesa = opIdRemesa;
	}
	/**
	 * @return the opRfcParticipanteOrd
	 */
	public synchronized String getOpRfcParticipanteOrd() {
		return opRfcParticipanteOrd;
	}
	/**
	 * @param opRfcParticipanteOrd the opRfcParticipanteOrd to set
	 */
	public synchronized void setOpRfcParticipanteOrd(String opRfcParticipanteOrd) {
		this.opRfcParticipanteOrd = opRfcParticipanteOrd;
	}
	/**
	 * @return the opNomParticipanteOrd
	 */
	public synchronized String getOpNomParticipanteOrd() {
		return opNomParticipanteOrd;
	}
	/**
	 * @param opNomParticipanteOrd the opNomParticipanteOrd to set
	 */
	public synchronized void setOpNomParticipanteOrd(String opNomParticipanteOrd) {
		this.opNomParticipanteOrd = opNomParticipanteOrd;
	}
	/**
	 * @return the OpCdClave
	 */
	public synchronized int getOpCdClave() {
		return OpCdClave;
	}
	/**
	 * @param opInsClave the opInsClave to set
	 */
	public synchronized void setOpCdClave(int OpCdClave) {
		this.OpCdClave = OpCdClave;
	}
	/**
	 * @return the opFechaOper
	 */
	public synchronized String getOpCuentaParticipanteOrd() {
		return opCuentaParticipanteOrd;
	}
	/**
	 * @param opFechaOper the opFechaOper to set
	 */
	public synchronized void setOpCuentaParticipanteOrd(String opCuentaParticipanteOrd) {
		this.opCuentaParticipanteOrd = opCuentaParticipanteOrd;
	}
	/**
	 * @return the opFechaOper
	 */
	public synchronized String getOpCuentaEmisorRemesa() {
		return opCuentaEmisorRemesa;
	}
	/**
	 * @param opFechaOper the opFechaOper to set
	 */
	public synchronized void setOpCuentaEmisorRemesa(String opCuentaEmisorRemesa) {
		this.opCuentaEmisorRemesa = opCuentaEmisorRemesa;
	}
	/**
	 * @return the opFechaOper
	 */
	public synchronized String getOpFechaOper() {
		return OpFechaOper;
	}
	/**
	 * @param opFechaOper the opFechaOper to set
	 */
	public synchronized void setOpFechaOper(String opFechaOper) {
		this.OpFechaOper = opFechaOper;
	}
	/**
	 * @return the opFolio
	 */
	public synchronized int getOpFolio() {
		return OpFolio;
	}
	/**
	 * @param opFolio the opFolio to set
	 */
	public synchronized void setOpFolio(int opFolio) {
		this.OpFolio = opFolio;
	}
	/**
	 * @return the opInsClave
	 */
	public synchronized int getOpInsClave() {
		return OpInsClave;
	}
	/**
	 * @param opInsClave the opInsClave to set
	 */
	public synchronized void setOpInsClave(int opInsClave) {
		this.OpInsClave = opInsClave;
	}
	/**
	 * @return the opMonto
	 */
	public synchronized BigDecimal getOpMonto() {
		return OpMonto;
	}
	/**
	 * @param opMonto the opMonto to set
	 */
	public synchronized void setOpMonto(BigDecimal opMonto) {
		this.OpMonto = opMonto;
	}
	/**
	 * @return the opTpClave
	 */
	public synchronized int getOpTpClave() {
		return OpTpClave;
	}
	/**
	 * @param opTpClave the opTpClave to set
	 */
	public synchronized void setOpTpClave(int opTpClave) {
		this.OpTpClave = opTpClave;
	}
	/**
	 * @return the opCveRastreo
	 */
	public synchronized String getOpCveRastreo() {
		return OpCveRastreo;
	}
	/**
	 * @param opCveRastreo the opCveRastreo to set
	 */
	public synchronized void setOpCveRastreo(String opCveRastreo) {
		this.OpCveRastreo = opCveRastreo;
	}
	/**
	 * @return the opEstado
	 */
	public synchronized String getOpEstado() {
		return OpEstado;
	}
	/**
	 * @param opEstado the opEstado to set
	 */
	public synchronized void setOpEstado(String opEstado) {
		this.OpEstado = opEstado;
	}
	/**
	 * @return the opTipoOrden
	 */
	public synchronized String getOpTipoOrden() {
		return OpTipoOrden;
	}
	/**
	 * @param opTipoOrden the opTipoOrden to set
	 */
	public synchronized void setOpTipoOrden(String opTipoOrden) {
		this.OpTipoOrden = opTipoOrden;
	}
	/**
	 * @return the opPrioridad
	 */
	public synchronized int getOpPrioridad() {
		return OpPrioridad;
	}
	/**
	 * @param opPrioridad the opPrioridad to set
	 */
	public synchronized void setOpPrioridad(int opPrioridad) {
		this.OpPrioridad = opPrioridad;
	}
	/**
	 * @return the opTopologia
	 */
	public synchronized String getOpTopologia() {
		return OpTopologia;
	}
	/**
	 * @param opTopologia the opTopologia to set
	 */
	public synchronized void setOpTopologia(String opTopologia) {
		this.OpTopologia = opTopologia;
	}
	/**
	 * @return the opMeClave
	 */
	public synchronized int getOpMeClave() {
		return OpMeClave;
	}
	/**
	 * @param opMeClave the opMeClave to set
	 */
	public synchronized void setOpMeClave(int opMeClave) {
		this.OpMeClave = opMeClave;
	}
	/**
	 * @return the opUsuClave
	 */
	public synchronized String getOpUsuClave() {
		return OpUsuClave;
	}
	/**
	 * @param opUsuClave the opUsuClave to set
	 */
	public synchronized void setOpUsuClave(String opUsuClave) {
		this.OpUsuClave = opUsuClave;
	}
	/**
	 * @return the opNomOrd
	 */
	public synchronized String getOpNomOrd() {
		return OpNomOrd;
	}
	/**
	 * @param opNomOrd the opNomOrd to set
	 */
	public synchronized void setOpNomOrd(String opNomOrd) {
		this.OpNomOrd = opNomOrd;
	}
	/**
	 * @return the opTcClaveOrd
	 */
	public synchronized int getOpTcClaveOrd() {
		return OpTcClaveOrd;
	}
	/**
	 * @param opTcClaveOrd the opTcClaveOrd to set
	 */
	public synchronized void setOpTcClaveOrd(int opTcClaveOrd) {
		this.OpTcClaveOrd = opTcClaveOrd;
	}
	/**
	 * @return the opCuentaOrd
	 */
	public synchronized String getOpCuentaOrd() {
		return OpCuentaOrd;
	}
	/**
	 * @param opCuentaOrd the opCuentaOrd to set
	 */
	public synchronized void setOpCuentaOrd(String opCuentaOrd) {
		this.OpCuentaOrd = opCuentaOrd;
	}
	/**
	 * @return the opRfcCurpOrd
	 */
	public synchronized String getOpRfcCurpOrd() {
		return OpRfcCurpOrd;
	}
	/**
	 * @param opRfcCurpOrd the opRfcCurpOrd to set
	 */
	public synchronized void setOpRfcCurpOrd(String opRfcCurpOrd) {
		this.OpRfcCurpOrd = opRfcCurpOrd;
	}
	/**
	 * @return the opNomBen
	 */
	public synchronized String getOpNomBen() {
		return OpNomBen;
	}
	/**
	 * @param opNomBen the opNomBen to set
	 */
	public synchronized void setOpNomBen(String opNomBen) {
		this.OpNomBen = opNomBen;
	}
	/**
	 * @return the opTcClaveBen
	 */
	public synchronized int getOpTcClaveBen() {
		return OpTcClaveBen;
	}
	/**
	 * @param opTcClaveBen the opTcClaveBen to set
	 */
	public synchronized void setOpTcClaveBen(int opTcClaveBen) {
		this.OpTcClaveBen = opTcClaveBen;
	}
	/**
	 * @return the opCuentaBen
	 */
	public synchronized String getOpCuentaBen() {
		return OpCuentaBen;
	}
	/**
	 * @param opCuentaBen the opCuentaBen to set
	 */
	public synchronized void setOpCuentaBen(String opCuentaBen) {
		this.OpCuentaBen = opCuentaBen;
	}
	/**
	 * @return the opNomBen2
	 */
	public synchronized String getOpNomBen2() {
		return OpNomBen2;
	}
	/**
	 * @param opNomBen2 the opNomBen2 to set
	 */
	public synchronized void setOpNomBen2(String opNomBen2) {
		this.OpNomBen2 = opNomBen2;
	}
	/**
	 * @return the opTcClaveBen2
	 */
	public synchronized int getOpTcClaveBen2() {
		return OpTcClaveBen2;
	}
	/**
	 * @param opTcClaveBen2 the opTcClaveBen2 to set
	 */
	public synchronized void setOpTcClaveBen2(int opTcClaveBen2) {
		this.OpTcClaveBen2 = opTcClaveBen2;
	}
	/**
	 * @return the opCuentaBen2
	 */
	public synchronized String getOpCuentaBen2() {
		return OpCuentaBen2;
	}
	/**
	 * @param opCuentaBen2 the opCuentaBen2 to set
	 */
	public synchronized void setOpCuentaBen2(String opCuentaBen2) {
		this.OpCuentaBen2 = opCuentaBen2;
	}
	/**
	 * @return the opConceptoPago
	 */
	public synchronized String getOpConceptoPago() {
		return OpConceptoPago;
	}
	/**
	 * @param opConceptoPago the opConceptoPago to set
	 */
	public synchronized void setOpConceptoPago(String opConceptoPago) {
		this.OpConceptoPago = opConceptoPago;
	}
	/**
	 * @return the opRefNumerica
	 */
	public synchronized int getOpRefNumerica() {
		return OpRefNumerica;
	}
	/**
	 * @param opRefNumerica the opRefNumerica to set
	 */
	public synchronized void setOpRefNumerica(int opRefNumerica) {
		this.OpRefNumerica = opRefNumerica;
	}
	/**
	 * @return the opFirmaDig
	 */
	public synchronized String getOpFirmaDig() {
		return opFirmaDig;
	}
	/**
	 * @param opFirmaDig the opFirmaDig to set
	 */
	public synchronized void setOpFirmaDig(String opFirmaDig) {
		this.opFirmaDig = opFirmaDig;
	}
	/**
	 * @return the opToClave
	 */
	public synchronized int getOpToClave() {
		return OpToClave;
	}
	/**
	 * @param opToClave the opToClave to set
	 */
	public synchronized void setOpToClave(int opToClave) {
		this.OpToClave = opToClave;
	}
	/**
	 * @return the opConceptoPag2
	 */
	public synchronized String getOpConceptoPag2() {
		return OpConceptoPag2;
	}
	/**
	 * @param opConceptoPag2 the opConceptoPag2 to set
	 */
	public synchronized void setOpConceptoPag2(String opConceptoPag2) {
		this.OpConceptoPag2 = opConceptoPag2;
	}
	
	public synchronized String getId() {
		return this.Id;
	}

	public synchronized void setId(String Id) {
		this.Id = Id;
	}
    
	public synchronized BigDecimal getOpIva()
	{
		return OpIva;
	}

	public synchronized void setOpIva(BigDecimal opIva)
	{
		OpIva = opIva;
	}

	public synchronized int getPaqFolioOri()
	{
		return paqFolioOri;
	}

	public synchronized void setPaqFolioOri(int paqFolioOri)
	{
		this.paqFolioOri = paqFolioOri;
	}

	public synchronized int getOpFolioOri()
	{
		return opFolioOri;
	}

	public synchronized void setOpFolioOri(int opFolioOri)
	{
		this.opFolioOri = opFolioOri;
	}
    
}
