package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spi.dto;

public class AccendoConnectionData {
	
    private String scope;
    private String grantType;
    private String clientSecret;
    private String clientId;
    private String authServerUrl;
    private String loginServerUrl;
    private String predefinedReferencesUrl;
    private String queryReferencesUrl;
    private String client;
    private String username;
    private String password;
    private String idCuentaOrdenante;
    private String token;
    private String accessToken;
    private String thirdPartyIUrl;
    private String apiKey;
    private String updateReferencesUrl;
    private String baseUrl;
    private String algorith;
    private String urlSession;
    private String urlSingleToken;
    private String urlRegistraSpei;
    private String appClient;
    private String companyId;
    private String trackingKeyPrefix;
    private String algnUri;
    private String certUri;
    private String speiDummy;
    private String timeInitDay;
    

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getGrantType() { return grantType; }
    public void setGrantType(String grantType) { this.grantType = grantType; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getAuthServerUrl() { return authServerUrl; }
    public void setAuthServerUrl(String authServerUrl) { this.authServerUrl = authServerUrl; }

    public String getLoginServerUrl() { return loginServerUrl; }
    public void setLoginServerUrl(String loginServerUrl) { this.loginServerUrl = loginServerUrl; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPredefinedReferencesUrl() { return predefinedReferencesUrl; }
    public void setPredefinedReferencesUrl(String predefinedReferencesUrl) { this.predefinedReferencesUrl = predefinedReferencesUrl; }

    public String getIdCuentaOrdenante() { return idCuentaOrdenante; }
    public void setIdCuentaOrdenante(String idCuentaOrdenante) { this.idCuentaOrdenante = idCuentaOrdenante; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getQueryReferencesUrl() { return queryReferencesUrl; }
    public void setQueryReferencesUrl(String queryReferencesUrl) { this.queryReferencesUrl = queryReferencesUrl; }

    public String getThirdPartyIUrl() { return thirdPartyIUrl; }
    public void setThirdPartyIUrl(String thirdPartyIUrl) { this.thirdPartyIUrl = thirdPartyIUrl; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getUpdateReferencesUrl() { return updateReferencesUrl; }
    public void setUpdateReferencesUrl(String updateReferencesUrl) { this.updateReferencesUrl = updateReferencesUrl; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
	public String getAlgorith() {
		return algorith;
	}
	public void setAlgorith(String algorith) {
		this.algorith = algorith;
	}
	public String getUrlSession() {
		return urlSession;
	}
	public void setUrlSession(String urlSession) {
		this.urlSession = urlSession;
	}
	public String getUrlSingleToken() {
		return urlSingleToken;
	}
	public void setUrlSingleToken(String urlSingleToken) {
		this.urlSingleToken = urlSingleToken;
	}
	public String getUrlRegistraSpei() {
		return urlRegistraSpei;
	}
	public void setUrlRegistraSpei(String urlRegistraSpei) {
		this.urlRegistraSpei = urlRegistraSpei;
	}
	public String getAppClient() {
		return appClient;
	}
	public void setAppClient(String appClient) {
		this.appClient = appClient;
	}

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getTrackingKeyPrefix() {
        return trackingKeyPrefix;
    }

    public void setTrackingKeyPrefix(String trackingKeyPrefix) {
        this.trackingKeyPrefix = trackingKeyPrefix;
    }

    public String getAlgnUri() { return algnUri; }

    public void setAlgnUri(String algnUri) { this.algnUri = algnUri; }

    public String getCertUri() { return certUri; }

    public void setCertUri(String certUri) { this.certUri = certUri; }

    public String getSpeiDummy() { return speiDummy; }

    public void setSpeiDummy(String speiDummy) { this.speiDummy = speiDummy; }

    public String getTimeInitDay() { return timeInitDay; }

    public void setTimeInitDay(String timeInitDay) { this.timeInitDay = timeInitDay; }

    @Override
    public String toString() {
        return "AccendoConnectionData{" +
                "scope='" + scope + '\'' +
                ", grantType='" + grantType + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", clientId='" + clientId + '\'' +
                ", authServerUrl='" + authServerUrl + '\'' +
                ", loginServerUrl='" + loginServerUrl + '\'' +
                ", predefinedReferencesUrl='" + predefinedReferencesUrl + '\'' +
                ", queryReferencesUrl='" + queryReferencesUrl + '\'' +
                ", client='" + client + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", idCuentaOrdenante='" + idCuentaOrdenante + '\'' +
                ", token='" + token + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", thirdPartyIUrl='" + thirdPartyIUrl + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", updateReferencesUrl='" + updateReferencesUrl + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", algorith='" + algorith + '\'' +
                ", urlSession='" + urlSession + '\'' +
                ", urlSingleToken='" + urlSingleToken + '\'' +
                ", urlRegistraSpei='" + urlRegistraSpei + '\'' +
                ", appClient='" + appClient + '\'' +
                ", timeInitDay='" + timeInitDay + '\'' +
                ", speiDummy='" + speiDummy + '\'' +
                '}';
    }
}
