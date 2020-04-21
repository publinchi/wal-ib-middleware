
package com.cobiscorp.ecobis.ws.client.linkser;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.cobiscorp.ecobis.ws.client.linkser package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EstadoCtaTcPCodigoLinea_QNAME = new QName("http://tempuri.org/", "pCodigoLinea");
    private final static QName _EstadoCtaTcPCodigoCliente_QNAME = new QName("http://tempuri.org/", "pCodigoCliente");
    private final static QName _GetUserInformationResponseGetUserInformationResult_QNAME = new QName("http://tempuri.org/", "GetUserInformationResult");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _UserInfo_QNAME = new QName("http://schemas.datacontract.org/2004/07/TsmBoServicio", "UserInfo");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _TarCredClienteResponseTarCredClienteResult_QNAME = new QName("http://tempuri.org/", "TarCred_ClienteResult");
    private final static QName _ConsultaTarjetasCreResponseConsultaTarjetasCreResult_QNAME = new QName("http://tempuri.org/", "Consulta_Tarjetas_CreResult");
    private final static QName _WSGETONLINEPAYMENTResponseWSGETONLINEPAYMENTResult_QNAME = new QName("http://tempuri.org/", "WSGETONLINEPAYMENTResult");
    private final static QName _WSGETONLINEPAYMENTPIdLineaCreditoCobis_QNAME = new QName("http://tempuri.org/", "pIdLineaCreditoCobis");
    private final static QName _WSGETONLINEPAYMENTPMonto_QNAME = new QName("http://tempuri.org/", "pMonto");
    private final static QName _WSGETONLINEPAYMENTPIdFecha_QNAME = new QName("http://tempuri.org/", "pIdFecha");
    private final static QName _WSGETONLINEPAYMENTPIdMoneda_QNAME = new QName("http://tempuri.org/", "pIdMoneda");
    private final static QName _WSGETONLINEPAYMENTPIdDocumentoCliente_QNAME = new QName("http://tempuri.org/", "pIdDocumentoCliente");
    private final static QName _WSGETONLINEPAYMENTPIdClienteCobis_QNAME = new QName("http://tempuri.org/", "pIdClienteCobis");
    private final static QName _WSGETONLINEPAYMENTPModPago_QNAME = new QName("http://tempuri.org/", "pModPago");
    private final static QName _DetalleTarjetaResponseDetalleTarjetaResult_QNAME = new QName("http://tempuri.org/", "Detalle_TarjetaResult");
    private final static QName _AumentoCupoPNombreTitular_QNAME = new QName("http://tempuri.org/", "pNombreTitular");
    private final static QName _AumentoCupoPCodigoCobis_QNAME = new QName("http://tempuri.org/", "pCodigoCobis");
    private final static QName _AumentoCupoPDireccion_QNAME = new QName("http://tempuri.org/", "pDireccion");
    private final static QName _AumentoCupoPActividad_QNAME = new QName("http://tempuri.org/", "pActividad");
    private final static QName _AumentoCupoPEvaluador_QNAME = new QName("http://tempuri.org/", "pEvaluador");
    private final static QName _AumentoCupoPFechaAprobacion_QNAME = new QName("http://tempuri.org/", "pFechaAprobacion");
    private final static QName _AumentoCupoPIdTitular_QNAME = new QName("http://tempuri.org/", "pIdTitular");
    private final static QName _AumentoCupoPNroOperacion_QNAME = new QName("http://tempuri.org/", "pNro_Operacion");
    private final static QName _ConsultaMovResponseConsultaMovResult_QNAME = new QName("http://tempuri.org/", "Consulta_movResult");
    private final static QName _EstadoCtaTcResponseEstadoCtaTcResult_QNAME = new QName("http://tempuri.org/", "Estado_cta_tcResult");
    private final static QName _ConsultaMovPFechaInicio_QNAME = new QName("http://tempuri.org/", "pFechaInicio");
    private final static QName _ConsultaMovPFechaFin_QNAME = new QName("http://tempuri.org/", "pFechaFin");
    private final static QName _ConsultaMovPNoMov_QNAME = new QName("http://tempuri.org/", "pNo_mov");
    private final static QName _LineaAprobadaResponseLineaAprobadaResult_QNAME = new QName("http://tempuri.org/", "Linea_AprobadaResult");
    private final static QName _UserInfoSecurityServiceContextUsername_QNAME = new QName("http://schemas.datacontract.org/2004/07/TsmBoServicio", "SecurityServiceContextUsername");
    private final static QName _UserInfoThreadCurrentPrincipalUsername_QNAME = new QName("http://schemas.datacontract.org/2004/07/TsmBoServicio", "ThreadCurrentPrincipalUsername");
    private final static QName _ConsultaTarjetasCrePCocigoCliente_QNAME = new QName("http://tempuri.org/", "pCocigoCliente");
    private final static QName _AumentoCupoResponseAumentoCupoResult_QNAME = new QName("http://tempuri.org/", "Aumento_cupoResult");
    private final static QName _LineaAprobadaPMoneda_QNAME = new QName("http://tempuri.org/", "pMoneda");
    private final static QName _LineaAprobadaPOficina_QNAME = new QName("http://tempuri.org/", "pOficina");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.cobiscorp.ecobis.ws.client.linkser
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EstadoCtaTc }
     * 
     */
    public EstadoCtaTc createEstadoCtaTc() {
        return new EstadoCtaTc();
    }

    /**
     * Create an instance of {@link GetUserInformationResponse }
     * 
     */
    public GetUserInformationResponse createGetUserInformationResponse() {
        return new GetUserInformationResponse();
    }

    /**
     * Create an instance of {@link TarCredClienteResponse }
     * 
     */
    public TarCredClienteResponse createTarCredClienteResponse() {
        return new TarCredClienteResponse();
    }

    /**
     * Create an instance of {@link ConsultaTarjetasCreResponse }
     * 
     */
    public ConsultaTarjetasCreResponse createConsultaTarjetasCreResponse() {
        return new ConsultaTarjetasCreResponse();
    }

    /**
     * Create an instance of {@link WSGETONLINEPAYMENTResponse }
     * 
     */
    public WSGETONLINEPAYMENTResponse createWSGETONLINEPAYMENTResponse() {
        return new WSGETONLINEPAYMENTResponse();
    }

    /**
     * Create an instance of {@link WSGETONLINEPAYMENT }
     * 
     */
    public WSGETONLINEPAYMENT createWSGETONLINEPAYMENT() {
        return new WSGETONLINEPAYMENT();
    }

    /**
     * Create an instance of {@link DetalleTarjetaResponse }
     * 
     */
    public DetalleTarjetaResponse createDetalleTarjetaResponse() {
        return new DetalleTarjetaResponse();
    }

    /**
     * Create an instance of {@link AumentoCupo }
     * 
     */
    public AumentoCupo createAumentoCupo() {
        return new AumentoCupo();
    }

    /**
     * Create an instance of {@link ConsultaMovResponse }
     * 
     */
    public ConsultaMovResponse createConsultaMovResponse() {
        return new ConsultaMovResponse();
    }

    /**
     * Create an instance of {@link EstadoCtaTcResponse }
     * 
     */
    public EstadoCtaTcResponse createEstadoCtaTcResponse() {
        return new EstadoCtaTcResponse();
    }

    /**
     * Create an instance of {@link GetUserInformation }
     * 
     */
    public GetUserInformation createGetUserInformation() {
        return new GetUserInformation();
    }

    /**
     * Create an instance of {@link TarCredCliente }
     * 
     */
    public TarCredCliente createTarCredCliente() {
        return new TarCredCliente();
    }

    /**
     * Create an instance of {@link ConsultaMov }
     * 
     */
    public ConsultaMov createConsultaMov() {
        return new ConsultaMov();
    }

    /**
     * Create an instance of {@link LineaAprobadaResponse }
     * 
     */
    public LineaAprobadaResponse createLineaAprobadaResponse() {
        return new LineaAprobadaResponse();
    }

    /**
     * Create an instance of {@link DetalleTarjeta }
     * 
     */
    public DetalleTarjeta createDetalleTarjeta() {
        return new DetalleTarjeta();
    }

    /**
     * Create an instance of {@link ConsultaTarjetasCre }
     * 
     */
    public ConsultaTarjetasCre createConsultaTarjetasCre() {
        return new ConsultaTarjetasCre();
    }

    /**
     * Create an instance of {@link UserInfo }
     * 
     */
    public UserInfo createUserInfo() {
        return new UserInfo();
    }

    /**
     * Create an instance of {@link AumentoCupoResponse }
     * 
     */
    public AumentoCupoResponse createAumentoCupoResponse() {
        return new AumentoCupoResponse();
    }

    /**
     * Create an instance of {@link LineaAprobada }
     * 
     */
    public LineaAprobada createLineaAprobada() {
        return new LineaAprobada();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoLinea", scope = EstadoCtaTc.class)
    public JAXBElement<String> createEstadoCtaTcPCodigoLinea(String value) {
        return new JAXBElement<String>(_EstadoCtaTcPCodigoLinea_QNAME, String.class, EstadoCtaTc.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoCliente", scope = EstadoCtaTc.class)
    public JAXBElement<String> createEstadoCtaTcPCodigoCliente(String value) {
        return new JAXBElement<String>(_EstadoCtaTcPCodigoCliente_QNAME, String.class, EstadoCtaTc.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetUserInformationResult", scope = GetUserInformationResponse.class)
    public JAXBElement<UserInfo> createGetUserInformationResponseGetUserInformationResult(UserInfo value) {
        return new JAXBElement<UserInfo>(_GetUserInformationResponseGetUserInformationResult_QNAME, UserInfo.class, GetUserInformationResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/TsmBoServicio", name = "UserInfo")
    public JAXBElement<UserInfo> createUserInfo(UserInfo value) {
        return new JAXBElement<UserInfo>(_UserInfo_QNAME, UserInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "TarCred_ClienteResult", scope = TarCredClienteResponse.class)
    public JAXBElement<String> createTarCredClienteResponseTarCredClienteResult(String value) {
        return new JAXBElement<String>(_TarCredClienteResponseTarCredClienteResult_QNAME, String.class, TarCredClienteResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Consulta_Tarjetas_CreResult", scope = ConsultaTarjetasCreResponse.class)
    public JAXBElement<String> createConsultaTarjetasCreResponseConsultaTarjetasCreResult(String value) {
        return new JAXBElement<String>(_ConsultaTarjetasCreResponseConsultaTarjetasCreResult_QNAME, String.class, ConsultaTarjetasCreResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "WSGETONLINEPAYMENTResult", scope = WSGETONLINEPAYMENTResponse.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTResponseWSGETONLINEPAYMENTResult(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTResponseWSGETONLINEPAYMENTResult_QNAME, String.class, WSGETONLINEPAYMENTResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pIdLineaCreditoCobis", scope = WSGETONLINEPAYMENT.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTPIdLineaCreditoCobis(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPIdLineaCreditoCobis_QNAME, String.class, WSGETONLINEPAYMENT.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pMonto", scope = WSGETONLINEPAYMENT.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTPMonto(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPMonto_QNAME, String.class, WSGETONLINEPAYMENT.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pIdFecha", scope = WSGETONLINEPAYMENT.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTPIdFecha(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPIdFecha_QNAME, String.class, WSGETONLINEPAYMENT.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pIdMoneda", scope = WSGETONLINEPAYMENT.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTPIdMoneda(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPIdMoneda_QNAME, String.class, WSGETONLINEPAYMENT.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pIdDocumentoCliente", scope = WSGETONLINEPAYMENT.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTPIdDocumentoCliente(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPIdDocumentoCliente_QNAME, String.class, WSGETONLINEPAYMENT.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pIdClienteCobis", scope = WSGETONLINEPAYMENT.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTPIdClienteCobis(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPIdClienteCobis_QNAME, String.class, WSGETONLINEPAYMENT.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pModPago", scope = WSGETONLINEPAYMENT.class)
    public JAXBElement<String> createWSGETONLINEPAYMENTPModPago(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPModPago_QNAME, String.class, WSGETONLINEPAYMENT.class, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Detalle_TarjetaResult", scope = DetalleTarjetaResponse.class)
    public JAXBElement<String> createDetalleTarjetaResponseDetalleTarjetaResult(String value) {
        return new JAXBElement<String>(_DetalleTarjetaResponseDetalleTarjetaResult_QNAME, String.class, DetalleTarjetaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pMonto", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPMonto(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPMonto_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pNombreTitular", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPNombreTitular(String value) {
        return new JAXBElement<String>(_AumentoCupoPNombreTitular_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoCobis", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPCodigoCobis(String value) {
        return new JAXBElement<String>(_AumentoCupoPCodigoCobis_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pDireccion", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPDireccion(String value) {
        return new JAXBElement<String>(_AumentoCupoPDireccion_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pActividad", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPActividad(String value) {
        return new JAXBElement<String>(_AumentoCupoPActividad_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pEvaluador", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPEvaluador(String value) {
        return new JAXBElement<String>(_AumentoCupoPEvaluador_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pFechaAprobacion", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPFechaAprobacion(String value) {
        return new JAXBElement<String>(_AumentoCupoPFechaAprobacion_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pIdTitular", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPIdTitular(String value) {
        return new JAXBElement<String>(_AumentoCupoPIdTitular_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pNro_Operacion", scope = AumentoCupo.class)
    public JAXBElement<String> createAumentoCupoPNroOperacion(String value) {
        return new JAXBElement<String>(_AumentoCupoPNroOperacion_QNAME, String.class, AumentoCupo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Consulta_movResult", scope = ConsultaMovResponse.class)
    public JAXBElement<String> createConsultaMovResponseConsultaMovResult(String value) {
        return new JAXBElement<String>(_ConsultaMovResponseConsultaMovResult_QNAME, String.class, ConsultaMovResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Estado_cta_tcResult", scope = EstadoCtaTcResponse.class)
    public JAXBElement<String> createEstadoCtaTcResponseEstadoCtaTcResult(String value) {
        return new JAXBElement<String>(_EstadoCtaTcResponseEstadoCtaTcResult_QNAME, String.class, EstadoCtaTcResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoCliente", scope = TarCredCliente.class)
    public JAXBElement<String> createTarCredClientePCodigoCliente(String value) {
        return new JAXBElement<String>(_EstadoCtaTcPCodigoCliente_QNAME, String.class, TarCredCliente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pFechaInicio", scope = ConsultaMov.class)
    public JAXBElement<String> createConsultaMovPFechaInicio(String value) {
        return new JAXBElement<String>(_ConsultaMovPFechaInicio_QNAME, String.class, ConsultaMov.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pFechaFin", scope = ConsultaMov.class)
    public JAXBElement<String> createConsultaMovPFechaFin(String value) {
        return new JAXBElement<String>(_ConsultaMovPFechaFin_QNAME, String.class, ConsultaMov.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoLinea", scope = ConsultaMov.class)
    public JAXBElement<String> createConsultaMovPCodigoLinea(String value) {
        return new JAXBElement<String>(_EstadoCtaTcPCodigoLinea_QNAME, String.class, ConsultaMov.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pNo_mov", scope = ConsultaMov.class)
    public JAXBElement<String> createConsultaMovPNoMov(String value) {
        return new JAXBElement<String>(_ConsultaMovPNoMov_QNAME, String.class, ConsultaMov.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoCliente", scope = ConsultaMov.class)
    public JAXBElement<String> createConsultaMovPCodigoCliente(String value) {
        return new JAXBElement<String>(_EstadoCtaTcPCodigoCliente_QNAME, String.class, ConsultaMov.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Linea_AprobadaResult", scope = LineaAprobadaResponse.class)
    public JAXBElement<String> createLineaAprobadaResponseLineaAprobadaResult(String value) {
        return new JAXBElement<String>(_LineaAprobadaResponseLineaAprobadaResult_QNAME, String.class, LineaAprobadaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoLinea", scope = DetalleTarjeta.class)
    public JAXBElement<String> createDetalleTarjetaPCodigoLinea(String value) {
        return new JAXBElement<String>(_EstadoCtaTcPCodigoLinea_QNAME, String.class, DetalleTarjeta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/TsmBoServicio", name = "SecurityServiceContextUsername", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoSecurityServiceContextUsername(String value) {
        return new JAXBElement<String>(_UserInfoSecurityServiceContextUsername_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/TsmBoServicio", name = "ThreadCurrentPrincipalUsername", scope = UserInfo.class)
    public JAXBElement<String> createUserInfoThreadCurrentPrincipalUsername(String value) {
        return new JAXBElement<String>(_UserInfoThreadCurrentPrincipalUsername_QNAME, String.class, UserInfo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCocigoCliente", scope = ConsultaTarjetasCre.class)
    public JAXBElement<String> createConsultaTarjetasCrePCocigoCliente(String value) {
        return new JAXBElement<String>(_ConsultaTarjetasCrePCocigoCliente_QNAME, String.class, ConsultaTarjetasCre.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Aumento_cupoResult", scope = AumentoCupoResponse.class)
    public JAXBElement<String> createAumentoCupoResponseAumentoCupoResult(String value) {
        return new JAXBElement<String>(_AumentoCupoResponseAumentoCupoResult_QNAME, String.class, AumentoCupoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pMonto", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPMonto(String value) {
        return new JAXBElement<String>(_WSGETONLINEPAYMENTPMonto_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pMoneda", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPMoneda(String value) {
        return new JAXBElement<String>(_LineaAprobadaPMoneda_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pNombreTitular", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPNombreTitular(String value) {
        return new JAXBElement<String>(_AumentoCupoPNombreTitular_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pCodigoCobis", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPCodigoCobis(String value) {
        return new JAXBElement<String>(_AumentoCupoPCodigoCobis_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pDireccion", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPDireccion(String value) {
        return new JAXBElement<String>(_AumentoCupoPDireccion_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pActividad", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPActividad(String value) {
        return new JAXBElement<String>(_AumentoCupoPActividad_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pEvaluador", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPEvaluador(String value) {
        return new JAXBElement<String>(_AumentoCupoPEvaluador_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pOficina", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPOficina(String value) {
        return new JAXBElement<String>(_LineaAprobadaPOficina_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pFechaAprobacion", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPFechaAprobacion(String value) {
        return new JAXBElement<String>(_AumentoCupoPFechaAprobacion_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pIdTitular", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPIdTitular(String value) {
        return new JAXBElement<String>(_AumentoCupoPIdTitular_QNAME, String.class, LineaAprobada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "pNro_Operacion", scope = LineaAprobada.class)
    public JAXBElement<String> createLineaAprobadaPNroOperacion(String value) {
        return new JAXBElement<String>(_AumentoCupoPNroOperacion_QNAME, String.class, LineaAprobada.class, value);
    }

}
