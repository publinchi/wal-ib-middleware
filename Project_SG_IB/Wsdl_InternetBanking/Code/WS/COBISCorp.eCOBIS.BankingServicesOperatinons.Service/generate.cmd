    
@echo off
set PATH=%2\bin;%PATH%
set JAVA_HOME=%2
ECHO generate.cmd 
ECHO JAVA_HOME=%JAVA_HOME%
set PROJECT_DIR=%~dp0

REM copio XSDs del DTO
		
    xcopy /Y /E /R "C:\Trafalgar\middleware_22\trf-ib-middleware\Project_SG_IB\Wsdl_InternetBanking\Code\WS\COBISCorp.eCOBIS.BankingServicesOperation.DTO\webapp\wsdl" "%PROJECT_DIR%\webapp\wsdl" /i
  
    
REM elimino el backslash final
set PROJECT_DIR=%PROJECT_DIR:~0,-1%
if exist "%PROJECT_DIR%\src" rd /S /Q %PROJECT_DIR%\src
mkdir "%PROJECT_DIR%\src"
xcopy /Y /E /R "%PROJECT_DIR%\build\src" "%PROJECT_DIR%\src"
wsimport "%PROJECT_DIR%/webapp/wsdl/COBISCorp.eCOBIS.BankingServicesOperatinons.Service.wsdl" -d "%PROJECT_DIR%\src" -b "%PROJECT_DIR%/webapp/wsdl/binding.jxb" -Xnocompile
  REM copia interface de personalizacion
xcopy /Y /R "%PROJECT_DIR%\ICustomCode.java" "%PROJECT_DIR%\src\cobiscorp\ecobis\util\" /i
del /F /Q "%PROJECT_DIR%\ICustomCode.java"

  REM copia interface de autenticación personalizada
xcopy /Y /R "%PROJECT_DIR%\ICustomAuthentication.java" "%PROJECT_DIR%\src\cobiscorp\ecobis\util\" /i
del /F /Q "%PROJECT_DIR%\ICustomAuthentication.java"

		