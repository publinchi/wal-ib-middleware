package com.cobiscorp.ecobis.ib.orchestration.dtos;

import com.cobiscorp.ecobis.ib.application.dtos.BaseRequest;

public class SpeiRequest extends BaseRequest{

	private String idSpei;
	private String folioOrigen;
	private String claveRastreo;
	private String estado;
	private String causaDevolucion;
	private String motivo;
	private String tipoNotificacion;
	private String fechaOperacion;
	private String fechaRecepcion;
	private String institucionOrdenante;
	private String institucionBeneficiaria;
	private String monto;
	private String nombreOrdenante;
	private String tipoCuentaOrdenante;
	private String cuentaOrdenante;
	private String rfcCurpOrdenante;
	private String nombreBeneficiario;
	private String tipoCuentaBeneficiario;
	private String cuentaBeneficiario;
	private String rfcCurpBeneficiario;
	private String conceptoPago;
	private String referenciaNumerica;
	private String idTipoPago;
	private String firma;
	private ordenpago ordenPago;
	private Integer institucionOperante;

	public Integer getInstitucionOperante()
	{
		return institucionOperante;
	}
	public void setInstitucionOperante(Integer institucionOperante)
	{
		this.institucionOperante = institucionOperante;
	}
	public ordenpago getOrdenPago()
	{
		return ordenPago;
	}
	public void setOrdenPago(ordenpago ordenPago)
	{
		this.ordenPago = ordenPago;
	}
	public String getIdSpei() {
		return idSpei;
	}
	public void setIdSpei(String idSpei) {
		this.idSpei = idSpei;
	}
	public String getFolioOrigen() {
		return folioOrigen;
	}
	public void setFolioOrigen(String folioOrigen) {
		this.folioOrigen = folioOrigen;
	}
	public String getClaveRastreo() {
		return claveRastreo;
	}
	public void setClaveRastreo(String claveRastreo) {
		this.claveRastreo = claveRastreo;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCausaDevolucion() {
		return causaDevolucion;
	}
	public void setCausaDevolucion(String causaDevolucion) {
		this.causaDevolucion = causaDevolucion;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getTipoNotificacion() {
		return tipoNotificacion;
	}
	public void setTipoNotificacion(String tipoNotificacion) {
		this.tipoNotificacion = tipoNotificacion;
	}
	public String getFechaOperacion() {
		return fechaOperacion;
	}
	public void setFechaOperacion(String fechaOperacion) {
		this.fechaOperacion = fechaOperacion;
	}
	public String getFechaRecepcion() {
		return fechaRecepcion;
	}
	public void setFechaRecepcion(String fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}
	public String getInstitucionOrdenante() {
		return institucionOrdenante;
	}
	public void setInstitucionOrdenante(String institucionOrdenante) {
		this.institucionOrdenante = institucionOrdenante;
	}
	public String getInstitucionBeneficiaria() {
		return institucionBeneficiaria;
	}
	public void setInstitucionBeneficiaria(String institucionBeneficiaria) {
		this.institucionBeneficiaria = institucionBeneficiaria;
	}
	public String getMonto() {
		return monto;
	}
	public void setMonto(String monto) {
		this.monto = monto;
	}
	public String getNombreOrdenante() {
		return nombreOrdenante;
	}
	public void setNombreOrdenante(String nombreOrdenante) {
		this.nombreOrdenante = nombreOrdenante;
	}
	public String getTipoCuentaOrdenante() {
		return tipoCuentaOrdenante;
	}
	public void setTipoCuentaOrdenante(String tipoCuentaOrdenante) {
		this.tipoCuentaOrdenante = tipoCuentaOrdenante;
	}
	public String getCuentaOrdenante() {
		return cuentaOrdenante;
	}
	public void setCuentaOrdenante(String cuentaOrdenante) {
		this.cuentaOrdenante = cuentaOrdenante;
	}
	public String getRfcCurpOrdenante() {
		return rfcCurpOrdenante;
	}
	public void setRfcCurpOrdenante(String rfcCurpOrdenante) {
		this.rfcCurpOrdenante = rfcCurpOrdenante;
	}
	public String getNombreBeneficiario() {
		return nombreBeneficiario;
	}
	public void setNombreBeneficiario(String nombreBeneficiario) {
		this.nombreBeneficiario = nombreBeneficiario;
	}
	public String getTipoCuentaBeneficiario() {
		return tipoCuentaBeneficiario;
	}
	public void setTipoCuentaBeneficiario(String tipoCuentaBeneficiario) {
		this.tipoCuentaBeneficiario = tipoCuentaBeneficiario;
	}
	public String getCuentaBeneficiario() {
		return cuentaBeneficiario;
	}
	public void setCuentaBeneficiario(String cuentaBeneficiario) {
		this.cuentaBeneficiario = cuentaBeneficiario;
	}
	public String getRfcCurpBeneficiario() {
		return rfcCurpBeneficiario;
	}
	public void setRfcCurpBeneficiario(String rfcCurpBeneficiario) {
		this.rfcCurpBeneficiario = rfcCurpBeneficiario;
	}
	public String getConceptoPago() {
		return conceptoPago;
	}
	public void setConceptoPago(String conceptoPago) {
		this.conceptoPago = conceptoPago;
	}
	public String getReferenciaNumerica() {
		return referenciaNumerica;
	}
	public void setReferenciaNumerica(String referenciaNumerica) {
		this.referenciaNumerica = referenciaNumerica;
	}
	public String getIdTipoPago() {
		return idTipoPago;
	}
	public void setIdTipoPago(String idTipoPago) {
		this.idTipoPago = idTipoPago;
	}
	public String getFirma() {
		return firma;
	}
	public void setFirma(String firma) {
		this.firma = firma;
	}

	@Override
	public String toString() {
		return "{" +
				"idSpei='" + idSpei + '\'' +
				", folioOrigen='" + folioOrigen + '\'' +
				", claveRastreo='" + claveRastreo + '\'' +
				", estado='" + estado + '\'' +
				", causaDevolucion='" + causaDevolucion + '\'' +
				", motivo='" + motivo + '\'' +
				", tipoNotificacion='" + tipoNotificacion + '\'' +
				", fechaOperacion='" + fechaOperacion + '\'' +
				", fechaRecepcion='" + fechaRecepcion + '\'' +
				", institucionOrdenante='" + institucionOrdenante + '\'' +
				", institucionBeneficiaria='" + institucionBeneficiaria + '\'' +
				", monto='" + monto + '\'' +
				", nombreOrdenante='" + nombreOrdenante + '\'' +
				", tipoCuentaOrdenante='" + tipoCuentaOrdenante + '\'' +
				", cuentaOrdenante='" + cuentaOrdenante + '\'' +
				", rfcCurpOrdenante='" + rfcCurpOrdenante + '\'' +
				", nombreBeneficiario='" + nombreBeneficiario + '\'' +
				", tipoCuentaBeneficiario='" + tipoCuentaBeneficiario + '\'' +
				", cuentaBeneficiario='" + cuentaBeneficiario + '\'' +
				", rfcCurpBeneficiario='" + rfcCurpBeneficiario + '\'' +
				", conceptoPago='" + conceptoPago + '\'' +
				", referenciaNumerica='" + referenciaNumerica + '\'' +
				", idTipoPago='" + idTipoPago + '\'' +
				", firma='" + firma +
				'}';
	}
}
