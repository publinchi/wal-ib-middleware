package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto;

public class Constans
{
	public static final String ODPS_LIQUIDADAS_CARGOS_RESPUESTA = "ODPS_LIQUIDADAS_CARGOS_RESPUESTA";
	public static final String ODPS_LIQUIDADAS_CARGOS = "ODPS_LIQUIDADAS_CARGOS";
	public static final String ODPS_LIQUIDADAS_ABONOS_RESPUESTA = "ODPS_LIQUIDADAS_ABONOS_RESPUESTA";
	public static final String ODPS_LIQUIDADAS_ABONOS = "ODPS_LIQUIDADAS_ABONOS";
	public static final String ODPS_CANCELADAS_LOCAL_RESPUESTA = "ODPS_CANCELADAS_LOCAL_RESPUESTA";
	public static final String ODPS_CANCELADAS_LOCAL = "ODPS_CANCELADAS_LOCAL";
	public static final String ENSESION = "ENSESION";
	public static final String ENSESION_RESPUESTA = "ENSESION_RESPUESTA";
	public static final String ODPS_CANCELADAS_X_BANXICO = "ODPS_CANCELADAS_X_BANXICO";
	public static final String ODPS_CANCELADAS_X_BANXICO_RESPUESTA = "ODPS_CANCELADAS_X_BANXICO_RESPUESTA";
	public static final String RESPUESTA = "_RESPUESTA";
	public static final String MESSAJE_CODE = "messajeCode";
	public static final String VALIDATE_CODE = "validateCode";
	
	public static final String MESSAJE_FECHA_OPERACION_OBLIGATORIA = "La fecha de operación es obligatoria";
	public static final String MESSAJE_FOLIO_CODI_OBLIGATORIO = "El Folio CoDi es obligatorio";
	public static final String MESSAJE_CLAVE_INSTITUCION_OBLIGATORIA = "La clave de institución ordenante es obligatoria para este Tipo de Pago";
	public static final String MESSAJE_TIPO_OPERACION_OBLIGATORIO = "El tipo de operación es obligatorio para este Tipo de Pago";
	public static final String MESSAJE_CLAVE_RASTREO_OBLIGATORIO = "La clave de rastreo es obligatoria";
	public static final String MESSAJE_ESTADO_ENVIO_OBLIGATORIO = "El Estado del envío es obligatorio";
	public static final String MESSAJE_TIPO_ORDEN_OBLIGATORIO = "Tipo de orden es requerido.";
	public static final String MESSAJE_PRIORIDAD_ORDEN_OBLIGATORIA = "La prioridad de la orden es un dato obligatorio";
	public static final String MESSAJE_TOPOLOGIA_OBLIGATORIA = "La Topología de la orden es obligatorio";
	public static final String MESSAJE_CLAVE_BANCO_USUARIO_OBLIGATORIA = "La clave del banco usuario es obligatoria para este Tipo de Pago";
	public static final String MESSAJE_MONTO_OBLIGATORIO = "El Monto es obligatorio";
	public static final String MESSAJE_CLAVE_PAGO_OBLIGATORIA = "La clave del pago es obligatorio para este Tipo de Pago";
	public static final String MESSAJE_CUENTA_BENEFICIARIO_NUMERICA = "La cuenta del beneficiario solo puede ser numérica";
	public static final String MESSAJE_CUENTA_TARJETA_DEBITO = "Para tipo de cuenta Tarjeta de Debito la cuenta del beneficiario debe ser de 16 dígitos.";
	public static final String MESSAJE_CUENTA_TELEFONO = "Para tipo de cuenta Telefono, la cuenta del beneficiario debe ser de 10 dígitos.";
	public static final String MESSAJE_TIPO_DESTINO_NO_EXISTE = "El tipo de destino no existe en el catalogo [bv_tipo_cuenta_spei].";

	// Códigos de validación
	public static final int CODIGO_FECHA_OPERACION_OBLIGATORIA = 93;
	public static final int CODIGO_FOLIO_CODI_OBLIGATORIO = 445;
	public static final int CODIGO_CLAVE_INSTITUCION_OBLIGATORIA = 5;
	public static final int CODIGO_TIPO_OPERACION_OBLIGATORIO = 81;
	public static final int CODIGO_CLAVE_RASTREO_OBLIGATORIO = 92;
	public static final int CODIGO_ESTADO_ENVIO_OBLIGATORIO = 98;
	public static final int CODIGO_TIPO_ORDEN_OBLIGATORIO = 106;
	public static final int CODIGO_PRIORIDAD_ORDEN_OBLIGATORIA = 85;
	public static final int CODIGO_TOPOLOGIA_OBLIGATORIA = 87;
	public static final int CODIGO_CLAVE_BANCO_USUARIO_OBLIGATORIA = 158;
	public static final int CODIGO_MONTO_OBLIGATORIO = 9;
	public static final int CODIGO_CLAVE_PAGO_OBLIGATORIA = 57;
	public static final int CODIGO_CUENTA_BENEFICIARIO_NUMERICA = 34;
	public static final int CODIGO_CUENTA_TARJETA_DEBITO = 38;
}
