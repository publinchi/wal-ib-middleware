package com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils;

import java.util.HashMap;
import java.util.Map;

public class ParameterValidationUtil {
    private String paramName; // Nombre del parámetro a validar
    private ValidationType type; // Tipo de validación
    private int errorCode; // Código de error
    private String errorMessage; // Mensaje de error
    private Map<String, Object> additionalParams; // Parámetros adicionales para validaciones

    // Constructor para validaciones generales
    public ParameterValidationUtil(String paramName, ValidationType type, ErrorCode error) {
        this(paramName, type, error.getCode(), error.getMessage(), new HashMap<>());
    }

    // Constructor que permite parámetros adicionales
    public ParameterValidationUtil(String paramName, ValidationType type, int errorCode, String errorMessage, Map<String, Object> additionalParams) {
        this.paramName = paramName;
        this.type = type;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.additionalParams = additionalParams != null ? additionalParams : new HashMap<>();
    }

    public String getParamName() {
        return paramName;
    }

    public ValidationType getType() {
        return type;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getAdditionalParam(String key) {
        return additionalParams.get(key);
    }

    public Map<String, Object> getAdditionalParams() {
        return additionalParams;
    }

    public void addAdditionalParam(String key, Object value) {
        additionalParams.put(key, value);
    }
}
