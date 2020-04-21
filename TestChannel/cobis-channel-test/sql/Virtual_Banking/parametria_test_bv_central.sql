use cobis
go
declare @service_id int,
        --ENTE NATURAL
        @ente_prueba int,
        @ente_mis_prueba int,
        @login varchar(50),
        @login_identificaion varchar(50),
        @perfil_name varchar(50),
        @perfil_id int,
        --ENTE EMPRESA
        @ente_prueba_empresa int,
        @ente_mis_prueba_empresa int,
        @login_empresa varchar(50),
        @login_identificaion_empresa varchar(50),
        @perfil_name_empresa varchar(50),
        @perfil_id_empresa int,
        --CUENTA CORRIENTE PARA TRANSACCIONAR
        @acc_ctacte_number varchar(20),
        @acc_ctacte_type_product int,
        @acc_ctacte_type_currency int,
        --CUENTA CORRIENTE PARA TRANSACCIONAR CON DOBLE AUTORIZACION EMPRESA
        @acc_ctacte_number_double_auth_empresa varchar(20),
        @acc_ctacte_type_product_double_auth_empresa int,
        @acc_ctacte_type_currency_double_auth_empresa int,
        --CUENTA AHORRO PARA TRANSACCIONAR CON DOBLE AUTORIZACION EMPRESA
        @acc_ctaaho_number_double_auth_empresa varchar(20),
        @acc_ctaaho_type_product_double_auth_empresa int,
        @acc_ctaaho_type_currency_double_auth_empresa int,
        --CUENTA AHORRO PARA TRANSACCIONAR
        @acc_ctaaho_number varchar(20),
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
        --CUENTA CARTERA PARA TRANSACCIONAR
        @acc_car_number varchar(20),
        @acc_car_type_product int,
        @acc_car_type_currency int,
        @med_envio_siguiente int,
        --DESTINO PARA PRUEBAS DE TRANSFERENCIAS A TERCERO
        @des_acc_ctacte_number varchar(20),
        @des_acc_ctacte_type_product int,
        @des_acc_ctacte_type_currency int,
        --CUENTA CORRIENTE PARA TRANSACCIONAR USD
        @acc2_ctacte_number_USD varchar(20),
        @acc2_ctacte_type_product_USD int,
        @acc2_ctacte_type_currency_USD int,
        
        @acc_ctacte_number_USD varchar(20),
        @acc_ctacte_type_product_USD int,
        @acc_ctacte_type_currency_USD int

/**
 * Inicializacion de los parametros Base
 */

select  @service_id=1,
        --ENTE NATURAL
        @ente_prueba=277,
        @ente_mis_prueba =13036,
        @login ='testCts',
        @login_identificaion='1234567890',
        @perfil_name='PERFIL PERSONAL',
        --ENTE EMPRESA
        @ente_prueba_empresa=279,
        @ente_mis_prueba_empresa=137488,
        @login_empresa ='testCtsEmp',
        @login_identificaion_empresa='1234567890',
        @perfil_name_empresa='PERFIL EMPRESARIAL',
        --CTACTE
        @acc_ctacte_number='10410108275406111',
        @acc_ctacte_type_product=3,
        @acc_ctacte_type_currency=0,
        --CTACTE DOBLE AUTORIZACION EMPRESA
        @acc_ctacte_number_double_auth_empresa='10410108275407019',
        @acc_ctacte_type_product_double_auth_empresa=3,
        @acc_ctacte_type_currency_double_auth_empresa=0,
        --CTAAHO DOBLE AUTORIZACION EMPRESA
        @acc_ctaaho_number_double_auth_empresa='10410000005233616',
        @acc_ctaaho_type_product_double_auth_empresa=4,
        @acc_ctaaho_type_currency_double_auth_empresa=0,
        --CTAAHO
        @acc_ctaaho_number='10410108275249013',
        @acc_ctaaho_type_product=4,
        @acc_ctaaho_type_currency=0,
        --DPF
        @acc_dpf_number='01414052458',
        @acc_dpf_type_product=14,
        @acc_dpf_type_currency=0,
        --DPF EMPRESA
        @acc_dpf_number2='28414118610',
        @acc_dpf_type_product2=14,
        @acc_dpf_type_currency2=0,
        --CAR
        @acc_car_number='10407740700943818',
        @acc_car_type_product=7,
        @acc_car_type_currency=0,
        --DESTINO PARA PRUEBAS DE TRANSFERENCIAS A TERCERO
        @des_acc_ctacte_number='10410108595405215',
        @des_acc_ctacte_type_product=3,
        @des_acc_ctacte_type_currency=0,
        --CTACTE
        
        @acc2_ctacte_number_USD='10410108275402927',
        @acc2_ctacte_type_product_USD=3,
        @acc2_ctacte_type_currency_USD=17,
        
        @acc_ctacte_number_USD='10410108640405011',
        @acc_ctacte_type_product_USD=3,
        @acc_ctacte_type_currency_USD=17
        
declare @FUNCIONARIO_ID int,
        @ENTE_COBIS_TEST int,
        @ENTE_BV_TEST int,
        @PROD_DPF varchar(20),
        @PROD_DPF_OPE int,
        @PROD_DPF_TRN int,
        @PROD_CAR varchar(20),
        @PROD_CAR_OPE int,
        @PROD_CAR2 varchar(20),
        @PROD_CAR_OPE2 int,
        @PROD_CEX varchar(20),
        @PROD_CEX_OPE int,
        @PROD_TAR varchar(20),
        @ID_MAX int,
        @LOGIN varchar(15),
        @LOGIN_NUM_DOC varchar(15)

--Ente de Pruebas no Cambiar
select  @ENTE_COBIS_TEST=13036,
        @ENTE_BV_TEST=277,
        @PROD_DPF='01414052458',
        @PROD_DPF_OPE=34416,
        @PROD_DPF_TRN=14901,
        @PROD_CAR='10407740700943818',
        @PROD_CAR_OPE=108120,
        @PROD_CAR2='10410108232700018',
        @PROD_CAR_OPE2=642,
        @PROD_CEX='TRR00108000011',
        @PROD_CEX_OPE=1000,
        @PROD_TAR='1234567890123456',
        @LOGIN ='testCts',
        @LOGIN_NUM_DOC='1234567890'

/* CREACION DEL FUNCIONARIO IB */
select @FUNCIONARIO_ID=894
update cobis..cl_parametro set pa_smallint=@FUNCIONARIO_ID where pa_nemonico = 'COD' and pa_producto='BVI'

if not exists(select 1 from cobis..cl_funcionario where fu_funcionario=@FUNCIONARIO_ID)
begin
    insert into cobis..cl_funcionario(
        fu_funcionario, fu_nombre,      fu_sexo,
        fu_dinero,      fu_nomina,      fu_departamento,
        fu_oficina,     fu_cargo,       fu_secuencial,
        fu_jefe,        fu_nivel,       fu_fecha_ing,
        fu_login,       fu_telefono,    fu_fec_inicio,
        fu_fec_final,   fu_clave ,      fu_estado,
        fu_offset)
        values(
        @FUNCIONARIO_ID,        'funcional_test_ib', 'M',
        'N',                    null,               29,
        1,                      34,                 3,
        0,                      null,               getdate(),
        'funcional_test_ib',    null,               getdate(),
        getdate(),              '|4cF{\wo',         'V',
        '')
end

if not exists(select 1 from cobis..cc_oficial where oc_funcionario=@FUNCIONARIO_ID)
begin
    select @ID_MAX=max(oc_oficial)+1 from cobis..cc_oficial
    insert into cobis..cc_oficial(
    oc_oficial,    oc_funcionario,     oc_sector,
    oc_actividad,  oc_ofi_nsuperior,   oc_ofi_sustituto,
    oc_tipo_oficial)values(
    @ID_MAX,   @FUNCIONARIO_ID,    'P',
    null,      602,                1000,
    '4')
end 

if not exists(select 1 from cobis..cl_medios_funcio where mf_funcionario=@FUNCIONARIO_ID)
begin
    insert into cobis..cl_medios_funcio(mf_funcionario, mf_codigo, mf_tipo, mf_descripcion, mf_estado)
    values(@FUNCIONARIO_ID,1,'1','funcional_test_ib@cobiscorp.com','V')
end
/* ----------------------------------------------------------------------------------------------------- */

/* Registro de un DPF */
if not exists(select 1 from cobis..cl_ttransaccion where tn_trn_code=@PROD_DPF_TRN)
begin
    insert into cobis..cl_ttransaccion(tn_trn_code,tn_descripcion,tn_nemonico,tn_desc_larga)
    values(@PROD_DPF_TRN,'APERTURA DPF','FIOP','PROCESO QUE INSERTA LA OPERACION')
end

if not exists(select 1 from cob_pfijo..pf_operacion where op_num_banco=@PROD_DPF)
begin
    INSERT INTO cob_pfijo..pf_operacion ( op_num_banco, op_operacion, op_ente, op_toperacion, op_categoria, op_estado, op_producto, op_oficina, op_moneda, op_num_dias, op_base_calculo, op_monto, op_monto_pg_int, op_monto_pgdo, op_monto_blq, op_tasa, op_tasa_efectiva, op_int_ganado, op_int_estimado, op_residuo, op_int_pagados, op_int_provision, op_total_int_ganados, op_total_int_pagados, op_total_int_estimado, op_total_int_retenido, op_total_retencion, op_fpago, op_ppago, op_dia_pago, op_casilla, op_direccion, op_telefono, op_historia, op_duplicados, op_renovaciones, op_incremento, op_mon_sgte, op_pignorado, op_renova_todo, op_imprime, op_retenido, op_retienimp, op_totalizado, op_tcapitalizacion, op_oficial, op_accion_sgte, op_preimpreso, op_tipo_plazo, op_tipo_monto, op_causa_mod, op_descripcion, op_fecha_valor, op_fecha_ven, op_fecha_cancela, op_fecha_ingreso, op_fecha_pg_int, op_fecha_ult_pg_int, op_ult_fecha_calculo, op_fecha_crea, op_fecha_mod, op_fecha_total, op_puntos, op_total_int_acumulado, op_tasa_mer, op_ced_ruc, op_plazo_ant, op_fecven_ant, op_tot_int_est_ant, op_fecha_ord_act, op_mantiene_stock, op_stock, op_emision_inicial, op_moneda_pg, op_impuesto, op_num_imp_orig, op_impuesto_capital, op_retiene_imp_capital, op_ley, op_reestruc, op_fecha_real, op_ult_fecha_cal_tasa, op_num_dias_gracia, op_prorroga_aut, op_tasa_variable, op_mnemonico_tasa, op_modalidad_tasa, op_periodo_tasa, op_descr_tasa, op_operador, op_spread, op_estatus_prorroga, op_num_prorroga, op_anio_comercial, op_flag_tasaefec, op_comision, op_porc_comision, op_cupon, op_categoria_cupon, op_custodia, op_nueva_tasa, op_incremento_prorroga, op_puntos_prorroga, op_scontable, op_captador, op_aprobado, op_bloqueo_legal, op_monto_blqlegal, op_ult_fecha_calven, op_prov_pendiente, op_residuo_prov, op_int_total_prov_vencida, op_int_prov_vencida, op_tipo_tasa_var, op_oficial_principal, op_oficial_secundario, op_origen_fondos, op_proposito_cuenta, op_producto_bancario1, op_producto_bancario2, op_revision_tasa, op_dias_reales, op_plazo_orig, op_sec_incre, op_renovada, op_int_ajuste, op_tasa_ant, op_cambio_tasa, op_plazo_cont, op_incre, op_tasa_min, op_tasa_max, op_camb_oper, op_fecha_ult_renov, op_fecha_ult_pago_int_ant, op_ente_corresp, op_contador_firma, op_condiciones, op_localizado, op_fecha_localizacion, op_fecha_no_localiza, op_inactivo, op_dias_hold, op_sucursal, op_incremento_suspenso, op_oficina_apertura, op_oficial_apertura, op_toperacion_apertura, op_tipo_plazo_apertura, op_tipo_monto_apertura, op_amortiza_periodo, op_total_amortizado, op_fideicomiso, op_pago_interes, op_fecha_rev_tasa ) 
    VALUES ( @PROD_DPF, @PROD_DPF_OPE, @ENTE_COBIS_TEST, 'PERF1', 'LOC', 'ACT_X', 14, 1, 0, 361, 360, 402029.48, 402029.48, 402029.48, 0.00, 8.152, 0, 0.00, 2731.12, 0, 0.00, 91.0403, 8193.36, 8193.36, 32864.49, 655.47, 0.00, 'PER', 'M', 0, NULL, NULL, NULL, 4, 99, 13, 0.00, 3, 'S', 'N', 'S', 'N', 'S', 'N', 'N', 'emoran1', 'XREN', 0, '13', '2', 'MIG', 'EUGENIO ARAGON CARAZO', '2/2/2013 12:00:00.000 AM', '2/3/2014 12:00:00.000 AM', NULL, '2/2/2013 12:00:00.000 AM', '6/2/2013 12:00:00.000 AM', '5/2/2013 12:00:00.000 AM', '5/1/2013 12:00:00.000 AM', '2/2/2004 12:00:00.000 AM', '5/2/2013 12:00:00.000 AM', '6/2/2013 12:00:00.000 AM', 0, 8102.32, 0, '01-0370-0251', NULL, NULL, NULL, '2/2/2013 12:00:00.000 AM', 'N', NULL, NULL, '0', 8, 1, 0.08, 'N', 'N', NULL, '2/2/2013 12:00:00.000 AM', '2/2/2013 12:00:00.000 AM', 0, 'S', 'N', NULL, NULL, NULL, NULL, NULL, 0.25, 'N', 0, 'N', 'N', 0.00, 0, 'N', NULL, 'S', 8.152, 0.00, 0, NULL, '04ofsj01', 'S', 'N', 0.00, NULL, 0, 0, 0, 0, 'N', 'MIGRACION2', '04es6577', '190', '18', NULL, NULL, 'N', 'N', 360, 0, 'S', 0, 8.152, 'N', NULL, 'N', NULL, NULL, NULL, '2/2/2013 12:00:00.000 AM', '4/2/2013 12:00:00.000 AM', NULL, NULL, NULL, 'S', NULL, NULL, NULL, 0, 1, NULL, 1, '04ofsj01', 'PERF1', '13', '2', NULL, NULL, NULL, NULL, NULL )
end
else
begin
    update cob_pfijo..pf_operacion set
    op_ente=@ENTE_COBIS_TEST
    where op_num_banco=@PROD_DPF
end

--Movimientos de Consulta del DPF
if not exists(select 1 from cob_pfijo..pf_mov_monet where mm_operacion=@PROD_DPF_OPE)
begin
	insert into cob_pfijo..pf_mov_monet(
		mm_operacion,              mm_tran,            mm_secuencia,
		mm_secuencial,             mm_sub_secuencia,   mm_fecha_aplicacion,
		mm_producto,               mm_cuenta,          mm_valor,
		mm_estado,                 mm_tipo,            mm_beneficiario,
		mm_impuesto,               mm_moneda,          mm_valor_ext,
		mm_fecha_crea,             mm_fecha_mod,       mm_oficina,
		mm_impuesto_capital_me,    mm_fecha_real,      mm_secuencia_emis_che,
		mm_user,                   mm_tipo_cliente,    mm_autorizado,
		mm_cotizacion,             mm_tipo_cotiza,     mm_ttransito,
		mm_fecha_valor,            mm_renovado,        mm_cta_corresp,
		mm_cod_corresp,            mm_benef_corresp,   mm_ofic_corresp,
		mm_incremento,             mm_num_cheque,      mm_sec_mov,
		mm_usuario,                mm_tipo_cuenta_ach, mm_banco_ach,
		mm_penaliza,               mm_ssn_branch,      mm_oficina_pago,
		mm_cod_banco_ach,          mm_terminal
	)values(
		@PROD_DPF_OPE,@PROD_DPF_TRN,1,
		0,1,null,
		'CHG',null,1045545.72,
		'A','C',@ENTE_COBIS_TEST,
		3422.39,0,0,
		'10/25/2013 12:00:00 AM','10/25/2013 12:00:00 AM',1,
		null,null,null,
		'testib','M',null,
		0,'N',null,
		'10/25/2013 12:00:00 AM',null,null,
		null,'NOMBRE COMPLETO 92388',null,
		null,null,1,
		'testib',null,null,
		null,null,1,
		null,'PC01SOLBAN266'
	)
end
else
begin
	update cob_pfijo..pf_mov_monet
	set mm_tran=@PROD_DPF_TRN,
	mm_terminal='PC01SOLBAN266',
	mm_usuario='testib',
	mm_user='testib',
	mm_estado='A',
	mm_beneficiario=@ENTE_COBIS_TEST
	where mm_operacion=@PROD_DPF_OPE
end 

if not exists(select 1 from cob_pfijo..pf_cliente_externo where ce_secuencial=@ENTE_COBIS_TEST)
begin
	insert into cob_pfijo..pf_cliente_externo (ce_secuencial,ce_nombre,ce_cedula,ce_direccion)
	values(@ENTE_COBIS_TEST,'TEST IB','12345678912','XXXXXXX')
end 
else
begin
	update cob_pfijo..pf_cliente_externo
	set ce_nombre='TEST IB'
end 
/* ----------------------------------------------------------------------------------------------------- */

/* Registro de Cartera */
delete from cob_cartera..ca_operacion where op_operacion=@PROD_CAR_OPE
insert into cob_cartera..ca_operacion ( op_operacion, op_banco, op_anterior, op_migrada, op_tramite, op_cliente, op_nombre, op_sector, op_toperacion, op_oficina, op_moneda, op_comentario, op_oficial, op_fecha_ini, op_fecha_fin, op_fecha_ult_proceso, op_fecha_liq, op_fecha_reajuste, op_monto, op_monto_aprobado, op_destino, op_lin_credito, op_ciudad, op_estado, op_periodo_reajuste, op_reajuste_especial, op_tipo, op_forma_pago, op_cuenta, op_dias_anio, op_tipo_amortizacion, op_cuota_completa, op_tipo_cobro, op_tipo_reduccion, op_aceptar_anticipos, op_precancelacion, op_tipo_aplicacion, op_tplazo, op_plazo, op_tdividendo, op_periodo_cap, op_periodo_int, op_dist_gracia, op_gracia_cap, op_gracia_int, op_dia_fijo, op_cuota, op_evitar_feriados, op_num_renovacion, op_renovacion, op_mes_gracia, op_reajustable, op_origen_fondo, op_fondos_propios, op_dias_desembolso, op_fecha_ins_desembolso, op_nro_bmi, op_cupos_terceros, op_sector_contable, op_clabas, op_clabope, op_plazo_contable, op_fecha_ven_legal, op_cuota_ballom, op_cuota_menor, op_tcertificado, op_estado_manual, op_prd_cobis, op_sujeta_nego, op_ref_exterior, op_via_judicial, op_reest_int, op_garant_emi, op_oficial_cont, op_premios, op_saldo, op_base_calculo, op_ajuste_moneda, op_moneda_ajuste, op_product_group, op_lin_comext, op_tipo_prioridad, op_promotor, op_vendedor, op_comision_pro, op_cuota_incluye, op_dias_prorroga, op_subsidio, op_porcentaje_subsidio, op_financia, op_abono_ini, op_opcion_compra, op_beneficiario, op_factura, op_tpreferencial, op_porcentaje_preferencial, op_monto_preferencial, op_comision_ven, op_cuenta_vendedor, op_agencia_venta, op_comision_agencia, op_cuenta_agencia, op_canal_venta, op_iniciador, op_entrevistador, op_fecha_ult_mod, op_usuario_mod, op_referido, op_num_reest, op_num_prorroga, op_fecha_cambio_est, op_num_reajuste, op_gracia_mora, op_modo_reest, op_estado_deuda, op_dividendo_vig, op_fecha_ult_reest, op_fecha_ult_pago, op_fecha_ult_pago_cap, op_fecha_ult_pago_int, op_monto_ult_pago, op_monto_ult_pago_cap, op_monto_ult_pago_int, op_sec_ult_pago, op_efecto_pago, op_fecha_ult_reaj, op_tasa_anterior, op_monto_promocion, op_saldo_promocion, op_tipo_promocion, op_cuota_promocion, op_compra_operacion ) 
values ( @PROD_CAR_OPE, @PROD_CAR, '77407008888', '77407009438', 13677, @ENTE_COBIS_TEST, 'CLIENTE (@ENTE_COBIS_TEST)', 'BHVI', 'HVI', 77, 0, 'HIPOTECA ULTIMO DESEMBOLSO/ SE COBRAN GASTOS LEGALES//SEGURO VIDA E INCENDIO// SE CANCELA OP HSBC', 1030, '10/5/2007 12:00:00.000 AM', '10/5/2037 12:00:00.000 AM', '4/16/2013 12:00:00.000 AM', '10/8/2007 12:00:00.000 AM', NULL, 17000000.00, 17000000.00, '310', '50471085218', 206, 2, 0, 'N', 'D', 'C', @PROD_CAR, 360, 'FRANCESA', 'N', 'P', 'C', 'S', 'S', 'D', 'M', 361, 'M', 1, 1, 'S', 0, 0, 5, 158944.75, 'N', 2, 'S', 0, 'N', '190', 'S', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'N', NULL, 'N', 7, NULL, NULL, 'S', NULL, 'S', 1030, NULL, 13661361.87, 'E', NULL, NULL, NULL, NULL, 'N', NULL, NULL, NULL, 'T', 0, 'N', 0, NULL, 0.00, 0.00, NULL, NULL, 'N', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1/30/2013 12:00:00.000 AM', ' ', NULL, NULL, NULL, '2/5/2013 12:00:00.000 AM', 2, 'N', 'P', '11', 68, NULL, NULL, '1/5/2013 12:00:00.000 AM', '1/5/2013 12:00:00.000 AM', NULL, NULL, NULL, NULL, 'M', NULL, NULL, NULL, NULL, NULL, NULL, 'N' )

delete from cob_cartera..ca_abono where ab_operacion=@PROD_CAR_OPE
insert into cob_cartera..ca_abono ( ab_operacion, ab_secuencial_ing, ab_secuencial_rpa, ab_secuencial_pag, ab_fecha_ing, ab_fecha_pag, ab_cuota_completa, ab_aceptar_anticipos, ab_tipo_reduccion, ab_tipo_cobro, ab_dias_retencion_ini, ab_dias_retencion, ab_estado, ab_usuario, ab_oficina, ab_terminal, ab_tipo, ab_tipo_aplicacion, ab_nro_recibo, ab_saldo, ab_dividendo, ab_cod_orides, ab_tipo_prioridad, ab_rubro_pago, ab_negociado, ab_dias_validez, ab_pago_anticipado, ab_fecha_reversa, ab_usuario_reversa, ab_estado_aprob, ab_usuario_aprob, ab_fecha_aprob, ab_usuario_mod, ab_fecha_mod, ab_canal, ab_efecto_pago, ab_monto_no_neg ) 
values ( @PROD_CAR_OPE, 3005, 0, 3005, '2/4/2013 12:00:00.000 AM', '4/16/2013 12:00:00.000 AM', 'S', 'S', 'C', 'P', 0, 0, 'A', 'userbatch', 77, 'BATCH_CARTERA', 'PAG', 'D', 0, NULL, 1234, NULL, 'N', NULL, 'N', NULL, NULL, NULL, NULL, 'N', NULL, NULL, NULL, NULL, 99, 'M', NULL )


delete from cob_cartera..ca_abono_det where abd_operacion=@PROD_CAR_OPE
insert into cob_cartera..ca_abono_det ( abd_operacion, abd_secuencial_ing, abd_tipo, abd_concepto, abd_cuenta, abd_beneficiario, abd_moneda, abd_monto_mpg, abd_monto_mop, abd_monto_mn, abd_cotizacion_mpg, abd_cotizacion_mop, abd_tcotizacion_mpg, abd_tcotizacion_mop ) 
        values ( @PROD_CAR_OPE, 3005, 'PAG', 'NDASCA-00', @PROD_CAR, 'Debito Automatico', 0, 0, 0, 0.00, 0, 0, 'N', 'N' )
        
delete from cob_cartera..ca_transaccion where tr_operacion = @PROD_CAR_OPE
insert into cob_cartera..ca_transaccion ( tr_operacion, tr_secuencial, tr_fecha_mov, tr_toperacion, tr_moneda, tr_tran, tr_en_linea, tr_banco, tr_dias_calc, tr_ofi_oper, tr_ofi_usu, tr_usuario, tr_terminal, tr_fecha_ref, tr_secuencial_ref, tr_estado, tr_sector, tr_oficial, tr_causa_rev, tr_fecha_reversa, tr_comprobante, tr_fecha_cont ) 
        values ( @PROD_CAR_OPE, 2, '1/31/2013 12:00:00.000 AM', 'HVI', 0, 'PAG', 'N', @PROD_CAR, 0, 77, 77, 'userbatch', 'consola', '2/1/2013 12:00:00.000 AM', 2, 'ING', 'BHVI', 1030, NULL, NULL, NULL, NULL )

insert into cob_cartera..ca_transaccion ( tr_operacion, tr_secuencial, tr_fecha_mov, tr_toperacion, tr_moneda, tr_tran, tr_en_linea, tr_banco, tr_dias_calc, tr_ofi_oper, tr_ofi_usu, tr_usuario, tr_terminal, tr_fecha_ref, tr_secuencial_ref, tr_estado, tr_sector, tr_oficial, tr_causa_rev, tr_fecha_reversa, tr_comprobante, tr_fecha_cont ) 
        values ( @PROD_CAR_OPE, 3002, '2/1/2013 12:00:00.000 AM', 'HVI', 0, 'FVA', 'N', @PROD_CAR, 0, 77, 77, 'userbatch', 'consola', '2/2/2013 12:00:00.000 AM', 3002, 'ING', 'BHVI', 1030, NULL, NULL, NULL, NULL )

insert into cob_cartera..ca_transaccion ( tr_operacion, tr_secuencial, tr_fecha_mov, tr_toperacion, tr_moneda, tr_tran, tr_en_linea, tr_banco, tr_dias_calc, tr_ofi_oper, tr_ofi_usu, tr_usuario, tr_terminal, tr_fecha_ref, tr_secuencial_ref, tr_estado, tr_sector, tr_oficial, tr_causa_rev, tr_fecha_reversa, tr_comprobante, tr_fecha_cont ) 
        values ( @PROD_CAR_OPE, 3117, '3/29/2013 12:00:00.000 AM', 'HVI', 0, 'FVA', 'N', @PROD_CAR, 0, 77, 77, 'userbatch', 'consola', '4/1/2013 12:00:00.000 AM', 3117, 'ING', 'BHVI', 1030, NULL, NULL, NULL, NULL )

insert into cob_cartera..ca_transaccion ( tr_operacion, tr_secuencial, tr_fecha_mov, tr_toperacion, tr_moneda, tr_tran, tr_en_linea, tr_banco, tr_dias_calc, tr_ofi_oper, tr_ofi_usu, tr_usuario, tr_terminal, tr_fecha_ref, tr_secuencial_ref, tr_estado, tr_sector, tr_oficial, tr_causa_rev, tr_fecha_reversa, tr_comprobante, tr_fecha_cont ) 
        values ( @PROD_CAR_OPE, 3008, '2/4/2013 12:00:00.000 AM', 'HVI', 0, 'EST', 'N', @PROD_CAR, 0, 77, 77, 'userbatch', 'consola', '2/5/2013 12:00:00.000 AM', 0, 'ING', 'BHVI', 1030, NULL, NULL, NULL, NULL )

insert into cob_cartera..ca_transaccion ( tr_operacion, tr_secuencial, tr_fecha_mov, tr_toperacion, tr_moneda, tr_tran, tr_en_linea, tr_banco, tr_dias_calc, tr_ofi_oper, tr_ofi_usu, tr_usuario, tr_terminal, tr_fecha_ref, tr_secuencial_ref, tr_estado, tr_sector, tr_oficial, tr_causa_rev, tr_fecha_reversa, tr_comprobante, tr_fecha_cont ) 
        values ( @PROD_CAR_OPE, 3005, '2/28/2013 12:00:00.000 AM', 'HVI', 0, 'PAG', 'N', @PROD_CAR, 0, 77, 77, 'userbatch', 'consola', '3/1/2013 12:00:00.000 AM', 3055, 'ING', 'BHVI', 1030, NULL, NULL, NULL, NULL )

delete from cob_custodia..cu_doctos where do_documento=1234
insert into cob_custodia..cu_doctos ( do_filial, do_documento, do_emisor, do_num_doc, do_sucursal, do_num_negocio, do_aceptante, do_tasa, do_precio_compra, do_fecha_proc, do_tipo_doc, do_moneda, do_valor_bruto, do_anticipos, do_por_impuestos, do_por_retencion, do_valor_neto, do_valor_neg, do_ciudad, do_fecha_emision, do_fecha_vtodoc, do_fecha_inineg, do_fecha_vtoneg, do_fecha_pago, do_base_calculo, do_dias_negocio, do_num_dex, do_fecha_dex, do_cliente, do_resp_pago, do_resp_dscto, do_observaciones, do_estado, do_agrupado, do_grupo, do_recaudado ) VALUES ( 1, 1234, 1, '1234', 1, '1234', 1, 1, 2, '01/01/2013 12:00:00 AM', '4', 0, 120, 20, 12, 12, 120, 0, 1, '12/12/2013 12:00:00 AM', '12/12/2013 12:00:00 AM', '12/12/2013 12:00:00 AM', '12/12/2013 12:00:00 AM', '12/12/2013 12:00:00 AM', '0', 0, '1', '12/12/2013 12:00:00 AM', @ENTE_COBIS_TEST, 0, 0, '0', 'A', 'N', 1, 0 )
delete from cob_credito..cr_facturas where fa_documento=1234
insert into cob_credito..cr_facturas ( fa_tramite, fa_documento, fa_num_negocio, fa_grupo, fa_valor, fa_moneda, fa_fecini_neg, fa_fecfin_neg, fa_usada, fa_dividendo, fa_referencia, fa_porcentaje ) VALUES ( 13677, 1234, '15', 1, 123, 0, '01/01/2013 12:00:00 AM', '01/12/2013 12:00:00 AM', '0', 1234, '123456789', 0 )

delete from cob_cartera..ca_pago_automatico where pa_operacion=@PROD_CAR_OPE
insert into cob_cartera..ca_pago_automatico(pa_operacion,pa_forma_pago,pa_cuenta,pa_monto,pa_rubro,pa_institucion,pa_cliente,pa_rol,pa_comentario) values(@PROD_CAR_OPE,'ACH',NULL,50000000.00,NULL,'903',NULL,NULL,'TEST IB')

/* ============================================== */
delete from cob_cartera..ca_operacion where op_operacion=@PROD_CAR_OPE2
INSERT INTO cob_cartera..ca_operacion (op_operacion,op_banco,op_anterior,op_migrada,op_tramite,op_cliente,op_nombre,op_sector,op_toperacion,op_oficina,op_moneda,op_comentario,op_oficial,op_fecha_ini,op_fecha_fin,op_fecha_ult_proceso,op_fecha_liq,op_fecha_reajuste,op_monto,op_monto_aprobado,op_destino,op_lin_credito,op_ciudad,op_estado,op_periodo_reajuste,op_reajuste_especial,op_tipo,op_forma_pago,op_cuenta,op_dias_anio,op_tipo_amortizacion,op_cuota_completa,op_tipo_cobro,op_tipo_reduccion,op_aceptar_anticipos,op_precancelacion,op_tipo_aplicacion,op_tplazo,op_plazo,op_tdividendo,op_periodo_cap,op_periodo_int,op_dist_gracia,op_gracia_cap,op_gracia_int,op_dia_fijo,op_cuota,op_evitar_feriados,op_num_renovacion,op_renovacion,op_mes_gracia,op_reajustable,op_origen_fondo,op_fondos_propios,op_dias_desembolso,op_fecha_ins_desembolso,op_nro_bmi,op_cupos_terceros,op_sector_contable,op_clabas,op_clabope,op_plazo_contable,op_fecha_ven_legal,op_cuota_ballom,op_cuota_menor,op_tcertificado,op_estado_manual,op_prd_cobis,op_sujeta_nego,op_ref_exterior,op_via_judicial,op_reest_int,op_garant_emi,op_oficial_cont,op_premios,op_saldo,op_base_calculo,op_ajuste_moneda,op_moneda_ajuste,op_product_group,op_lin_comext,op_tipo_prioridad,op_promotor,op_vendedor,op_comision_pro,op_cuota_incluye,op_dias_prorroga,op_subsidio,op_porcentaje_subsidio,op_financia,op_abono_ini,op_opcion_compra,op_beneficiario,op_factura,op_tpreferencial,op_porcentaje_preferencial,op_monto_preferencial,op_comision_ven,op_cuenta_vendedor,op_agencia_venta,op_comision_agencia,op_cuenta_agencia,op_canal_venta,op_iniciador,op_entrevistador,op_fecha_ult_mod,op_usuario_mod,op_referido,op_num_reest,op_num_prorroga,op_fecha_cambio_est,op_num_reajuste,op_gracia_mora,op_modo_reest,op_estado_deuda,op_dividendo_vig,op_fecha_ult_reest,op_fecha_ult_pago,op_fecha_ult_pago_cap,op_fecha_ult_pago_int,op_monto_ult_pago,op_monto_ult_pago_cap,op_monto_ult_pago_int,op_sec_ult_pago,op_efecto_pago,op_fecha_ult_reaj,op_tasa_anterior,op_monto_promocion,op_saldo_promocion,op_tipo_promocion,op_cuota_promocion,op_compra_operacion) 
VALUES(@PROD_CAR_OPE2,@PROD_CAR2,NULL,NULL,2355,@ENTE_COBIS_TEST,'CLIENTE (@ENTE_COBIS_TEST)','PERSONAL','COMEPRE',1,0,'INGRESADA DESDE ULTIMUS',1008,'2012-11-30 00:00:00.0','2014-02-23 00:00:00.0','2013-01-02 00:00:00.0','2012-11-30 00:00:00.0','2013-05-29 00:00:00.0',10000.0000,10000.0000,'2',NULL,6,1,3,'S','N',NULL,@PROD_CAR2,360,'FRANCESA','N','P','N','S','S','D','MENSUAL',15,'MENSUAL',1,1,NULL,0,0,NULL,698.2000,NULL,NULL,'S',0,'S','PROPIOS','S',NULL,'2012-11-30 00:00:00.0',NULL,NULL,NULL,NULL,NULL,'1','2014-02-23 00:00:00.0','N','N',NULL,NULL,7,'N',NULL,'N',NULL,'N',1008,NULL,10000.0000,NULL,NULL,NULL,NULL,NULL,'N',1,NULL,NULL,'T',NULL,'N',0.0,NULL,NULL,NULL,NULL,NULL,'N',0.0,0.0000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2012-11-30 00:00:00.0','gcueva',NULL,NULL,NULL,'2012-11-30 00:00:00.0',2,'N','P','11',2,NULL,'2013-01-02 00:00:00.0',NULL,NULL,10.0000,0.0000,0.0000,280,'M',NULL,NULL,NULL,0.0000,NULL,NULL,NULL)

delete from cob_cartera..ca_abono where ab_operacion=@PROD_CAR_OPE2
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg) 
VALUES(@PROD_CAR_OPE2,258,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',258,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg) 
VALUES(@PROD_CAR_OPE2,259,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',259,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,260,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',260,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,261,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',261,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,262,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',262,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,263,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',263,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,264,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',264,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,265,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'ING','ewilliams',1,'PC01GYECAP41','PAG','D',265,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,266,0,0,'2013-01-02 00:00:00.0','2013-05-01 00:00:00.0','N','S','N','P',0,0,'E  ','ewilliams',1,'PC01GYECAP41','PAG','D',266,NULL,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,5,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,268,0,0,'2013-01-02 00:00:00.0','2013-01-02 00:00:00.0','N','S','N','P',0,0,'ING','admuser',1,'PC01SOLBAN143','PAG','D',268,NULL,0,NULL,'N',NULL,'S',5,'N',NULL,NULL,'N',NULL,NULL,NULL,NULL,0,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,269,270,271,'2013-01-02 00:00:00.0','2013-01-02 00:00:00.0','N','S','N','P',0,0,'A  ','admuser',1,'PC01SOLBAN143','PAG','D',269,1286963.9600,0,NULL,'N',NULL,'S',5,'N',NULL,NULL,'A','admuser','2013-01-02 00:00:00.0',NULL,NULL,0,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,275,276,277,'2013-01-02 00:00:00.0','2013-01-02 00:00:00.0','N','S','N','P',0,0,'A  ','usuariobv',1,'192.168.83.158','PAG','D',275,1286963.9600,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'A','usuariobv','2013-01-02 00:00:00.0',NULL,NULL,0,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,272,273,274,'2013-01-02 00:00:00.0','2013-01-02 00:00:00.0','N','S','N','P',0,0,'A  ','admuser',1,'PC01SOLBAN143','PAG','D',272,1286963.9600,0,NULL,'N',NULL,'S',5,'N',NULL,NULL,'A','admuser','2013-01-02 00:00:00.0',NULL,NULL,0,'M',NULL)
INSERT INTO cob_cartera..ca_abono (ab_operacion,ab_secuencial_ing,ab_secuencial_rpa,ab_secuencial_pag,ab_fecha_ing,ab_fecha_pag,ab_cuota_completa,ab_aceptar_anticipos,ab_tipo_reduccion,ab_tipo_cobro,ab_dias_retencion_ini,ab_dias_retencion,ab_estado,ab_usuario,ab_oficina,ab_terminal,ab_tipo,ab_tipo_aplicacion,ab_nro_recibo,ab_saldo,ab_dividendo,ab_cod_orides,ab_tipo_prioridad,ab_rubro_pago,ab_negociado,ab_dias_validez,ab_pago_anticipado,ab_fecha_reversa,ab_usuario_reversa,ab_estado_aprob,ab_usuario_aprob,ab_fecha_aprob,ab_usuario_mod,ab_fecha_mod,ab_canal,ab_efecto_pago,ab_monto_no_neg)
VALUES(@PROD_CAR_OPE2,278,279,280,'2013-01-02 00:00:00.0','2013-01-02 00:00:00.0','N','S','N','P',0,0,'A  ','usuariobv',1,'192.168.83.158','PAG','D',278,1286963.9600,NULL,NULL,'N',NULL,'N',0,'N',NULL,NULL,'A','usuariobv','2013-01-02 00:00:00.0',NULL,NULL,0,'M',NULL)

delete from cob_cartera..ca_abono_det where abd_operacion=@PROD_CAR_OPE2
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,258,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,100.0000,100.0000,100.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,259,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,100.0000,100.0000,100.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,260,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,100.0000,100.0000,100.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,261,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,80.0000,80.0000,80.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,262,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,70.0000,70.0000,70.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,263,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,50.0000,50.0000,50.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,264,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,10.0000,10.0000,10.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,265,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,1000.0000,1000.0000,1000.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,266,'PAG','EFEC0','1','PAGO POR ATX                                      ',0,100.0000,100.0000,100.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,268,'PAG','EFEC','1','2                                                 ',0,40.0000,40.0000,40.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,269,'PAG','EFEC','12','23                                                ',0,40.0000,40.0000,40.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,272,'PAG','EFEC','1','2                                                 ',0,20.0000,20.0000,20.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,275,'PAG','EFEC0','10410108275249013','                                                  ',0,10.0000,10.0000,10.0000,1.0,1.0,'N','N')
INSERT INTO cob_cartera..ca_abono_det (abd_operacion,abd_secuencial_ing,abd_tipo,abd_concepto,abd_cuenta,abd_beneficiario,abd_moneda,abd_monto_mpg,abd_monto_mop,abd_monto_mn,abd_cotizacion_mpg,abd_cotizacion_mop,abd_tcotizacion_mpg,abd_tcotizacion_mop) 
VALUES(@PROD_CAR_OPE2,278,'PAG','EFEC0','10410108275249013','                                                  ',0,10.0000,10.0000,10.0000,1.0,1.0,'N','N')

delete from cob_cartera..ca_transaccion where tr_operacion = @PROD_CAR_OPE2
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,6,'2012-11-30 00:00:00.0','COMEPRE   ',0,'DES       ','S',@PROD_CAR2,6,1,1,'xcueva        ','PC01SOLBA','2012-11-30 00:00:00.0',0,'RV        ','PERSONAL',1008,'01','2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,8,'2012-11-30 00:00:00.0','COMEPRE   ',0,'PRS       ','S',@PROD_CAR2,0,1,1,'xcueva        ','PC01SOLBA','2012-11-30 00:00:00.0',0,'RV        ','PERSONAL',1008,'01','2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,9,'2012-11-30 00:00:00.0','COMEPRE   ',0,'PRS       ','S',@PROD_CAR2,0,1,1,'xcueva        ','PC01SOLBA','2012-11-30 00:00:00.0',0,'RV        ','PERSONAL',1008,'01','2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,13,'2012-11-30 00:00:00.0','COMEPRE   ',0,'DES       ','S',@PROD_CAR2,13,1,1,'xcueva        ','PC01SOLBA','2012-11-30 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,17,'2012-12-10 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'xcueva        ','PC01SOLBA','2012-11-30 00:00:00.0',17,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,51,'2013-01-02 00:00:00.0','COMEPRE   ',0,'CPC       ','S',@PROD_CAR2,1,1,1,'xcueva        ','PC01SOLBA','2013-01-01 00:00:00.0',0,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,52,'2013-01-02 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'xcueva        ','PC01SOLBA','2013-01-01 00:00:00.0',52,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,54,'2012-12-28 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-11-30 00:00:00.0',54,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,15,'2012-11-30 00:00:00.0','COMEPRE   ',0,'PRS       ','S',@PROD_CAR2,0,1,1,'xcueva        ','PC01SOLBA','2012-11-30 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,16,'2012-11-30 00:00:00.0','COMEPRE   ',0,'PRS       ','S',@PROD_CAR2,0,1,1,'xcueva        ','PC01SOLBA','2012-11-30 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,88,'2013-01-02 00:00:00.0','COMEPRE   ',0,'CPC       ','N',@PROD_CAR2,1,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',0,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,89,'2012-12-28 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',89,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,90,'2012-12-28 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-11-30 00:00:00.0',90,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,124,'2013-01-02 00:00:00.0','COMEPRE   ',0,'CPC       ','N',@PROD_CAR2,1,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',0,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,125,'2012-12-28 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',125,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,126,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-11-30 00:00:00.0',126,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,128,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-12-01 00:00:00.0',128,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,129,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-11-30 00:00:00.0',129,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,131,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-12-01 00:00:00.0',131,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,164,'2013-01-02 00:00:00.0','COMEPRE   ',0,'CPC       ','N',@PROD_CAR2,1,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',0,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,165,'2012-12-28 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',165,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,166,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-11-30 00:00:00.0',166,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,168,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-12-01 00:00:00.0',168,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,169,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-11-30 00:00:00.0',169,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,171,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-12-01 00:00:00.0',171,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,204,'2013-01-02 00:00:00.0','COMEPRE   ',0,'CPC       ','N',@PROD_CAR2,1,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',0,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,205,'2012-12-28 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',205,'RV        ','PERSONAL',1008,NULL,'2012-11-30 00:00:00.0',NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,206,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-11-30 00:00:00.0',206,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,208,'2012-11-30 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2012-12-01 00:00:00.0',208,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,241,'2013-01-02 00:00:00.0','COMEPRE   ',0,'CPC       ','N',@PROD_CAR2,1,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,242,'2012-12-28 00:00:00.0','COMEPRE   ',0,'FVA       ','N',@PROD_CAR2,0,1,1,'sa            ','CONSOLA','2013-01-01 00:00:00.0',242,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,244,'2013-05-09 00:00:00.0','COMEPRE   ',0,'IOC       ','S',@PROD_CAR2,0,1,1,'fernando      ','PC01TEC11','2013-01-02 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,256,'2013-05-09 00:00:00.0','COMEPRE   ',0,'REJ       ','S',@PROD_CAR2,0,1,1,'dmejia        ','PC01GYECA','2013-01-02 00:00:00.0',255,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,271,'2013-10-23 00:00:00.0','COMEPRE   ',0,'PAG       ','S',@PROD_CAR2,0,1,1,'admuser       ','PC01SOLBAN143','2013-01-02 00:00:00.0',270,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,273,'2013-10-23 00:00:00.0','COMEPRE   ',0,'RPA       ','S',@PROD_CAR2,272,1,1,'admuser       ','PC01SOLBAN143','2013-01-02 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,274,'2013-10-23 00:00:00.0','COMEPRE   ',0,'PAG       ','S',@PROD_CAR2,0,1,1,'admuser       ','PC01SOLBAN143','2013-01-02 00:00:00.0',273,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,276,'2013-01-02 00:00:00.0','COMEPRE   ',0,'RPA       ','S',@PROD_CAR2,275,1,1,'usuariobv     ','192.168.83.158','2013-01-02 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,277,'2013-01-02 00:00:00.0','COMEPRE   ',0,'PAG       ','S',@PROD_CAR2,0,1,1,'usuariobv     ','192.168.83.158','2013-01-02 00:00:00.0',276,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,279,'2013-01-02 00:00:00.0','COMEPRE   ',0,'RPA       ','S',@PROD_CAR2,278,1,1,'usuariobv     ','192.168.83.158','2013-01-02 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,280,'2013-01-02 00:00:00.0','COMEPRE   ',0,'PAG       ','S',@PROD_CAR2,0,1,1,'usuariobv     ','192.168.83.158','2013-01-02 00:00:00.0',279,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,257,'2013-05-09 00:00:00.0','COMEPRE   ',0,'REJ       ','S',@PROD_CAR2,0,1,1,'dmejia        ','PC01GYECA','2013-01-02 00:00:00.0',255,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)
INSERT INTO cob_cartera..ca_transaccion (tr_operacion,tr_secuencial,tr_fecha_mov,tr_toperacion,tr_moneda,tr_tran,tr_en_linea,tr_banco,tr_dias_calc,tr_ofi_oper,tr_ofi_usu,tr_usuario,tr_terminal,tr_fecha_ref,tr_secuencial_ref,tr_estado,tr_sector,tr_oficial,tr_causa_rev,tr_fecha_reversa,tr_comprobante,tr_fecha_cont) 
VALUES(@PROD_CAR_OPE2,270,'2013-10-23 00:00:00.0','COMEPRE   ',0,'RPA       ','S',@PROD_CAR2,269,1,1,'admuser       ','PC01SOLBAN143','2013-01-02 00:00:00.0',0,'ING       ','PERSONAL',1008,NULL,NULL,NULL,NULL)

delete from cob_custodia..cu_doctos where do_documento=1234
INSERT INTO cob_custodia..cu_doctos (do_filial,do_documento,do_emisor,do_num_doc,do_sucursal,do_num_negocio,do_aceptante,do_tasa,do_precio_compra,do_fecha_proc,do_tipo_doc,do_moneda,do_valor_bruto,do_anticipos,do_por_impuestos,do_por_retencion,do_valor_neto,do_valor_neg,do_ciudad,do_fecha_emision,do_fecha_vtodoc,do_fecha_inineg,do_fecha_vtoneg,do_fecha_pago,do_base_calculo,do_dias_negocio,do_num_dex,do_fecha_dex,do_cliente,do_resp_pago,do_resp_dscto,do_observaciones,do_estado,do_agrupado,do_grupo,do_recaudado) 
VALUES(1,1234,1,'1234',1,'1234',1,1.0,2.0,'2013-01-01 00:00:00.0','4',0,120.0000,20.0000,12.0,12.0,120.0000,0.0000,1,'2013-12-12 00:00:00.0','2013-12-12 00:00:00.0','2013-12-12 00:00:00.0','2013-12-12 00:00:00.0','2013-12-12 00:00:00.0','0',0,'1','2013-12-12 00:00:00.0',@ENTE_COBIS_TEST,0,0,'0','A','N',1,0.0000)

delete from cob_credito..cr_facturas where fa_documento=1234
INSERT INTO cob_credito..cr_facturas (fa_tramite,fa_documento,fa_num_negocio,fa_grupo,fa_valor,fa_moneda,fa_fecini_neg,fa_fecfin_neg,fa_usada,fa_dividendo,fa_referencia,fa_porcentaje) 
VALUES(13677,1234,'15',1,123.0000,0,'2013-01-01 00:00:00.0','2013-01-12 00:00:00.0','0',1234,'123456789',0.0)

delete from cob_cartera..ca_pago_automatico where pa_operacion=@PROD_CAR_OPE2
INSERT INTO cob_cartera..ca_pago_automatico (pa_operacion,pa_forma_pago,pa_cuenta,pa_monto,pa_rubro,pa_institucion,pa_cliente,pa_rol,pa_comentario) 
VALUES(@PROD_CAR_OPE2,'DESCTDIR',NULL,50000000.0000,NULL,'903',NULL,NULL,NULL)




/* ----------------------------------------------------------------------------------------------------- */

/* Simuladores de Prestamos */
delete from tempdb..pint4 where pro_bancario=100
insert into tempdb..pint4 ( tipo_ente, pro_bancario, filial, sucursal, producto, moneda, servicio_dis, rubro, tipo_atributo, rango_desde, rango_hasta, valor, secuencial, categoria, rango ) values ('P',100,1,1,4,0,'PINT','18','1',1,10000,10,1,'A',1)

delete from tempdb..pe_tipo_atributo where pro_bancario=100
insert into tempdb..pe_tipo_atributo ( tipo_atributo, filial, sucursal, producto, pro_bancario, tipo_cta, moneda, servicio, rubro ) values ( '1', 1, 1, 4, 100, 'P', 0, 'PINT', '18' ) 

/* ----------------------------------------------------------------------------------------------------- */


/* Internacionales */
delete from cob_comext..ce_operacion where op_operacion=@PROD_CEX_OPE
delete from cob_comext..ce_operacion where op_operacion_banco=@PROD_CEX and op_tipo_oper='TRR'
insert into cob_comext..ce_operacion ( op_operacion, op_operacion_banco, op_tipo_oper, op_etapa, op_oficina, op_oficina_pertenece, op_filial, op_producto, op_tipo, op_oficial, op_usuario, op_fecha, op_irrevocable, op_confirmado, op_pre_aviso, op_fecha_prea, op_fecha_solic, op_fecha_emis, op_fecha_expir, op_ordenante, op_direccion_ord, op_tipo_cuenta, op_cuenta, op_categoria, op_ced_ruc, op_cod_ben, op_ced_ruc_ben, op_beneficiario, op_direccion_ben, op_ciudad_ben, op_pais_ben, op_continente_ben, op_moneda, op_tolerancia_superior, op_tolerancia_inferior, op_importe, op_importe_calc, op_termino, op_mensaje, op_prioridad, op_convenio, op_transporte, op_tcorreo, op_tipo_importe, op_transbordo, op_emb_parcial, op_nro_embarques, op_notificacion, op_flete, op_puerto_embarque, op_puerto_destino, op_ult_fecha_embarque, op_documentos, op_plazo_pres_dtos, op_transaccion, op_mercancia, op_poliza, op_enmienda, op_letra, op_gastos_bancarios, op_financia, op_saldo, op_saldo_credito, op_lugar_expir, op_transferible, op_tasa_interes_clt, op_interes_gen, op_fecha_etapa, op_nro_referencia, op_nro_ref_opc, op_tipo_ben, op_ult_ben, op_resp_operacion, op_fecha_cancelacion, op_por_cuenta_de, op_direccion_pcd, op_adelantada, op_nro_aprobacion, op_ref_nota_emision, op_abono, op_fecha_acuse, op_tasa_interes_ext, op_fecha_apd, op_renovacion, op_tipo_aval, op_tipo_tasint, op_disponibilidad, op_disp_banco, op_disp_oficina, op_disp_continente, op_disp_pais, op_disp_ciudad, op_his_est_ope, op_fpago, op_cod_dir_ben, op_plazo_financ, op_ref_cliente, op_tipo_tasa, op_signo, op_oper_sba, op_especifica, op_por_orden_de, op_direccion_pod, op_telefono, op_fax, op_email, op_fondeos, op_prov_ben, op_importe_ml, op_fecha_valor, op_tipo_conceptr, op_tipo_plazo, op_nro_mensaje ) 
values ( @PROD_CEX_OPE, @PROD_CEX, 'TRR', '41', 1, 1, 1, 9, 'R', 103, 'ibtest', '1/9/2008 12:00:00.000 AM', NULL, NULL, NULL, NULL, NULL, '1/9/2008 12:00:00.000 AM', '1/9/2008 12:00:00.000 AM', 13036, 1, 'CTE', '01221000012', 'N', '03-0090-01619', NULL, NULL, 'SANTA CLARA ESTATES LTD.', 'P.O. BOX 0831-00975', 13403, 840, 'AMN', 0, NULL, NULL, 19305.00, 19305.00, NULL, NULL, NULL, 'N', NULL, 'AUT', NULL, NULL, NULL, 1, 'N/A', NULL, NULL, NULL, '1/9/2008 12:00:00.000 AM', 1, 0, 3, 0, NULL, 0, NULL, NULL, NULL, 0.00, NULL, NULL, NULL, NULL, NULL, '1/9/2008 12:00:00.000 AM', '008431490', NULL, NULL, 'MOISES DAVID COHEN MUGRABI', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1/9/2008 12:00:00.000 AM', NULL, NULL, NULL, 'C', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 4, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'M', NULL, NULL )

delete from cob_comext..ce_transaccion where tr_operacion=@PROD_CEX_OPE
insert into cob_comext..ce_transaccion ( tr_operacion, tr_sec_transaccion, tr_tipo_trn, tr_numero, tr_sec_fpago, tr_plazo, tr_fecha, tr_usuario, tr_fecha_reg, tr_sec_detalle_trn, tr_sec_detalle_pgo, tr_ext_concepto, tr_comprobante, tr_comp_alterno, tr_nro_comprobante, tr_nro_comp_alterno, tr_oficina, tr_estado, tr_fecha_maxima, tr_terminal, tr_banco, tr_toperacion, tr_ofi_usu ) 
values ( @PROD_CEX_OPE, 1, 'AP1', NULL, NULL, NULL, '1/9/2008 12:00:00.000 AM', 'mcarring', '1/9/2008 12:00:00.000 AM', 1, 1, 'APERTURA OP.#: TRR00108000011 del 01/09/2008', '1201', NULL, 1, NULL, 1, 'C', NULL, NULL, NULL, NULL, NULL )

delete from cob_comext..ce_instruccion where in_operacion=@PROD_CEX_OPE
insert into cob_comext..ce_instruccion ( in_operacion, in_tipo_oper, in_tipo_trn, in_tipo_instruccion, in_numero, in_descripcion_a, in_descripcion_b, in_descripcion_c ) 
values ( @PROD_CEX_OPE, 'STB', 'AP1', 'S11', NULL, 'CERTIFICACION ESCRITA POR PARTE DEL BENEFICIARIO EN DONDE CONFIRME QUE DERIVADOS ELECTRONICOS, C.A. Y/O INTER GLOBAL TRADING, INC. NO CUMPLIO CON LOS PAGOS Y QUE SE ENCUENTRAN VENCIDOS POR MAS DE 60 DIAS', NULL, NULL )

delete from cob_comext..ce_operacion_rol where ol_operacion=@PROD_CEX_OPE
insert into cob_comext..ce_operacion_rol ( ol_operacion, ol_banco, ol_oficina, ol_rol, ol_fecha, ol_nro_lc, ol_tipo_uso, ol_nro_reserva, ol_tipo_cuenta, ol_cuenta, ol_dir_swift ) 
values ( @PROD_CEX_OPE, 12417, 2, '5', NULL, NULL, NULL, NULL, 'CTE', '048-220057-001', 'S' )
insert into cob_comext..ce_operacion_rol ( ol_operacion, ol_banco, ol_oficina, ol_rol, ol_fecha, ol_nro_lc, ol_tipo_uso, ol_nro_reserva, ol_tipo_cuenta, ol_cuenta, ol_dir_swift ) 
values ( @PROD_CEX_OPE, 20539, 1, '12', NULL, NULL, NULL, NULL, NULL, NULL, 'S' )


/* ----------------------------------------------------------------------------------------------------- */

/* Lotes */
delete from cob_bvirtual..bv_mpayment_account where fo_file_id=1000
/* ----------------------------------------------------------------------------------------------------- */

/* Tarjetas */
delete from cob_cams_his..tm_tcr_totales where ta_tarjeta=1000
insert into cob_cams_his..tm_tcr_totales  ( ta_tarjeta, ta_codigo, to_numctabnx, to_mtosconal, to_mtodebnal, to_mtocrenal, to_mtocxsnal, to_mtosplnal, to_mtoivenal, to_mtocmenal, to_mtopminal, to_numpvenal, to_mtoscodol, to_mtocxsdol, to_mtospldol, to_mtoivedol, to_mtocmedol, to_mtopmidol, to_numpvedol, to_mtodebdol, to_mtocredol, to_nomtitula, to_mtolimcre, to_mtocredis, to_feccorant, to_feccorte, to_fecvencim, to_mtosantlo, to_mtosantdo, to_mtolimexf, to_tassernal, to_tasserdol, to_tasmornal, to_tasmordol, to_estado, to_pagado_colones, to_pagado_dolares, tcc_secuencial, to_fecha_cuadre, to_mtoiniprem, to_mtodebprem, to_mtocreprem, to_mtototprem, to_disefenal, to_disefedol, to_carsobnal, to_carsobdol, to_crecontar, to_cresalcta, to_cremanual, to_crebonifi, to_debcanred, to_debpenali, to_debmanual, to_numctatar, to_ciclcorte, to_intcordol, to_intcornal, to_icpermdol, to_icpermnal, to_rcvenmdol, to_rcvenmnal, to_inmormdol, to_inmormnal, to_innfimnal, to_innfimdol, to_cfamondol, to_cfamonnal, to_fecpago, to_pagominprinml, to_pagominprinme, to_pagomininteml, to_pagomininteme, to_pagomintarjml, to_pagomintarjme, to_salantprinml, to_salantprinme, to_salantintml, to_salantintme, to_salcorprinml, to_salcorprinme, to_salcorintml, to_salcorintme, to_tasinteanuml, to_tasinteanume, to_tasintmoranuml, to_tasintmoranume, to_plazotarjlo, to_plazotarjin, to_intdmornopagml, to_intdmornopagme ) 
values ( 1000, @PROD_TAR, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 4.44, 0.00, 6.67, 2.22, 0.00, 0.00, 0.00, 5.56, 0.00, 7.78, 0.00, 0.00, 0.00, 'TEST IB', 0.00, 0.00, '1/1/2012 12:00:00.000 AM', '1/1/2013 12:00:00.000 AM', '12/12/2012 12:00:00.000 AM', 8.89, 10.00, 0.00, 0, 0, 0, 0, 'UNKNOWN', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL )

delete from cob_atm..tm_tarjeta where ta_codigo='1234567890000000'
insert into cob_atm..tm_tarjeta ( ta_banco, ta_tarjeta, ta_codigo, ta_miembro, ta_ult_miembro, ta_version, ta_principal, ta_tipo_tarjeta, ta_nombre_tarjeta, ta_saludo, ta_estado_tarjeta, ta_fecha_estado, ta_tipo_sol, ta_motivo, ta_estado_aux, ta_tipo_sol_aux, ta_motivo_aux, ta_cliente, ta_propietario, ta_ced_ruc, ta_retencion, ta_num_retencion, ta_fecha_expiracion, ta_fecha_renov, ta_fecha_mante, ta_fecha_cobro_mante, ta_lugar_primer_mov, ta_fecha_primer_mov, ta_solicitud, ta_fecha_sol, ta_tipo_sol_org, ta_fecha_elaboracion, ta_fecha_entregada, ta_persona_retira, ta_ofi_org, ta_ofi_actual, ta_ofi_ent, ta_tipo_ent, ta_confirmado, ta_cupo_periodo, ta_tipo_cupo, ta_cupo_atm_nac, ta_cupo_pos_nac, ta_cupo_atm_int, ta_cupo_pos_int, ta_cupo_gen_nac, ta_cupo_gen_int, ta_cupo_transf, ta_saldo_atm_nac, ta_saldo_pos_nac, ta_saldo_atm_int, ta_saldo_pos_int, ta_saldo_gen_nac, ta_saldo_gen_int, ta_saldo_transf, ta_lugar_ult_mov, ta_fecha_ult_mov, ta_fecha_ult_mov_atm_nac, ta_fecha_ult_mov_pos_nac, ta_fecha_ult_mov_atm_int, ta_fecha_ult_mov_pos_int, ta_fecha_ult_mov_gen_nac, ta_fecha_ult_mov_gen_int, ta_fecha_ult_mov_transf, ta_sol_act, ta_fecha_act, ta_sol_can, ta_fecha_can, ta_cuenta_coop, ta_plastico, ta_convenio, ta_renov_automatica, ta_cont_trn, ta_user_estado, ta_ofi_estado, ta_comentario_estado, ta_tarj_reemplazo, ta_oficial, ta_distrib, ta_fecha_activacion, ta_chip, ta_cc_code, ta_cod_lealtad, ta_limite_credito, ta_limite_credito_int, ta_cuenta_cliente, ta_cupo_atm_men, ta_cupo_pos_men, ta_cupo_atm_int_men, ta_cupo_pos_int_men, ta_cupo_transf_men, ta_transacciones, ta_transacciones_men, ta_saldo_atm_men, ta_saldo_pos_men, ta_saldo_atm_int_men, ta_saldo_pos_int_men, ta_saldo_transf_men, ta_direccion_ec, ta_numero_emp ) 
values ( 1, 10000, '1234567890000000', 0, 0, 0, 0, 'MCA', 'TEST IB TARJ', 'JJJ', 'A', '1/2/2014 12:00:00.000 AM', 'CAN', 'REP', 'A', 'CAN', 'REP', @ENTE_COBIS_TEST, @ENTE_COBIS_TEST, NULL, 'S', 0, '10/31/2015 12:00:00.000 AM', NULL, NULL, NULL, NULL, NULL, 1, '11/30/2012 10:59:00.000 AM', 'MNT', NULL, NULL, 'PROPIETARIO', 1, 1, 1, 'O', NULL, 'D', 'E', 1, 2, 1, 2, NULL, NULL, 9, NULL, NULL, NULL, NULL, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 114, '11/30/2012 3:21:31.000 PM', NULL, '1/10/2013 5:24:47.033 PM', NULL, '0', 1, 'A', 0, 'jgarcia', 1, 'ELIMINACION POR REEXPEDICION D', NULL, 800, 'S', NULL, 'N', NULL, '0', 0.00, 0.00, '10410000005405100', NULL, NULL, NULL, NULL, 11, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL )

delete from cob_atm..tm_emision where em_cuenta='1234567890000000'
insert into cob_atm..tm_emision ( em_banco, em_tarjeta, em_producto, em_prod_cobis, em_prod_banc, em_moneda, em_cuenta, em_tipo, em_fecha, em_cupo_online, em_cupo_offline, em_periodo, em_cupo_transferencia, em_principal, em_codigo, em_ult_lugar, em_fecha_ult_mov, em_saldo_cupo, em_orden, em_cupo_online_men, em_cupo_offline_men, em_cupo_transf_men ) 
values ( 1, 1000, 3, 3, 1, 0, '1234567890000000', 'S', '11/30/2012 10:59:00.000 AM', 2500.00, 2500.00, 'D', 22000.00, 'P', NULL, NULL, NULL, 2500.00, 1, NULL, NULL, NULL )
/* ----------------------------------------------------------------------------------------------------- */

/* Varios */
delete from cob_bvirtual..bv_convenio_inscrito where ci_ente=@ENTE_COBIS_TEST or ci_id in(1000,1001)
insert into cob_bvirtual..bv_convenio_inscrito ( ci_id, ci_ente, ci_login, ci_id_categoria, ci_id_convenio, ci_num_doc, ci_descripcion, ci_estado, ci_fecha_creacion, ci_fecha_modificacion, ci_ente_bv ) 
values ( 1000, @ENTE_COBIS_TEST, @LOGIN, '07', 1000, @LOGIN_NUM_DOC, 'IB TEST', 'V', '7/15/2013 4:24:03.853 PM', '7/15/2013 4:24:03.853 PM', @ENTE_BV_TEST )

delete from cob_bvirtual..bv_convenio_inscrito_param where cip_num_doc=@LOGIN_NUM_DOC or cip_id in(1000,1001)
insert into cob_bvirtual..bv_convenio_inscrito_param ( cip_id, cip_llave, cip_tipo_doc, cip_num_doc, cip_referencia1, cip_referencia2, cip_referencia3, cip_referencia4, cip_referencia5, cip_referencia6, cip_referencia7, cip_referencia8, cip_referencia9, cip_referencia10, cip_referencia11, cip_referencia12 ) 
values ( 1000, '001', '001', @LOGIN_NUM_DOC, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL )

delete from cob_remesas..re_convenio where cv_convenio=1000
insert into cob_remesas..re_convenio ( cv_convenio, cv_nombre, cv_nemonico, cv_descripcion, cv_estado, cv_cliente, cv_tipo_interfaz, cv_tipo_arch, cv_num_conv, cv_moneda, cv_format_imp, cv_efectivo, cv_doc_propio, cv_chq_local, cv_debito_cta, cv_pago_cat, cv_pago_caf, cv_forma_pago, cv_tipo_cta, cv_num_cta, cv_period_pago, cv_benef_chq, cv_ofi_chq, cv_comi_cli, cv_comi_tipo, cv_comi_conv, cv_comi_period, cv_comi_tranex, cv_fecha_mod, cv_usuario_mod, cv_categoria ) 
values ( 1000, 'CONVENIO PRUEBA', 'IBTEST', 'IBTEST', 'V', 108276, 'N', '0', 70, 0, 'FREC002', 'S', 'N', 'N', 'S', 'N', 'N', 'D', 'AHO', '10410108463222813', NULL, NULL, NULL, 0.00, NULL, 0.00, NULL, NULL, '5/1/2013 12:00:00.000 AM', 'testcts', '10' )

delete from cob_remesas..re_base_factura where bf_identificacion=@LOGIN_NUM_DOC
insert into cob_remesas..re_base_factura ( bf_secuencial, bf_convenio, bf_identificacion, bf_nombre, bf_valor, bf_fecha_limite, bf_reftxt_01, bf_reftxt_02, bf_reftxt_03, bf_reftxt_04, bf_reftxt_05, bf_reftxt_06, bf_reftxt_07, bf_reftxt_08, bf_reftxt_09, bf_reftxt_10, bf_reftxt_11, bf_reftxt_12, bf_fecha_carga, bf_estado, bf_archivo ) 
values ( 1000, 1000, @LOGIN_NUM_DOC, @LOGIN, 1234.45, '1/7/2013 12:00:00.000 AM', '1', '0213307102', '01', '22590049', '22590049', '10', NULL, NULL, NULL, NULL, NULL, NULL, '5/5/2012 12:00:00.000 AM', 'V ', 15 )


/*ValidateAccountsRelations */		
update cobis..cl_det_producto 
set  dp_cliente_ec= @ente_mis_prueba_empresa
where dp_cuenta = @acc_ctacte_number
or dp_cuenta = @acc_ctaaho_number_double_auth_empresa

delete cobis..cl_cliente
where cl_det_producto in (select dp_det_producto from cobis..cl_det_producto where dp_cuenta = @acc_ctacte_number or dp_cuenta = @acc_ctaaho_number_double_auth_empresa)
and cl_cliente <> @ente_mis_prueba_empresa

update cobis..cl_cliente 
set cl_cliente = @ente_mis_prueba_empresa
where cl_det_producto in (select dp_det_producto from cobis..cl_det_producto where dp_cuenta = @acc_ctacte_number or dp_cuenta = @acc_ctaaho_number_double_auth_empresa)

/*Cta bloqueada */
delete cob_cuentas..cc_ctabloqueada where cb_cuenta = (select cc_ctacte  from cob_cuentas..cc_ctacte where cc_cta_banco= @acc_ctacte_number)

/*CTA DE ENTE EMPRESA */
delete from cobis..cl_cliente where cl_cliente=@ente_mis_prueba_empresa
insert into cobis..cl_cliente values(137488,4723,'T','0600000871',getdate())
insert into cobis..cl_cliente values(137488,4872,'T','0600000871',getdate())
insert into cobis..cl_cliente values(137488,4821,'T','0600000871',getdate())

/*Cta de Ente Natural */
update cobis..cl_det_producto 
set  dp_cliente_ec= @ente_mis_prueba
where dp_cuenta in( @acc_ctaaho_number,@acc_ctacte_number,@acc_ctacte_number_USD)

delete cobis..cl_cliente
where cl_det_producto in (select dp_det_producto from cobis..cl_det_producto where dp_cuenta in( @acc_ctaaho_number,@acc_ctacte_number,@acc_ctacte_number_USD))
and cl_cliente <> @ente_mis_prueba

declare @id_temp_product_aho int,@id_temp_product_cte int,@id_temp_product_cte_usd int

select @id_temp_product_aho=dp_det_producto 
from cobis..cl_det_producto
where dp_cuenta in( @acc_ctaaho_number)

select @id_temp_product_cte=dp_det_producto 
from cobis..cl_det_producto
where dp_cuenta in(@acc_ctacte_number)

select @id_temp_product_cte_usd=dp_det_producto 
from cobis..cl_det_producto
where dp_cuenta in(@acc_ctacte_number_USD)

delete cobis..cl_cliente where cl_cliente = @ente_mis_prueba
insert into cobis..cl_cliente values(@ente_mis_prueba,@id_temp_product_aho,'T','1234567890',getdate())
insert into cobis..cl_cliente values(@ente_mis_prueba,@id_temp_product_cte,'T','1234567890',getdate())
insert into cobis..cl_cliente values(@ente_mis_prueba,@id_temp_product_cte_usd,'T','1234567890',getdate())

/*Cta de Ente EMPRESA */
update cobis..cl_det_producto 
set  dp_cliente_ec= @ente_mis_prueba_empresa
where dp_cuenta in( @acc_dpf_number2,@acc2_ctacte_number_USD,@acc_ctacte_number_double_auth_empresa,@acc_ctaaho_number_double_auth_empresa)

delete cobis..cl_cliente
where cl_det_producto in (select dp_det_producto from cobis..cl_det_producto where dp_cuenta in( @acc_dpf_number2,@acc2_ctacte_number_USD,@acc_ctacte_number_double_auth_empresa,@acc_ctaaho_number_double_auth_empresa))
and cl_cliente <> @ente_mis_prueba_empresa

declare @id_temp_product_dpf int,@id_temp_product_ctacta_usd2 int,@id_temp_product_ctacte int,@id_temp_product_ctaaho int

select @id_temp_product_dpf=dp_det_producto 
from cobis..cl_det_producto
where dp_cuenta in(@acc_dpf_number2)

select @id_temp_product_ctacta_usd2=dp_det_producto 
from cobis..cl_det_producto
where dp_cuenta in(@acc2_ctacte_number_USD)

select @id_temp_product_ctacte=dp_det_producto 
from cobis..cl_det_producto
where dp_cuenta in(@acc_ctacte_number_double_auth_empresa)

select @id_temp_product_ctaaho=dp_det_producto 
from cobis..cl_det_producto
where dp_cuenta in(@acc_ctaaho_number_double_auth_empresa)


delete cobis..cl_cliente where cl_cliente = @ente_mis_prueba_empresa
/*insert into cobis..cl_cliente values(@ente_mis_prueba_empresa,@id_temp_product_dpf,'T','1234567890',getdate())*/
--insert into cobis..cl_cliente values(@ente_mis_prueba_empresa,@id_temp_product_ctacta_usd2,'T','1234567890',getdate())
insert into cobis..cl_cliente values(@ente_mis_prueba_empresa,@id_temp_product_ctacte,'T','1234567890',getdate())
insert into cobis..cl_cliente values(@ente_mis_prueba_empresa,@id_temp_product_ctaaho,'T','1234567890',getdate())


/* ----------------------------------------------------------------------------------------------------- */
/* Registro del dpf a un cliente natural */
/* ----------------------------------------------------------------------------------------------------- */
declare @idtempdpf int
select @idtempdpf=op_operacion from cob_pfijo..pf_operacion where op_num_banco in (@acc_dpf_number)
delete from cob_pfijo..pf_beneficiario where be_operacion= @idtempdpf

insert into cob_pfijo..pf_beneficiario (be_operacion,be_ente,be_rol,be_fecha_crea,be_fecha_mod,be_estado_xren,be_estado,be_ced_ruc,be_tipo,be_condicion,be_secuencia)
values(@idtempdpf,@ente_mis_prueba,'T',getdate(),getdate(),'N','I','01-0522-0231','T','O',1)

/* ----------------------------------------------------------------------------------------------------- */

/* ----------------------------------------------------------------------------------------------------- */
/* Registro del dpf a un cliente empresa */
/* ----------------------------------------------------------------------------------------------------- */
select @idtempdpf=op_operacion from cob_pfijo..pf_operacion where op_num_banco in (@acc_dpf_number2)
delete from cob_pfijo..pf_beneficiario where be_operacion= @idtempdpf

insert into cob_pfijo..pf_beneficiario (be_operacion,be_ente,be_rol,be_fecha_crea,be_fecha_mod,be_estado_xren,be_estado,be_ced_ruc,be_tipo,be_condicion,be_secuencia)
values(@idtempdpf,@ente_mis_prueba_empresa,'T',getdate(),getdate(),'N','I','01-0522-0231','T','O',1)

/* ----------------------------------------------------------------------------------------------------- */


/* Actualizacion de Fondos Disponibles para las todas las cuentas */
update cob_ahorros..ah_cuenta set ah_disponible=1000000,ah_cliente=@ente_mis_prueba where ah_cta_banco in (@acc_ctaaho_number_double_auth_empresa,@acc_ctaaho_number)
update cob_cuentas..cc_ctacte set cc_disponible=100000,cc_cliente=@ente_mis_prueba where cc_cta_banco in (@acc_ctacte_number_double_auth_empresa,@acc_ctacte_number,@acc_ctacte_number_USD)
/* ----------------------------------------------------------------------------------------------------- */


/* Registro de data para compra y venta de divisas */
delete from cob_sbancarios..sb_preautori where pr_secuencial in (274,275,276,277)

/*Se comenta por se necesita data para la consulta de movimientos core cobis
 * delete from cob_cuentas..cc_tran_monet*/

/* CLIENTE NATURAL */
insert into cob_sbancarios..sb_preautori ( pr_secuencial, pr_cliente, pr_tipo, pr_moneda, pr_monto_efec_c, pr_monto_otr_c, pr_cotiz_efe_c, pr_cotiz_otr_c, pr_monto_v, pr_cotiz_v, pr_monto_usd_v, pr_factor, pr_chq_ajeno1, pr_chq_ajeno2, pr_cred_cuenta, pr_func_creador, pr_fecha, pr_estado, pr_observaciones, pr_oper_proceso, pr_func_modif, pr_oficina, pr_cod_creador, pr_cod_autorizante, pr_hora_autorizacion, pr_temas_n, pr_fecha_temas_n, pr_cuenta_cobro, pr_cuenta_pago, pr_estado_tn, pr_orig_dest, pr_desc_orgdes, pr_operador, pr_terminal ) 
values ( 274, @ente_mis_prueba, 'C', 17, 400.00, NULL, 400, 1, NULL, 400, 100, 1, NULL, NULL, NULL, 'tyronecruz', '5/1/2013 12:00:00.000 AM', 'P', 'COMPRA', NULL, 'tyronecruz', 1, 43, NULL, NULL, 1, '5/2/2013 12:00:00.000 AM', NULL, NULL, NULL, 1, 'SERVICIOS', '*', 'PC01GYECAP' )

insert into cob_sbancarios..sb_preautori ( pr_secuencial, pr_cliente, pr_tipo, pr_moneda, pr_monto_efec_c, pr_monto_otr_c, pr_cotiz_efe_c, pr_cotiz_otr_c, pr_monto_v, pr_cotiz_v, pr_monto_usd_v, pr_factor, pr_chq_ajeno1, pr_chq_ajeno2, pr_cred_cuenta, pr_func_creador, pr_fecha, pr_estado, pr_observaciones, pr_oper_proceso, pr_func_modif, pr_oficina, pr_cod_creador, pr_cod_autorizante, pr_hora_autorizacion, pr_temas_n, pr_fecha_temas_n, pr_cuenta_cobro, pr_cuenta_pago, pr_estado_tn, pr_orig_dest, pr_desc_orgdes, pr_operador, pr_terminal ) 
values ( 275, @ente_mis_prueba, 'V', 17, 400.00, NULL, 400, 1, NULL, 400, 100, 1, NULL, NULL, NULL, 'tyronecruz', '5/1/2013 12:00:00.000 AM', 'P', 'COMPRA', NULL, 'tyronecruz', 1, 43, NULL, NULL, 1, '5/2/2013 12:00:00.000 AM', NULL, NULL, NULL, 1, 'SERVICIOS', '*', 'PC01GYECAP' )

/* CLIENTE EMPRESA */
insert into cob_sbancarios..sb_preautori ( pr_secuencial, pr_cliente, pr_tipo, pr_moneda, pr_monto_efec_c, pr_monto_otr_c, pr_cotiz_efe_c, pr_cotiz_otr_c, pr_monto_v, pr_cotiz_v, pr_monto_usd_v, pr_factor, pr_chq_ajeno1, pr_chq_ajeno2, pr_cred_cuenta, pr_func_creador, pr_fecha, pr_estado, pr_observaciones, pr_oper_proceso, pr_func_modif, pr_oficina, pr_cod_creador, pr_cod_autorizante, pr_hora_autorizacion, pr_temas_n, pr_fecha_temas_n, pr_cuenta_cobro, pr_cuenta_pago, pr_estado_tn, pr_orig_dest, pr_desc_orgdes, pr_operador, pr_terminal ) 
values ( 276, @ente_mis_prueba_empresa, 'C', 17, 400.00, NULL, 400, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, 'tyronecruz', '5/1/2013 12:00:00.000 AM', 'P', 'COMPRA', NULL, 'tyronecruz', 1, 43, NULL, NULL, 1, '5/2/2013 12:00:00.000 AM', NULL, NULL, NULL, 1, 'SERVICIOS', '*', 'PC01GYECAP' )

insert into cob_sbancarios..sb_preautori ( pr_secuencial, pr_cliente, pr_tipo, pr_moneda, pr_monto_efec_c, pr_monto_otr_c, pr_cotiz_efe_c, pr_cotiz_otr_c, pr_monto_v, pr_cotiz_v, pr_monto_usd_v, pr_factor, pr_chq_ajeno1, pr_chq_ajeno2, pr_cred_cuenta, pr_func_creador, pr_fecha, pr_estado, pr_observaciones, pr_oper_proceso, pr_func_modif, pr_oficina, pr_cod_creador, pr_cod_autorizante, pr_hora_autorizacion, pr_temas_n, pr_fecha_temas_n, pr_cuenta_cobro, pr_cuenta_pago, pr_estado_tn, pr_orig_dest, pr_desc_orgdes, pr_operador, pr_terminal ) 
values ( 277, @ente_mis_prueba_empresa, 'V', 17, 400.00, NULL, 400, 1, NULL, 400, 100, 1, NULL, NULL, NULL, 'tyronecruz', '5/1/2013 12:00:00.000 AM', 'P', 'COMPRA', NULL, 'tyronecruz', 1, 43, NULL, NULL, 1, '5/2/2013 12:00:00.000 AM', NULL, NULL, NULL, 1, 'SERVICIOS', '*', 'PC01GYECAP' )

/* ----------------------------------------------------------------------------------------------------- */
if not exists (select 1 from cob_cuentas..cc_ctacte where cc_ctacte = 977)
begin
	if not exists (select 1 from cob_cuentas..cc_ctacte where cc_cta_banco = '01202000052')
	begin
		INSERT INTO cob_cuentas..cc_ctacte (cc_ctacte,cc_cta_banco,cc_filial,cc_oficina,cc_oficial,cc_nombre,cc_fecha_aper,cc_cliente,cc_ced_ruc,cc_estado,cc_cliente_ec,cc_direccion_ec,cc_descripcion_ec,cc_tipo_dir,cc_agen_ec,cc_parroquia,cc_zona,cc_ciclo,cc_categoria,cc_creditos_mes,cc_debitos_mes,cc_creditos_hoy,cc_debitos_hoy,cc_disponible,cc_12h,cc_12h_dif,cc_24h,cc_48h,cc_remesas,cc_rem_hoy,cc_fecha_ult_mov,cc_fecha_ult_mov_int,cc_fecha_ult_upd,cc_fecha_prx_corte,cc_cred_24h,cc_cred_rem,cc_dias_sob,cc_dias_sob_cont,cc_certificados,cc_protestos,cc_prot_justificados,cc_prot_periodo_ant,cc_sobregiros,cc_anulados,cc_revocados,cc_bloqueos,cc_num_blqmonto,cc_suspensos,cc_condiciones,cc_num_chq_defectos,cc_producto,cc_tipo,cc_moneda,cc_default,cc_tipo_def,cc_rol_ente,cc_chequeras,cc_cheque_inicial,cc_tipo_promedio,cc_saldo_ult_corte,cc_fecha_ult_corte,cc_fecha_ult_capi,cc_saldo_ayer,cc_monto_blq,cc_promedio1,cc_promedio2,cc_promedio3,cc_promedio4,cc_promedio5,cc_promedio6,cc_personalizada,cc_prom_disponible,cc_contador_trx,cc_cta_funcionario,cc_tipocta,cc_saldo_interes,cc_num_cta_asoc,cc_prod_banc,cc_origen,cc_contador_firma,cc_fecha_prx_capita,cc_dep_ini,cc_telefono,cc_int_hoy,cc_tasa_hoy,cc_cliente1,cc_nombre1,cc_monto_conformado,cc_sec_conformacion,cc_conformacion_hoy,cc_sector,cc_monto_imp,cc_monto_consumos,cc_segmento,cc_ctitularidad,cc_promotor,cc_int_mes,cc_tipocta_super,cc_cta_traslado,cc_direccion_dv,cc_descripcion_dv,cc_tipodir_dv,cc_parroquia_dv,cc_zona_dv,cc_agen_dv,cc_cliente_dv,cc_contador_chqs_rechazados,cc_numsol,cc_declarado,cc_capitalizacion,cc_min_dispmes,cc_aplica_tasacorp,cc_monto_ult_capi,cc_monto_emb,cc_creditos2,cc_creditos3,cc_creditos4,cc_creditos5,cc_creditos6,cc_debitos2,cc_debitos3,cc_debitos4,cc_debitos5,cc_debitos6,cc_aut_chequera,cc_id_aut_chequera,cc_tipo_credito,cc_tipo_inversion,cc_actividad_destino,cc_patente,cc_lin_chequera1,cc_lin_chequera2,cc_permite_sldcero,cc_rem_ayer,cc_estado_cuenta,cc_nota_dc,cc_fideicomiso,cc_cobra_feci,cc_ejecutivo_venta,cc_cta_banco_mig,cc_dia_corte,cc_cta_banker) 
        	            VALUES(977,'01202000052',1,1,200,'GUERRERO DE LOURDES PAULA PAULA de CONTRERAS                    ','2012-12-10 00:00:00.0',13036,'160228984','A',0,0,'RETENCION EN CASA MATRIZ                                                                                                ','R',1,0,0,'3','A',193300.0000,642614.2300,193300.0000,642614.2300,100000.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,'2013-01-02 00:00:00.0','2013-01-02 00:00:00.0','2012-05-16 00:00:00.0','2012-12-31 00:00:00.0','N','N',0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,'R',0,0,'D','P',0,1,'M',0.0000,'2012-12-09 00:00:00.0','2012-12-09 00:00:00.0',100000000.0000,0.0000,-179179.9100,20000.0000,30000.0000,40000.0000,50000.0000,60000.0000,'N',-189179.9100,592,'N','P',0.0000,0,33,'4  ',0,'2013-05-01 00:00:00.0',0,'XXXXXX      ',0.0000,0.0,0,'                                                                ',0.0000,0,0.0000,0,0.0000,0.0000,'TR        ','S',0,0.0000,'1','0',0,'RETENCION EN DIRECCION GENERAL                                  ','R',0,0,0,0,0,371,'N','M',0.0000,'N',0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,'                              ','                    ','CON       ','COM       ','3402      ','                                        ','                                                ','                                                ','N',0.0000,NULL,NULL,NULL,'N','1001      ',NULL,NULL,NULL)
    end
end                
                    

/* ----------------------------------------------------------------------------------------------------- */


go
