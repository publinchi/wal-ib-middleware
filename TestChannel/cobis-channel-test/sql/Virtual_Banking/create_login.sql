
use cob_bvirtual
go

/**
 * Creacion del usuario test para banca virtual
 * Requisitos ambiente con sql server y con las estructuras de banca virtual como la base
 * cob_bvritual, etc
 *
 * Clave Default para WEB
 * Login:testCts
 * Pass:Aa0000000000*
 * 
 * Login:testCtsEmp
 * Pass:Aa0000000000 
 * 
 * Login:testCtsEmpA
 * Pass:Aa0000000000
 * 
 * Login:testCtsGrupo
 * Pass:12345678
 * 
 */


declare @service_id int,
        @service_id_mb int,
        --ENTE NATURAL
        @ente_prueba int,
        @ente_mis_prueba int,
        @ente_mis_prueba2 int,
        @login varchar(50),
        @login_identificaion varchar(50),
        @perfil_name varchar(50),
        @perfil_id int,
        @perfil_name_mb varchar(50),
        @perfil_id_mb int,
        @ente_prueba_name varchar(50),
        
        --ENTE EMPRESA
        @ente_prueba_empresa int,
        @ente_mis_prueba_empresa int,
        @login_empresa varchar(50),
        @login_identificaion_empresa varchar(50),
        @perfil_name_empresa varchar(50),
        @perfil_id_empresa int,
         --ENTE EMPRESA TIPO A
        @ente_prueba_empresa_A int,
        @ente_mis_prueba_empresa_A int,
        @login_empresa_A varchar(50),
        @login_identificaion_empresa_A varchar(50),
        @perfil_name_empresa_A varchar(50),
        @perfil_id_empresa_A int,
        
        --ENTE GRUPO
        @ente_prueba_grupo int,
        @ente_mis_prueba_grupo int,
        @login_grupo varchar(50),
        @login_identificaion_grupo varchar(50),
        @perfil_name_grupo varchar(50),
        @perfil_id_grupo int,
        
        --CUENTA CORRIENTE PARA TRANSACCIONAR
        @acc_ctacte_number varchar(20),
        @acc_ctacte_number2 varchar(20),
		@acc_ctacte_number3 varchar(20),
        @acc_ctacte_type_product int,
        @acc_ctacte_type_currency int,
        
        @acc_ctacte_number_USD varchar(20),
        @acc_ctacte_type_product_USD int,
        @acc_ctacte_type_currency_USD int,
        
        @acc2_ctacte_number_USD varchar(20),
        @acc2_ctacte_type_product_USD int,
        @acc2_ctacte_type_currency_USD int,
        
        --CUENTA CORRIENTE PARA TRANSACCIONAR CHEQUERA
        @acc_ctacte_number_chequera varchar(20),

        --CUENTA CORRIENTE PARA TRANSACCIONAR CON DOBLE AUTORIZACION EMPRESA
        @acc_ctacte_number_double_auth_empresa varchar(20),
        @acc_ctacte_number_double_auth_empresa2 varchar(20),
        @acc_ctacte_type_product_double_auth_empresa int,
        @acc_ctacte_type_currency_double_auth_empresa int,
        --CUENTA AHORRO PARA TRANSACCIONAR CON DOBLE AUTORIZACION EMPRESA
        @acc_ctaaho_number_double_auth_empresa varchar(20),
        @acc_ctaaho_type_product_double_auth_empresa int,
        @acc_ctaaho_type_currency_double_auth_empresa int,
        --CUENTA AHORRO PARA TRANSACCIONAR
        @acc_ctaaho_number varchar(20),
        @acc_ctaaho_number2 varchar(20),
		@acc_ctaaho_number3 varchar(20),
        @acc_ctaaho_type_product int,
        @acc_ctaaho_type_currency int,
        --CUENTA PLAZO FIJO PARA TRANSACCIONAR
        @acc_dpf_number varchar(20),
        @acc_dpf_type_product int,
        @acc_dpf_type_currency int,
        --CUENTA PLAZO FIJO PARA TRANSACCIONAR EMPRESA
        @acc_dpf_number2 varchar(20),
        @acc_dpf_type_product2 int,
        @acc_dpf_type_currency2 int,

		--CUENTA PLAZO FIJO PARA TRANSACCIONAR
        @acc_dpf_number3 varchar(20),
        @acc_dpf_type_product3 int,
        @acc_dpf_type_currency3 int,	

        --CUENTA PLAZO FIJO PARA TRANSACCIONAR EMPRESA
        @acc_dpf_number4 varchar(20),
        @acc_dpf_type_product4 int,
        @acc_dpf_type_currency4 int,

        --CUENTA CARTERA PARA TRANSACCIONAR
        @acc_car_number varchar(20),
        @acc_car_number1 varchar(20),
        @acc_car_number2 varchar(20),
        @acc_car_type_product int,
        @acc_car_type_currency int,
        @med_envio_siguiente int,
        --DESTINO PARA PRUEBAS DE TRANSFERENCIAS A TERCERO
        @des_acc_ctacte_number varchar(20),
        @des_acc_ctacte_type_product int,
        @des_acc_ctacte_type_currency int,
        --TARJETA CREDITO PARA TRANSACCIONAR
        @acc_tar_number varchar(20),
        @acc_tar_number2 varchar(20),
        @acc_tar_type_product int,
        @acc_tar_type_currency int,
		--TARJETA CREDITO PARA TRANSACCIONAR
        @acc_tar_number_USD varchar(20),
        @acc_tar_type_product_USD int,
        @acc_tar_type_currency_USD int,
	    --CUENTA PRESTAMO
		@acc_ptm_number varchar(20),
        @acc_ptm_type_product int,
        @acc_ptm_type_currency int,
		--CUENTA AHORRO PROGRAMADO
		@acc_ctaaho_prog_number varchar(20),
        @acc_ctaaho_porg_product int,
        @acc_ctaaho_prog_type_currency int,
        --solicitud cheque gerencia
        @acc_ctacte_type_cheque int,
        @acc_ctacte_number_cheque varchar(24),
		--Comercio Exterior
		@acc_comext_trr varchar(24),
		@acc_comext_type_product int,
		@acc_comext_type_currency int
		
               

/**
 * Inicializacion de los parametros Base
 */

select  @service_id=1,
        @service_id_mb=8,
        --ENTE NATURAL
        @ente_prueba=277,
        @login ='testCts',
        @login_identificaion='1234567890',
        @perfil_name='PERFIL PERSONAL',
        @ente_prueba_name ='TEST CLIENT PERSON',
        @perfil_name_mb ='USUARIO SB',
        --ENTE EMPRESA
        @ente_prueba_empresa=279,
        @login_empresa ='testCtsEmp',
        @login_identificaion_empresa='1234567890',
        @perfil_name_empresa='PERFIL EMPRESARIAL',
        
        --ENTE EMPRESA TIPO A
        @ente_prueba_empresa_A=279,
        @login_empresa_A ='testCtsEmpA',
        @login_identificaion_empresa_A='1234567890',
        @perfil_name_empresa_A='PERFIL EMPRESARIAL',
        
        --ENTE GRUPO
        @ente_prueba_grupo=295,
        @login_grupo ='testCtsGrupo',
        @login_identificaion_grupo='1234567890',
        @perfil_name_grupo='ADMINISTRADOR',
        
        --CTACTE
        @acc_ctacte_number='10410108275406111',
        @acc_ctacte_number2='01202000052',
		@acc_ctacte_number3='10410108259405015',
        @acc_ctacte_type_product=3,
        @acc_ctacte_type_currency=0,
        
        --CTACTE
        @acc_ctacte_number_USD='10410108640405011',
        @acc_ctacte_type_product_USD=3,
        @acc_ctacte_type_currency_USD=17,
        
        @acc2_ctacte_number_USD='10410108275402927',
        @acc2_ctacte_type_product_USD=3,
        @acc2_ctacte_type_currency_USD=17,

        @acc_ctacte_number_chequera = '10410108275405315',
        
        --CTACTE DOBLE AUTORIZACION EMPRESA
        @acc_ctacte_number_double_auth_empresa='10410108275407019',
        @acc_ctacte_number_double_auth_empresa2='01202000187',
        @acc_ctacte_type_product_double_auth_empresa=3,
        @acc_ctacte_type_currency_double_auth_empresa=0,
        --CTACTE DOBLE AUTORIZACION EMPRESA
        @acc_ctaaho_number_double_auth_empresa='10410000005233616',
        @acc_ctaaho_type_product_double_auth_empresa=4,
        @acc_ctaaho_type_currency_double_auth_empresa=0,
        --CTAAHO
        @acc_ctaaho_number='10410108275249013',
        @acc_ctaaho_number2='10410108275248120',
		@acc_ctaaho_number3='10410000005233616',
        @acc_ctaaho_type_product=4,
        @acc_ctaaho_type_currency=0,
        --DPF
        @acc_dpf_number='01414052458',
        @acc_dpf_type_product=14,
        @acc_dpf_type_currency=0,
        --DPF EMPRESA
        @acc_dpf_number2='28414118610',
        @acc_dpf_type_product2=14,
        @acc_dpf_type_currency2=17,
    	--DPF
		@acc_dpf_number3 ='26414025968',
        @acc_dpf_type_product3= 14,
        @acc_dpf_type_currency3=17,	
        --DPF EMPRESA
        @acc_dpf_number4='28414120590',
        @acc_dpf_type_product4=14,
        @acc_dpf_type_currency4=17,
        --CAR
        @acc_car_number='10407740700943818',
        @acc_car_number1='10410108232700018',
        @acc_car_number2='10410000041700201',
        @acc_car_type_product=7,
        @acc_car_type_currency=0,
        --DESTINO PARA PRUEBAS DE TRANSFERENCIAS A TERCERO
        @des_acc_ctacte_number='10410108595405215',
        @des_acc_ctacte_type_product=3,
        @des_acc_ctacte_type_currency=0,
        --TARJETA CREDITO PARA TRANSACCIONAR
        @acc_tar_number ='5041960000000013',
        @acc_tar_number2 ='1234567890000000',
        @acc_tar_type_product = 83,
        @acc_tar_type_currency =0,
		--CUENTA ATM
        @acc_tar_number_USD = '5041960000000013',
        @acc_tar_type_product_USD =16,
        @acc_tar_type_currency_USD =17,
	    --CUENTA PRESTAMO
		@acc_ptm_number = '10410000041700201',
        @acc_ptm_type_product =7,
        @acc_ptm_type_currency =0,
		--CUENTA AHORRO PROGRAMADO
		@acc_ctaaho_prog_number ='10410108275408101',
        @acc_ctaaho_porg_product =8,
        @acc_ctaaho_prog_type_currency =0,
        --SOLOCITUD CHEQUE GERENCIA
		@acc_ctacte_type_cheque=4,
		@acc_ctacte_number_cheque='10410108275406806',
		
		--COMEXT
		@acc_comext_trr = 'TRR00108000022',
		@acc_comext_type_product = 9,
		@acc_comext_type_currency = 0
		

select @ente_mis_prueba=en_ente_mis from cob_bvirtual..bv_ente where en_ente=@ente_prueba
select @ente_mis_prueba_empresa=en_ente_mis from cob_bvirtual..bv_ente where en_ente=@ente_prueba_empresa
select @ente_mis_prueba_empresa_A=en_ente_mis from cob_bvirtual..bv_ente where en_ente=@ente_prueba_empresa_A
select @ente_mis_prueba_grupo=en_ente_mis from cob_bvirtual..bv_ente where en_ente=@ente_prueba_grupo
print @ente_mis_prueba_grupo
/*
 * ---------------------------------------------------------------------------------------------------------------------------------
 * Asignacion de Canales ente natural
 */
delete from cob_bvirtual..bv_ente_servicio where es_ente=@ente_prueba
insert into cob_bvirtual..bv_ente_servicio(es_ente,es_servicio,es_estado,es_creador,es_fecha_mod,es_fecha_reg) values(@ente_prueba,@service_id,'V','admuser',getdate(),getdate())
insert into cob_bvirtual..bv_ente_servicio(es_ente,es_servicio,es_estado,es_creador,es_fecha_mod,es_fecha_reg) values(@ente_prueba,@service_id_mb,'V','admuser',getdate(),getdate())


/*
 * ---------------------------------------------------------------------------------------------------------------------------------
 * Creacion del login natural Internet
 */
if exists(select 1 from cob_bvirtual..bv_login where lo_login=@login and lo_ente=@ente_prueba and lo_servicio=@service_id)
begin
    --Se autoriza y/o actualiza estados de vigencia,reintentos y autorizacion el login de prueba
    update cob_bvirtual..bv_login
    set lo_estado='A',lo_autorizado='S', lo_intento_1 = 0,lo_intento_6 = 0,lo_clave_temp='56DFEBC99B6FE99F1317E2D75448FD791305D47144A968167E1909E83DC66892',lo_clave_def='FC0CFD1CC67BFB20552868E2F474A09213669D15E2BE6083E504A969B69CC94B'
    where lo_login=@login and lo_ente=@ente_prueba and lo_servicio=@service_id
end
else
begin
    --Se realiza la creacion del login de prueba
    insert into cob_bvirtual..bv_login (
        lo_ente,                    lo_servicio,        lo_login,
        lo_clave_temp,              lo_clave_def,       lo_fecha_reg,
        lo_fecha_mod,               lo_dias_vigencia,   lo_parametro,
        lo_tipo_vigencia,           lo_renovable,       lo_fecha_ult_pwd,
        lo_hora,                    lo_descripcion,     lo_tipo_autorizacion,
        lo_autorizado,              lo_cambiar_login,   lo_estado,
        lo_usar_mimenu,             lo_estilo,          lo_lenguaje,
        lo_intento_1,               lo_intento_2,       lo_intento_5,
        lo_intento_6,               lo_motivo_reimp,    lo_clave_gen,
        lo_clave_imp,               lo_carga_pagina,    lo_fecha_ult_int,
        lo_fecha_ult_ing,           lo_fecha_ult_ing2,  lo_empresa,
        lo_autoriz_imp,             lo_afiliador,       lo_oficina,
        lo_impresor,                lo_fecha_ult_imp,   lo_clave_imp_cobro,
                             lo_cultura,         lo_tema,
        lo_numero_autorizacion,     lo_clave_mail,      lo_tip_envio,
        lo_login_personal )
    values (
        @ente_prueba,   @service_id,            @login,
        '56DFEBC99B6FE99F1317E2D75448FD791305D47144A968167E1909E83DC66892', 'FC0CFD1CC67BFB20552868E2F474A09213669D15E2BE6083E504A969B69CC94B', getdate(),
        getdate(),      NULL,                   NULL,
        'I',            'S',                    getdate(),
        getdate(),      'TEST REGRESION BV',    'A',
        'S',            'S',                    'A',
        NULL,           NULL,                   NULL,
        0,              0,                      0,
        0,              '0 ',                   3,
        3,              'I',                    NULL,
        getdate(),      NULL,                   NULL,
        'S',            'sa',                   1,
        'sa',           getdate(),              0,
                    'ES-EC',                NULL,
        NULL,           0,                      'I',
        @login
    )
end

/*
 * ---------------------------------------------------------------------------------------------------------------------------------
 * Creacion del login natural Mobile Banking
 */
if exists(select 1 from cob_bvirtual..bv_login where lo_login=@login and lo_ente=@ente_prueba and lo_servicio=@service_id_mb)
begin
    --Se autoriza y/o actualiza estados de vigencia,reintentos y autorizacion el login de prueba
    update cob_bvirtual..bv_login
    set lo_estado='A',lo_autorizado='S', lo_intento_1 = 0,lo_intento_6 = 0,lo_clave_temp='56DFEBC99B6FE99F1317E2D75448FD791305D47144A968167E1909E83DC66892',lo_clave_def='FC0CFD1CC67BFB20552868E2F474A09213669D15E2BE6083E504A969B69CC94B'
    where lo_login=@login and lo_ente=@ente_prueba and lo_servicio=@service_id_mb
end
else
begin
    --Se realiza la creacion del login de prueba
    insert into cob_bvirtual..bv_login (
        lo_ente,                    lo_servicio,        lo_login,
        lo_clave_temp,              lo_clave_def,       lo_fecha_reg,
        lo_fecha_mod,               lo_dias_vigencia,   lo_parametro,
        lo_tipo_vigencia,           lo_renovable,       lo_fecha_ult_pwd,
        lo_hora,                    lo_descripcion,     lo_tipo_autorizacion,
        lo_autorizado,              lo_cambiar_login,   lo_estado,
        lo_usar_mimenu,             lo_estilo,          lo_lenguaje,
        lo_intento_1,               lo_intento_2,       lo_intento_5,
        lo_intento_6,               lo_motivo_reimp,    lo_clave_gen,
        lo_clave_imp,               lo_carga_pagina,    lo_fecha_ult_int,
        lo_fecha_ult_ing,           lo_fecha_ult_ing2,  lo_empresa,
        lo_autoriz_imp,             lo_afiliador,       lo_oficina,
        lo_impresor,                lo_fecha_ult_imp,   lo_clave_imp_cobro,
                             lo_cultura,         lo_tema,
        lo_numero_autorizacion,     lo_clave_mail,      lo_tip_envio,
        lo_login_personal )
    values (
        @ente_prueba,   @service_id_mb,            @login,
        '56DFEBC99B6FE99F1317E2D75448FD791305D47144A968167E1909E83DC66892', 'FC0CFD1CC67BFB20552868E2F474A09213669D15E2BE6083E504A969B69CC94B', getdate(),
        getdate(),      NULL,                   NULL,
        'I',            'S',                    getdate(),
        getdate(),      'TEST REGRESION BV',    'A',
        'S',            'S',                    'A',
        NULL,           NULL,                   NULL,
        0,              0,                      0,
        0,              '0 ',                   3,
        3,              'I',                    NULL,
        getdate(),      NULL,                   NULL,
        'S',            'sa',                   1,
        'sa',           getdate(),              0,
                    'ES-EC',                NULL,
        NULL,           0,                      'I',
        @login
    )
end

/*
 * ---------------------------------------------------------------------------------------------------------------------------------
 * Creacion del login Empresa
 */
if exists(select 1 from cob_bvirtual..bv_login where lo_login=@login_empresa and lo_ente=@ente_prueba_empresa)
begin
    --Se autoriza y/o actualiza estados de vigencia,reintentos y autorizacion el login de prueba
    update cob_bvirtual..bv_login
    set lo_estado='A',lo_autorizado='S', lo_intento_1 = 0,lo_intento_6 = 0,lo_clave_temp='EC8E1E5CBE7AA5B69D75B6B809BF465DF9F9CC7498D329358CECD38E5F8A876C',lo_clave_def='FC0CFD1CC67BFB45187868E2F474A09213669D15E2BE6083E504A969B69CC94B',lo_descripcion='TEST EMP REGRESION BV',lo_tipo_autorizacion='D'
    where lo_login=@login_empresa and lo_ente=@ente_prueba_empresa
end
else
begin
               
    --Se realiza la creacion del login de prueba
    SET IDENTITY_INSERT cob_bvirtual..bv_login ON
    insert into cob_bvirtual..bv_login (
        lo_ente,                    lo_servicio,        lo_login,
        lo_clave_temp,              lo_clave_def,       lo_fecha_reg,
        lo_fecha_mod,               lo_dias_vigencia,   lo_parametro,
        lo_tipo_vigencia,           lo_renovable,       lo_fecha_ult_pwd,
        lo_hora,                    lo_descripcion,     lo_tipo_autorizacion,
        lo_autorizado,              lo_cambiar_login,   lo_estado,
        lo_usar_mimenu,             lo_estilo,          lo_lenguaje,
        lo_intento_1,               lo_intento_2,       lo_intento_5,
        lo_intento_6,               lo_motivo_reimp,    lo_clave_gen,
        lo_clave_imp,               lo_carga_pagina,    lo_fecha_ult_int,
        lo_fecha_ult_ing,           lo_fecha_ult_ing2,  lo_empresa,
        lo_autoriz_imp,             lo_afiliador,       lo_oficina,
        lo_impresor,                lo_fecha_ult_imp,   lo_clave_imp_cobro,
        lo_idx,                     lo_cultura,         lo_tema,
        lo_numero_autorizacion,     lo_clave_mail,      lo_tip_envio,
        lo_login_personal )
    values (
        @ente_prueba_empresa,   @service_id,            @login_empresa,
        'EC8E1E5CBE7AA5B69D75B6B809BF465DF9F9CC7498D329358CECD38E5F8A876C', 'FC0CFD1CC67BFB45187868E2F474A09213669D15E2BE6083E504A969B69CC94B', getdate(),
        getdate(),      NULL,                   NULL,
        'I',            'S',                    getdate(),
        getdate(),      'TEST EMP REGRESION BV',    'D',
        'S',            'S',                    'A',
        NULL,           NULL,                   NULL,
        0,              0,                      0,
        0,              '0 ',                   3,
        3,              'I',                    NULL,
        getdate(),      NULL,                   NULL,
        'S',            'sa',                   1,
        'sa',           getdate(),              0,
        493,            'ES-EC',                NULL,
        NULL,           0,                      'I',
        @login_empresa
    )
end

/*
 * ---------------------------------------------------------------------------------------------------------------------------------
 * Creacion del login Empresa Tipo A
 */
if exists(select 1 from cob_bvirtual..bv_login where lo_login=@login_empresa_A and lo_ente=@ente_prueba_empresa_A)
begin
    --Se autoriza y/o actualiza estados de vigencia,reintentos y autorizacion el login de prueba
    update cob_bvirtual..bv_login
    set lo_estado='A', lo_tipo_autorizacion='A', lo_autorizado='S', lo_intento_1 = 0,lo_intento_6 = 0,lo_clave_temp='EC8E1E5CBE7AA5B69D75B6B809BF465DF9F9CC7498D329358CECD38E5F8A876C',lo_clave_def='FC0CFD1CC67BFB45187809E2F474A09213669D15E2BE6083E504A969B69CC94B',lo_descripcion='TEST EMP REGRESION BV',lo_ente= @ente_prueba_empresa_A
    where lo_login=@login_empresa_A and lo_ente=@ente_prueba_empresa_A
end
else
begin
               
    --Se realiza la creacion del login de prueba
    insert into cob_bvirtual..bv_login (
        lo_ente,                    lo_servicio,        lo_login,
        lo_clave_temp,              lo_clave_def,       lo_fecha_reg,
        lo_fecha_mod,               lo_dias_vigencia,   lo_parametro,
        lo_tipo_vigencia,           lo_renovable,       lo_fecha_ult_pwd,
        lo_hora,                    lo_descripcion,     lo_tipo_autorizacion,
        lo_autorizado,              lo_cambiar_login,   lo_estado,
        lo_usar_mimenu,             lo_estilo,          lo_lenguaje,
        lo_intento_1,               lo_intento_2,       lo_intento_5,
        lo_intento_6,               lo_motivo_reimp,    lo_clave_gen,
        lo_clave_imp,               lo_carga_pagina,    lo_fecha_ult_int,
        lo_fecha_ult_ing,           lo_fecha_ult_ing2,  lo_empresa,
        lo_autoriz_imp,             lo_afiliador,       lo_oficina,
        lo_impresor,                lo_fecha_ult_imp,   lo_clave_imp_cobro,
                            lo_cultura,         lo_tema,
        lo_numero_autorizacion,     lo_clave_mail,      lo_tip_envio,
        lo_login_personal )
    values (
        @ente_prueba_empresa_A,   @service_id,            @login_empresa_A,
        'EC8E1E5CBE7AA5B69D75B6B809BF465DF9F9CC7498D329358CECD38E5F8A876C', 'FC0CFD1CC67BFB45187809E2F474A09213669D15E2BE6083E504A969B69CC94B', getdate(),
        getdate(),      NULL,                   NULL,
        'I',            'S',                    getdate(),
        getdate(),      'TEST EMPRESA A REGRESION BV',    'A',
        'S',            'S',                    'A',
        NULL,           NULL,                   NULL,
        0,              0,                      0,
        0,              '0 ',                   3,
        3,              'I',                    NULL,
        getdate(),      NULL,                   NULL,
        'S',            'sa',                   1,
        'sa',           getdate(),              0,
                   'ES-EC',                NULL,
        NULL,           0,                      'I',
        @login_empresa_A
    )
end


/*
 * ---------------------------------------------------------------------------------------------------------------------------------
 * Creacion del login Grupo
 */
print 'Inicio Datos Grupo'
print @login_grupo 
print @ente_prueba_grupo
print 'Fin Datos Grupo'
if exists(select 1 from cob_bvirtual..bv_login where lo_login=@login_grupo and lo_ente=@ente_prueba_grupo)
begin
    --Se autoriza y/o actualiza estados de vigencia,reintentos y autorizacion el login de prueba
    update cob_bvirtual..bv_login
    set lo_estado='A',lo_autorizado='S', lo_intento_1 = 0,lo_intento_6 = 0,lo_clave_temp='FC0CFD1CC67BFB45187868E2F474A09213669D15E2BE6083E504A969B69CC94B',lo_clave_def='9D78034860AFD9982FC022DBAC382A51C0D406E48DB154C3177894BA89641173',lo_descripcion='TEST GRUPO REGRESION BV',lo_tipo_autorizacion='D'
    where lo_login=@login_grupo and lo_ente=@ente_prueba_grupo
end
else
begin
               
    --Se realiza la creacion del login de prueba
    insert into cob_bvirtual..bv_login (
        lo_ente,                    lo_servicio,        lo_login,
        lo_clave_temp,              lo_clave_def,       lo_fecha_reg,
        lo_fecha_mod,               lo_dias_vigencia,   lo_parametro,
        lo_tipo_vigencia,           lo_renovable,       lo_fecha_ult_pwd,
        lo_hora,                    lo_descripcion,     lo_tipo_autorizacion,
        lo_autorizado,              lo_cambiar_login,   lo_estado,
        lo_usar_mimenu,             lo_estilo,          lo_lenguaje,
        lo_intento_1,               lo_intento_2,       lo_intento_5,
        lo_intento_6,               lo_motivo_reimp,    lo_clave_gen,
        lo_clave_imp,               lo_carga_pagina,    lo_fecha_ult_int,
        lo_fecha_ult_ing,           lo_fecha_ult_ing2,  lo_empresa,
        lo_autoriz_imp,             lo_afiliador,       lo_oficina,
        lo_impresor,                lo_fecha_ult_imp,   lo_clave_imp_cobro,
                             lo_cultura,         lo_tema,
        lo_numero_autorizacion,     lo_clave_mail,      lo_tip_envio,
        lo_login_personal )
    values (
        @ente_prueba_grupo,   @service_id,            @login_grupo,
        'EC8E1E5CBE7AA5B69D75B6B809BF465DF9F9CC7498D329358CECD38E5F8A876C', '9D78034860AFD9982FC022DBAC382A51C0D406E48DB154C3177894BA89641173', getdate(),
        getdate(),      NULL,                   NULL,
        'I',            'S',                    getdate(),
        getdate(),      'TEST GRUPO REGRESION BV',    'D',
        'S',            'S',                    'A',
        NULL,           NULL,                   NULL,
        0,              0,                      0,
        0,              '0 ',                   3,
        3,              'I',                    NULL,
        getdate(),      NULL,                   NULL,
        'S',            'sa',                   1,
        'sa',           getdate(),              0,
                    'ES-EC',                NULL,
        NULL,           0,                      'I',
        @login_grupo
    )
end



/**
 * Creacion del perfil natural
 */
	delete from cob_bvirtual..bv_ente_servicio_perfil where es_ente=@ente_prueba
	select @perfil_id=pe_perfil from cob_bvirtual..bv_perfil where pe_nombre=@perfil_name
	select @perfil_id_mb=pe_perfil from cob_bvirtual..bv_perfil where pe_nombre=@perfil_name_mb
	
	insert into cob_bvirtual..bv_ente_servicio_perfil ( es_ente, es_servicio, es_perfil, es_estado, es_login, es_autorizado)
	values ( @ente_prueba, @service_id, @perfil_id, 'V', @login, 'S')
    insert into cob_bvirtual..bv_ente_servicio_perfil ( es_ente, es_servicio, es_perfil, es_estado, es_login, es_autorizado)
    values ( @ente_prueba, @service_id_mb, @perfil_id_mb, 'V', @login, 'S')


/**
 * Creacion del perfil empresa
 */

    select @perfil_id_empresa=pe_perfil from cob_bvirtual..bv_perfil where pe_nombre=@perfil_name_empresa

    if exists(select 1 from cob_bvirtual..bv_ente_servicio_perfil where es_login=@login_empresa and es_ente=@ente_prueba_empresa and es_perfil=@perfil_id_empresa)
    begin
        update cob_bvirtual..bv_ente_servicio_perfil set es_autorizado='S'
        where es_login=@login_empresa and es_ente=@ente_prueba_empresa and es_perfil=@perfil_id_empresa
    end
    else
    begin
        insert into cob_bvirtual..bv_ente_servicio_perfil ( es_ente, es_servicio, es_perfil, es_estado, es_login, es_autorizado)
        values ( @ente_prueba_empresa, @service_id, @perfil_id_empresa, 'V', @login_empresa, 'S')
    end
    
/**
 * Creacion del perfil empresa tipo A
 */

    select @perfil_id_empresa_A=pe_perfil from cob_bvirtual..bv_perfil where pe_nombre=@perfil_name_empresa_A

    if exists(select 1 from cob_bvirtual..bv_ente_servicio_perfil where es_login=@login_empresa_A and es_ente=@ente_prueba_empresa_A and es_perfil=@perfil_id_empresa_A)
    begin
        update cob_bvirtual..bv_ente_servicio_perfil set es_autorizado='S'
        where es_login=@login_empresa_A and es_ente=@ente_prueba_empresa_A and es_perfil=@perfil_id_empresa_A
    end
    else
    begin
        insert into cob_bvirtual..bv_ente_servicio_perfil ( es_ente, es_servicio, es_perfil, es_estado, es_login, es_autorizado)
        values ( @ente_prueba_empresa_A, @service_id, @perfil_id_empresa_A, 'V', @login_empresa_A, 'S')
    end
    
/**
 * Acuerdos de servicios
 */
	delete from cob_bvirtual..bv_ente_acuerdo_servicio where as_login in (@login,@login_empresa,@login_empresa_A)

	insert into cob_bvirtual..bv_ente_acuerdo_servicio(as_ente,as_login,as_servicio,as_acuerdo,as_fecha_reg,as_fecha_vig,as_estado) values
	(@ente_prueba,@login,@service_id,'IBSER1',getdate(),null,'V')
	
	insert into cob_bvirtual..bv_ente_acuerdo_servicio(as_ente,as_login,as_servicio,as_acuerdo,as_fecha_reg,as_fecha_vig,as_estado) values
	(@ente_prueba_empresa,@login_empresa,@service_id,'IBSER1',getdate(),null,'V')
	
	insert into cob_bvirtual..bv_ente_acuerdo_servicio(as_ente,as_login,as_servicio,as_acuerdo,as_fecha_reg,as_fecha_vig,as_estado) values
	(@ente_prueba_empresa,@login_empresa_A,@service_id,'IBSER1',getdate(),null,'V')

    /**
 * Creacion del perfil grupo
 */

    select @perfil_id_grupo=pe_perfil from cob_bvirtual..bv_perfil where pe_nombre=@perfil_name_grupo

    if exists(select 1 from cob_bvirtual..bv_ente_servicio_perfil where es_login=@login_grupo and es_ente=@ente_prueba_grupo and es_perfil=@perfil_id_grupo)
    begin
        update cob_bvirtual..bv_ente_servicio_perfil set es_autorizado='S'
        where es_login=@login_grupo and es_ente=@ente_prueba_grupo and es_perfil=@perfil_id_grupo
    end
    else
    begin
        insert into cob_bvirtual..bv_ente_servicio_perfil ( es_ente, es_servicio, es_perfil, es_estado, es_login, es_autorizado)
        values ( @ente_prueba_grupo, @service_id, @perfil_id_grupo, 'V', @login_grupo, 'S')
    end

    
/**
 * Preguntas y respuestas
 */
	delete from cob_bvirtual..bv_pregunta where pr_pregunta=248
	insert into cob_bvirtual..bv_pregunta(pr_pregunta,pr_descripcion,pr_categoria,pr_estado)
	values(248,'p','P','V')

	/* NATURAL */
	delete from cob_bvirtual..bv_login_pregunta where lp_login=@login
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba,1,@login,1)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba,1,@login,2)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba,1,@login,248)
	
	delete from cob_bvirtual..bv_login_respuesta where lr_login=@login
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login,@ente_prueba,1,1,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login,@ente_prueba,1,2,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login,@ente_prueba,1,248,'g',null,getdate(),getdate(),null,null,'V')

	/* EMPRESA */
	delete from cob_bvirtual..bv_login_pregunta where lp_login=@login_empresa
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_empresa,1,@login_empresa,1)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_empresa,1,@login_empresa,2)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_empresa,1,@login_empresa,248)
	
	delete from cob_bvirtual..bv_login_respuesta where lr_login=@login_empresa
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_empresa,@ente_prueba_empresa,1,1,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_empresa,@ente_prueba_empresa,1,2,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_empresa,@ente_prueba_empresa,1,248,'g',null,getdate(),getdate(),null,null,'V')
	
	/* EMPRESA A */
	delete from cob_bvirtual..bv_login_pregunta where lp_login=@login_empresa_A
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_empresa,1,@login_empresa_A,1)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_empresa,1,@login_empresa_A,2)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_empresa,1,@login_empresa_A,248)
	
	delete from cob_bvirtual..bv_login_respuesta where lr_login=@login_empresa_A
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_empresa_A,@ente_prueba_empresa,1,1,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_empresa_A,@ente_prueba_empresa,1,2,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_empresa_A,@ente_prueba_empresa,1,248,'g',null,getdate(),getdate(),null,null,'V')
	
	/* GRUPO */	
	delete from cob_bvirtual..bv_login_pregunta where lp_login=@login_grupo
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_grupo,1,@login_grupo,1)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_grupo,1,@login_grupo,2)
	insert into cob_bvirtual..bv_login_pregunta(lp_ente,lp_servicio,lp_login,lp_pregunta)
	values(@ente_prueba_grupo,1,@login_grupo,248)
	
	delete from cob_bvirtual..bv_login_respuesta where lr_login=@login_empresa_A
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_grupo,@ente_prueba_grupo,1,1,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_grupo,@ente_prueba_grupo,1,2,null,1,getdate(),getdate(),null,null,'V')
	insert into cob_bvirtual..bv_login_respuesta(lr_login,lr_ente,lr_servicio,lr_pregunta,lr_respuesta_personal,lr_respuesta,lr_fecha,lr_fecha_mod,lr_fecha_intento,lr_intento,lr_estado)
	values(@login_grupo,@ente_prueba_grupo,1,248,'g',null,getdate(),getdate(),null,null,'V')
	
	
	
 /**
 * Creacion de los productos asociados al login Natural
 */
	delete from cob_bvirtual..bv_ente_servicio_producto where ep_ente=@ente_prueba
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id, @acc_ctacte_type_product,@acc_ctacte_type_currency, @acc_ctacte_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id, @acc_ctacte_type_product,@acc_ctacte_type_currency, @acc_ctacte_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id, @acc_ctacte_type_product,@acc_ctacte_type_currency, '10410108275407019', 'CTA CTE TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, @acc_ctaaho_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, @acc_ctaaho_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, '10410108598405113', 'CTA AHO TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null,108275)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, '10410108275407904', 'CTA AHO TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null,108275)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, '10410000005222913', 'CTA AHO TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null,108253)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_car_type_product, @acc_car_type_currency, @acc_car_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_car_type_product, @acc_car_type_currency, @acc_car_number1, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_car_type_product, @acc_car_type_currency, @acc_car_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_dpf_type_product, @acc_dpf_type_currency, @acc_dpf_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id, @acc_ctacte_type_product_USD,@acc_ctacte_type_currency_USD, @acc_ctacte_number_USD, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_dpf_type_product3, @acc_dpf_type_currency3, @acc_dpf_number3, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_tar_type_product, @acc_tar_type_currency, @acc_tar_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_tar_type_product, @acc_tar_type_currency, @acc_tar_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id, @acc_tar_type_product_USD, @acc_tar_type_currency_USD, @acc_tar_number_USD, 'TARJETA DE CREDITO TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba, @service_id, @acc_ctacte_type_product, @acc_ctacte_type_currency, @acc_ctacte_number_chequera, 'CTA CTE PRUEBA CHEQUERA PER NAT', 'V', getdate(), getdate(), @login, 'S', 'S', null, 'S', null, @ente_mis_prueba)	
    
    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba, @service_id, @acc_comext_type_product, @acc_comext_type_currency, @acc_comext_trr, 'COMEXT PRUEBA CONSULTA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)	
    
    /*
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba, @service_id, @acc_ctaaho_porg_product, @acc_ctaaho_porg_product, @acc_ctaaho_prog_number, 'CTA AHO PROGRAMADO', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)*/	
	
/**
 * Creacion de los productos asociados al login Natural Mobile Banking
 */
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id_mb, @acc_ctacte_type_product,@acc_ctacte_type_currency, @acc_ctacte_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id_mb, @acc_ctacte_type_product,@acc_ctacte_type_currency, @acc_ctacte_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id_mb, @acc_ctacte_type_product,@acc_ctacte_type_currency, @acc_ctacte_number3, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, @acc_ctaaho_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, @acc_ctaaho_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, @acc_ctaaho_number3, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_car_type_product, @acc_car_type_currency, @acc_car_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_car_type_product, @acc_car_type_currency, @acc_car_number1, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_car_type_product, @acc_car_type_currency, @acc_car_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_tar_type_product, @acc_tar_type_currency, @acc_tar_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_tar_type_product, @acc_tar_type_currency, @acc_tar_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id_mb, @acc_ctacte_type_product_USD,@acc_ctacte_type_currency_USD, @acc_ctacte_number_USD, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba, @service_id_mb, @acc_dpf_type_product3,@acc_dpf_type_currency3, @acc_dpf_number3, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba, @service_id_mb, @acc_comext_type_product, @acc_comext_type_currency, @acc_comext_trr, 'COMEXT PRUEBA CONSULTA', 'V', getdate(), getdate(), @login, 'N', 'S', null, 'S', null, @ente_mis_prueba)
	
	

/**
 * Creacion de los productos asociados al login Empresa
 */
	delete from cob_bvirtual..bv_ente_servicio_producto where ep_ente=@ente_prueba_empresa and ep_login = @login_empresa
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id, @acc_ctacte_type_product_double_auth_empresa,@acc_ctacte_type_currency_double_auth_empresa, @acc_ctacte_number_double_auth_empresa, 'CTA TESTPRUEBA EMPRESA GCO', 'V', getdate(), getdate(), @login_empresa, 'S', 'S', null, 'S', null, 13036)
		
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id, @acc_ctacte_type_product_double_auth_empresa,@acc_ctacte_type_currency_double_auth_empresa, @acc_ctacte_number_double_auth_empresa2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id, @acc_ctaaho_type_product_double_auth_empresa,@acc_ctaaho_type_currency_double_auth_empresa, @acc_ctaaho_number_double_auth_empresa, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id, @acc_ctaaho_type_product_double_auth_empresa,@acc_ctaaho_type_currency_double_auth_empresa, '10410108275248120', 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)

	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba_empresa, @service_id, @acc_dpf_type_product4, @acc_dpf_type_currency4, @acc_dpf_number4, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)
	

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id, @acc_ctacte_type_product_USD,@acc_ctacte_type_currency_USD, @acc_ctacte_number_USD, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba_empresa, @service_id, @acc_ctacte_type_product, @acc_ctacte_type_currency, @acc_ctacte_number_chequera, 'CTA CTE PRUEBA CHEQUERA EMPRESA', 'V', getdate(), getdate(), @login_empresa, 'S', 'S', null, 'S', null, @ente_mis_prueba)
    
    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba_empresa, @service_id,@acc_comext_type_product, @acc_comext_type_currency, @acc_comext_trr, 'COMEXT PRUEBA CONSULTA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)
    
    
	/*
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba_empresa, @service_id, @acc_ctaaho_porg_product, @acc_ctaaho_prog_type_currency, @acc_ctaaho_prog_number, 'CTA AHO PROGRAMADO EMPRESA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)*/	
	
	
	/*
    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id,  @acc_tar_type_product_USD, @acc_tar_type_currency_USD, @acc_tar_number_USD, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)
	
	
*/
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba_empresa, @service_id, @acc_tar_type_product, @acc_tar_type_currency, @acc_tar_number, 'TARJETA DE CREDITO PRUEBA', 'V', getdate(), getdate(),  @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id,  @acc_ptm_type_product, @acc_ptm_type_currency, @acc_ptm_number, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id, @acc_tar_type_product_USD, @acc_tar_type_currency_USD, @acc_tar_number_USD,  'CTA CTE TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa, @service_id,  @acc2_ctacte_type_product_USD, @acc2_ctacte_type_currency_USD, @acc2_ctacte_number_USD, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa)

	
/**
 * Creacion de los productos asociados al login Empresa tipo A
 */
	delete from cob_bvirtual..bv_ente_servicio_producto where ep_ente=@ente_prueba_empresa_A and ep_login = @login_empresa_A
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa_A, @service_id, @acc_ctacte_type_product_double_auth_empresa,@acc_ctacte_type_currency_double_auth_empresa, @acc_ctacte_number_double_auth_empresa, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa_A, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa_A)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa_A, @service_id, @acc_ctacte_type_product_double_auth_empresa,@acc_ctacte_type_currency_double_auth_empresa, @acc_ctacte_number_double_auth_empresa2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa_A, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa_A)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa_A, @service_id, @acc_ctaaho_type_product_double_auth_empresa,@acc_ctaaho_type_currency_double_auth_empresa, @acc_ctaaho_number_double_auth_empresa, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa_A, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa_A)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa_A, @service_id, @acc_ctaaho_type_product_double_auth_empresa,@acc_ctaaho_type_currency_double_auth_empresa, '10410108275248120', 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa_A, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa_A)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa_A, @service_id, @acc_ctacte_type_product_USD,@acc_ctacte_type_currency_USD, @acc_ctacte_number_USD, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa_A, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa_A)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa_A, @service_id,  @acc_tar_type_product_USD, @acc_tar_type_currency_USD, @acc_tar_number_USD,  'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa_A, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa_A)

    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_empresa_A, @service_id,  @acc_ptm_type_product, @acc_ptm_type_currency, @acc_ptm_number,  'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_empresa_A, 'N', 'S', null, 'S', null, @ente_mis_prueba_empresa_A)


	/**
 * Creacion de los productos asociados al login Grupo
 */
	delete from cob_bvirtual..bv_ente_servicio_producto where ep_ente=@ente_prueba_grupo
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_grupo, @service_id, @acc_ctacte_type_product_double_auth_empresa,@acc_ctacte_type_currency_double_auth_empresa, @acc_ctacte_number_double_auth_empresa, 'CTA CTE TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_grupo, @service_id, @acc_ctacte_type_product_double_auth_empresa,@acc_ctacte_type_currency_double_auth_empresa, @acc_ctacte_number_double_auth_empresa2, 'CTA CTE TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_grupo, @service_id, @acc_ctaaho_type_product_double_auth_empresa,@acc_ctaaho_type_currency_double_auth_empresa, @acc_ctaaho_number_double_auth_empresa, 'CTA AHO TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)
	
	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_grupo, @service_id, @acc_ctaaho_type_product_double_auth_empresa,@acc_ctaaho_type_currency_double_auth_empresa, '10410108275248120', 'CTA AHO TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
	values( @ente_prueba_grupo, @service_id, @acc_dpf_type_product2, @acc_dpf_type_currency2, @acc_dpf_number2, 'CTA TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)	

    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_grupo, @service_id, @acc_ctacte_type_product_USD,@acc_ctacte_type_currency_USD, @acc_ctacte_number_USD, 'CTA CTE TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_grupo, @service_id, @acc_tar_type_product_USD, @acc_tar_type_currency_USD, @acc_tar_number_USD,  'CTA CTE TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto,ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro)
	values(@ente_prueba_grupo, @service_id, @acc_ptm_type_product, @acc_ptm_type_currency, @acc_ptm_number,  'CTA CTE TESTPRUEBA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)

	insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba_grupo, @service_id, @acc_ctacte_type_product, @acc_ctacte_type_currency, @acc_ctacte_number_chequera, 'CTA CTE PRUEBA CHEQUERA GRUPO', 'V', getdate(), getdate(), @login_grupo, 'S', 'S', null, 'S', null, @ente_mis_prueba)	
	
    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba_grupo, @service_id, @acc_ctacte_type_cheque, @acc_ctacte_type_currency, @acc_ctacte_number_cheque, 'CTA CTE PRUEBA CHEQUE', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', 41, @ente_mis_prueba_grupo)	

    insert into cob_bvirtual..bv_ente_servicio_producto ( ep_ente, ep_servicio, ep_producto, ep_moneda, ep_cuenta, ep_alias, ep_estado, ep_fecha_reg, ep_fecha_mod, ep_login, ep_cuenta_cobro, ep_notificacion, ep_ver, ep_autorizado, ep_grupo, ep_miembro )
    values( @ente_prueba_grupo, @service_id, @acc_comext_type_product, @acc_comext_type_currency, @acc_comext_trr, 'COMEXT PRUEBA CONSULTA', 'V', getdate(), getdate(), @login_grupo, 'N', 'S', null, 'S', null, @ente_mis_prueba_grupo)
      
	
/**
 * Medio de envio para login natural
 */
	delete cob_bvirtual..bv_medio_envio where me_ente = @ente_prueba
	EXEC cobis..sp_cseqnos
	@i_tabla = 'bv_medio_envio',
	@o_siguiente = @med_envio_siguiente OUT
	insert cob_bvirtual..bv_medio_envio values(@ente_prueba ,@login, @med_envio_siguiente , 'MAIL' ,'schancay@cobiscorp.com' , 'S' ,getdate(),getdate())
	

/**
 * Medio de envio para login juridico
 */
	delete cob_bvirtual..bv_medio_envio where me_ente = @ente_prueba_empresa
	EXEC cobis..sp_cseqnos
	@i_tabla = 'bv_medio_envio',
	@o_siguiente = @med_envio_siguiente OUT
	insert cob_bvirtual..bv_medio_envio values(@ente_prueba_empresa ,@login_empresa, @med_envio_siguiente , 'MAIL' ,'schancay@cobiscorp.com' , 'S' ,getdate(),getdate())

/**
 * Medio de envio para login juridico tipo A
 */
	delete cob_bvirtual..bv_medio_envio where me_ente = @ente_prueba_empresa_A
	EXEC cobis..sp_cseqnos
	@i_tabla = 'bv_medio_envio',
	@o_siguiente = @med_envio_siguiente OUT
	insert cob_bvirtual..bv_medio_envio values(@ente_prueba_empresa_A ,@login_empresa_A, @med_envio_siguiente , 'MAIL' ,'schancay@cobiscorp.com' , 'S' ,getdate(),getdate())
	
	EXEC cobis..sp_cseqnos
	@i_tabla = 'bv_medio_envio',
	@o_siguiente = @med_envio_siguiente OUT
	insert cob_bvirtual..bv_medio_envio values(@ente_prueba_empresa_A ,@login_empresa, @med_envio_siguiente , 'MAIL' ,'schancay@cobiscorp.com' , 'S' ,getdate(),getdate())

	/**
 * Medio de envio para login grupo
 */
	delete cob_bvirtual..bv_medio_envio where me_ente = @ente_prueba_grupo
	EXEC cobis..sp_cseqnos
	@i_tabla = 'bv_medio_envio',
	@o_siguiente = @med_envio_siguiente OUT
	insert cob_bvirtual..bv_medio_envio values(@ente_prueba_grupo ,@login_grupo, @med_envio_siguiente , 'MAIL' ,'schancay@cobiscorp.com' , 'S' ,getdate(),getdate())
/**
 * Tarjeta de Credito ente Natural
 */
	update cob_atm..tm_tarjeta set ta_nombre_tarjeta='TEST TARJETA',ta_cliente=13036,ta_propietario=13036,ta_nombre_cliente=@ente_prueba_name,ta_ced_ruc=@login_identificaion where ta_codigo='5041960000000013' and ta_tarjeta=1
	
	
	/**
	 * Empresas para login tipo grupo
	 */
	delete cob_bvirtual..bv_login_empresa
	where le_login = @login_grupo
	
	insert into  cob_bvirtual..bv_login_empresa
	(
	le_login                                                         
	,le_servicio 
	,le_grupo    
	,le_empresa  
	,le_default 
	,le_estado 
	,le_fecha_reg                
	,le_fecha_mod
	)
	values( 
	@login_grupo
	,1
	,41
	,25
	,'S'
	,'V'
	,getdate()
	,getdate())
	                
	
	insert into  cob_bvirtual..bv_login_empresa
	(
	le_login                                                         
	,le_servicio 
	,le_grupo    
	,le_empresa  
	,le_default 
	,le_estado 
	,le_fecha_reg                
	,le_fecha_mod
	)
	values( 
	@login_grupo
	,1
	,41
	,41
	,'N'
	,'V'
	,getdate()
	,getdate())
	
	
	insert into  cob_bvirtual..bv_login_empresa
	(
	le_login                                                         
	,le_servicio 
	,le_grupo    
	,le_empresa  
	,le_default 
	,le_estado 
	,le_fecha_reg                
	,le_fecha_mod
	)
	values( 
	@login_grupo
	,1
	,41
	,108258
	,'N'
	,'V'
	,getdate()
	,getdate())
	
	
go