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
 * Pass:Bb0000000000
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
        --TARJETA CREDITO PARA TRANSACCIONAR
        @acc_tar_number varchar(20),
        @acc_tar_type_product int,
        @acc_tar_type_currency int
        
        

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
        @ente_mis_prueba_empresa_A=137488,
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
        @acc_ctacte_type_product=3,
        @acc_ctacte_type_currency=0,
        --CTACTE DOBLE AUTORIZACION EMPRESA
        @acc_ctacte_number_double_auth_empresa='10410108275407019',
        @acc_ctacte_type_product_double_auth_empresa=3,
        @acc_ctacte_type_currency_double_auth_empresa=0,
        --CTACTE DOBLE AUTORIZACION EMPRESA
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
        @acc_dpf_type_currency2=17,
        --CAR
        @acc_car_number='10407740700943818',
        @acc_car_type_product=7,
        @acc_car_type_currency=0,
        --DESTINO PARA PRUEBAS DE TRANSFERENCIAS A TERCERO
        @des_acc_ctacte_number='10410108595405215',
        @des_acc_ctacte_type_product=3,
        @des_acc_ctacte_type_currency=0,
        --TARJETA CREDITO PARA TRANSACCIONAR
        @acc_tar_number ='5041960000000013',
        @acc_tar_type_product = 83,
        @acc_tar_type_currency =0

/*------------------------------------------------------------------------------------------------------------*/
/* GENERACION DE ENTES */
/*------------------------------------------------------------------------------------------------------------*/
delete from cob_bvirtual..bv_ente where en_ente in (@ente_prueba,@ente_prueba_empresa,@ente_prueba_grupo)

INSERT INTO cob_bvirtual..bv_ente ( en_ente, en_ente_mis, en_nombre, en_pnombre, en_papellido, en_sapellido, en_fecha_reg, en_fecha_mod, en_fecha_nac, en_ced_ruc, en_categoria, en_tipo, en_email, en_fax, en_sector, en_lenguaje, en_oficina, en_notificaciones, en_oficial, en_usuario, en_origen_ente, en_uso_convenio, en_autorizado, en_grupo, en_segmento, en_linea_negocio, en_apoderado_legal ) 
VALUES ( @ente_prueba, 13036, 'TestBvName1Nat', 'TestBvName2Nat', 'TestBvApe1Nat', 'TestBvApe1Nat', getdate(), getdate(), getdate(), '14-5434-32156', 'P', 'P', 'testBv@cobiscorp.com', NULL, NULL, 'ES-EC', 1, 'S', 200, 'lestevez', NULL, 'N', 'S', NULL, 'BBMASS', 'CMB', NULL )

INSERT INTO cob_bvirtual..bv_ente  ( en_ente, en_ente_mis, en_nombre, en_pnombre, en_papellido, en_sapellido, en_fecha_reg, en_fecha_mod, en_fecha_nac, en_ced_ruc, en_categoria, en_tipo, en_email, en_fax, en_sector, en_lenguaje, en_oficina, en_notificaciones, en_oficial, en_usuario, en_origen_ente, en_uso_convenio, en_autorizado, en_grupo, en_segmento, en_linea_negocio, en_apoderado_legal ) 
VALUES ( @ente_prueba_empresa, 137488, 'TestBvName1Emp', 'TestBvName2NatEmp', 'TestBvApe1Emp', 'TestBvApe1Emp', getdate(), getdate(), getdate(), '3-101-002749', 'C', 'C', NULL, NULL, NULL, 'EN-US', 1, 'S', 202, 'pclavijo', ' ', 'N', 'S', 41, 'BBMASS', 'CMB', NULL )

INSERT INTO cob_bvirtual..bv_ente  ( en_ente, en_ente_mis, en_nombre, en_pnombre, en_papellido, en_sapellido, en_fecha_reg, en_fecha_mod, en_fecha_nac, en_ced_ruc, en_categoria, en_tipo, en_email, en_fax, en_sector, en_lenguaje, en_oficina, en_notificaciones, en_oficial, en_usuario, en_origen_ente, en_uso_convenio, en_autorizado, en_grupo, en_segmento, en_linea_negocio, en_apoderado_legal ) 
VALUES ( @ente_prueba_grupo, 137488, 'Grupo TestBvName1', 'Grupo TestBvName2', 'Grupo TestBvApe1', 'TestBvApe1', getdate(), getdate(), getdate(), '3-101-002749', 'C', 'G', NULL, NULL, NULL, 'EN-US', 1, 'S', 202, 'pclavijo', ' ', 'N', 'S', 41, 'BBMASS', 'CMB', NULL )



select @ente_mis_prueba=en_ente_mis from cob_bvirtual..bv_ente where en_ente=@ente_prueba
select @ente_mis_prueba_empresa=en_ente_mis from cob_bvirtual..bv_ente where en_ente=@ente_prueba_empresa


/*------------------------------------------------------------------------------------------------------------*/
/* REGISTRO DE GRUPO */
/*------------------------------------------------------------------------------------------------------------*/
delete from cob_bvirtual..bv_grupo_economico where ge_grupo=41

insert into cob_bvirtual..bv_grupo_economico(ge_grupo,ge_nombre,ge_cod_empresa,ge_empresa,ge_fecha_reg,ge_fecha_mod,ge_estado,ge_cedruc)
values(41,'GRUPO TEST',137488,'GRUPO TestBvName1Emp',getdate(),null,'V','3-101-002749')

delete from cob_bvirtual..bv_cliente_grupo where cg_grupo=41

insert into cob_bvirtual..bv_cliente_grupo(cg_grupo,cg_cod_cliente,cg_cliente,cg_fecha_reg,cg_fecha_mod,cg_estado,cg_autorizado)
values(41,137488,'TestBvName1Emp',getdate(),getdate(),'V','S')
insert into cob_bvirtual..bv_cliente_grupo(cg_grupo,cg_cod_cliente,cg_cliente,cg_fecha_reg,cg_fecha_mod,cg_estado,cg_autorizado)
values(41,13036,'TestBvName1Nat',getdate(),getdate(),'V','S')

delete from cob_bvirtual..bv_login_empresa where le_login in (@login_grupo)

insert into cob_bvirtual..bv_login_empresa(le_login,le_servicio,le_grupo,le_empresa,le_default,le_estado,le_fecha_reg,le_fecha_mod)
values(@login_grupo,@service_id,41,137488,'N','V',getdate(),getdate())

insert into cob_bvirtual..bv_login_empresa(le_login,le_servicio,le_grupo,le_empresa,le_default,le_estado,le_fecha_reg,le_fecha_mod)
values(@login_grupo,@service_id,41,13036,'N','V',getdate(),getdate())

/*------------------------------------------------------------------------------------------------------------*/
/* DESTINOS */
/*------------------------------------------------------------------------------------------------------------*/
/* ELIMINACION DE DESTINOS NATURAL Y JURIDICO */
delete from cob_bvirtual..bv_lote_destino where ld_secuencial in (select de_lote from cob_bvirtual..bv_destino where de_ente in(@ente_prueba_empresa,@ente_prueba)  and de_cuenta_destino in(@des_acc_ctacte_number,@acc_ctaaho_number,@acc_ctaaho_number_double_auth_empresa ))  
delete from cob_bvirtual..bv_destino where de_ente in(@ente_prueba_empresa,@ente_prueba) and de_cuenta_destino in(@des_acc_ctacte_number,@acc_ctaaho_number,@acc_ctaaho_number_double_auth_empresa )

/* CREACION DEL DESTINO PARA USUARIO NATURAL */
/* DESTINO PARA TRANSACCIONAR */
insert into cob_bvirtual..bv_destino(de_ente,de_servicio,de_cuenta,de_producto,de_moneda,de_cuenta_destino,de_producto_destino,de_moneda_destino,de_estado,de_tipo,de_id_beneficiario,de_beneficiario,de_fecha_reg,de_email1,de_email2,de_email3,de_lote,de_cta_banco_mig)
values(@ente_prueba,@service_id,@acc_ctacte_number,@acc_ctacte_type_product,@acc_ctaaho_type_currency_double_auth_empresa,@des_acc_ctacte_number,@des_acc_ctacte_type_product,@des_acc_ctacte_type_currency,'A','I','123456789','JUAN PEDRO PEREZ CRUZ',getdate(),null,null,null,0,null)

/* DESTINO PARA FUNCIONALIDAD DE ADMINISTRACION */
insert into cob_bvirtual..bv_destino ( de_ente, de_servicio, de_cuenta, de_producto, de_moneda, de_cuenta_destino, de_producto_destino, de_moneda_destino, de_estado, de_tipo, de_id_beneficiario, de_beneficiario, de_fecha_reg, de_email1, de_email2, de_email3, de_lote, de_cta_banco_mig )
values ( @ente_prueba, 1, @acc_ctaaho_number_double_auth_empresa, @acc_ctaaho_type_product_double_auth_empresa, @acc_ctacte_type_currency, @acc_ctaaho_number, @acc_ctaaho_type_product, @acc_ctaaho_type_currency, 'A', 'I', @login_identificaion, @login, getdate(), 'test@cobiscorp.com', NULL,NULL, 0, '' )

/* CREACION DEL DESTINO PARA USUARIO EMPRESA */
/* DESTINO PARA TRANSACCIONAR */
insert into cob_bvirtual..bv_destino(de_ente,de_servicio,de_cuenta,de_producto,de_moneda,de_cuenta_destino,de_producto_destino,de_moneda_destino,de_estado,de_tipo,de_id_beneficiario,de_beneficiario,de_fecha_reg,de_email1,de_email2,de_email3,de_lote,de_cta_banco_mig)
values(@ente_prueba_empresa,@service_id,@acc_ctacte_number,@acc_ctacte_type_product,@acc_ctacte_type_currency,@des_acc_ctacte_number,@des_acc_ctacte_type_product,@des_acc_ctacte_type_currency,'A','I','123456789','JUAN PEDRO PEREZ CRUZ',getdate(),null,null,null,0,null)

/* ELIMINACION DE DATA PREVIA GENERADA */
delete from cob_bvirtual..bv_lote_destino where ld_secuencial in (select de_lote from cob_bvirtual..bv_destino where de_cuenta_destino like '1520100101717588%')
delete from cob_bvirtual..bv_lote_destino where ld_secuencial in (select de_lote from cob_bvirtual..bv_destino where de_cuenta=@acc_ctacte_number)
delete from cob_bvirtual..bv_destino where de_cuenta_destino like '1520100101717588%'
delete from cob_bvirtual..bv_destino where de_cuenta=@acc_ctacte_number
/*------------------------------------------------------------------------------------------------------------*/


/*------------------------------------------------------------------------------------------------------------*/
/* COMBINACION DE CONDICIONES EMPRESA */
/*------------------------------------------------------------------------------------------------------------*/
delete from cob_bvirtual..bv_atributo_combinacion where ac_codigo=21
INSERT INTO cob_bvirtual..bv_atributo_combinacion ( ac_codigo, ac_codigo_comb, ac_tipo_login, ac_numero, ac_fecha_reg, ac_fecha_mod ) 
VALUES ( 21, 17, 'A', 1, '10/25/2013 12:00:00.000 AM', '10/25/2013 12:00:00.000 AM' )

delete from cob_bvirtual..bv_combinacion_condicion where cc_codigo=17
INSERT INTO cob_bvirtual..bv_combinacion_condicion ( cc_codigo, cc_condicion, cc_descripcion, cc_fecha_reg, cc_fecha_mod ) 
VALUES ( 17, 23, 'TEST COMBINACION', '10/25/2013 12:00:00.000 AM', '10/25/2013 12:00:00.000 AM' )

delete from cob_bvirtual..bv_condicion where co_id=23
INSERT INTO cob_bvirtual..bv_condicion ( co_id, co_ente, co_canal, co_moneda, co_transaccion, co_minimo, co_maximo, co_tipo_vigencia, co_fecha_desde, co_fecha_hasta, co_tipo_ejecucion, co_estado, co_fecha_reg, co_fecha_mod, co_autorizado, co_producto, co_cuenta, co_grupo, co_empresa ) 
VALUES ( 23, @ente_prueba_empresa, @service_id, @acc_ctaaho_type_currency, 1800009, 1.0000, 10000.0000, 'I', NULL, NULL, 'S', 'V', '10/25/2013 12:00:00.000 AM', '10/25/2013 12:00:00.000 AM', 'S', @acc_ctaaho_type_product_double_auth_empresa, @acc_ctaaho_number_double_auth_empresa, NULL, NULL )
/*------------------------------------------------------------------------------------------------------------*/

if not exists(select 1 from cob_bvirtual..bv_perfil_transaccion where pt_transaccion=18000 and pt_perfil=24)
insert into cob_bvirtual..bv_perfil_transaccion values (24, 18,18000,'V')

/* ----------------------------------------------------------------------------------------------------- */
/* Actualizacion de Fondos Disponibles para las todas las cuentas */
update cob_ahorros..ah_cuenta set ah_disponible=1000000 where ah_cta_banco in (@acc_ctaaho_number_double_auth_empresa,@acc_ctaaho_number)
update cob_cuentas..cc_ctacte set cc_disponible=100000 where cc_cta_banco in (@acc_ctacte_number_double_auth_empresa,@acc_ctacte_number)
/* ----------------------------------------------------------------------------------------------------- */


/*------------------------------------------------------------------------------------------------------------*/
/* PUBLICIDAD */
/*------------------------------------------------------------------------------------------------------------*/
delete from cob_bvirtual..bv_sector_servicio_preferencia where ss_sector in ('P','G') and ss_segmento='BBMASS' and ss_linea_negocio='CMB'
insert into cob_bvirtual..bv_sector_servicio_preferencia (ss_sector,ss_servicio,ss_preferencia,ss_estado,ss_segmento,ss_linea_negocio)
values ('P',1,3,'V','BBMASS','CMB')
insert into cob_bvirtual..bv_sector_servicio_preferencia (ss_sector,ss_servicio,ss_preferencia,ss_estado,ss_segmento,ss_linea_negocio)
values ('G',1,3,'V','BBMASS','CMB')

delete from cob_bvirtual..bv_publicidad where pu_preferencia=3 and pu_tipo=4
insert into cob_bvirtual..bv_publicidad(pu_preferencia,pu_publicidad,pu_nombre,pu_path,pu_tipo,pu_estado)
values (3,9,'VIVIENDA','PUBLICIDAD-VIVIENDA.JPG',4,'V')
/*------------------------------------------------------------------------------------------------------------*/



/*------------------------------------------------------------------------------------------------------------*/
/* PARAMETRIA PARA COTIZACION, COMPRA Y VENTA DE DIVISAS                                                      */
/*------------------------------------------------------------------------------------------------------------*/
if not exists(select pa_tinyint   from cobis..cl_parametro  where pa_producto = 'BVI' and   pa_nemonico = 'CODML')
insert into cobis..cl_parametro 
( pa_tinyint, pa_nemonico, pa_tipo, pa_char, pa_producto,pa_parametro) 
values( 0 , 'CODML', 'C', '8', 'BVI', 'MONEDA COLONES')

if not exists(select w_mon_ext = pa_tinyint  from cobis..cl_parametro where pa_producto = 'BVI' and   pa_nemonico = 'CODUSD' )
insert into cobis..cl_parametro 
( pa_tinyint, pa_nemonico, pa_tipo, pa_char, pa_producto,pa_parametro) 
values( 17 , 'CODUSD', 'C', '8', 'BVI','MONEDA DOLARES')

if not exists (select w_num_decimales = pa_tinyint from cobis..cl_parametro where pa_producto = 'BVI' and   pa_nemonico = 'DECMON' )
insert into cobis..cl_parametro 
( pa_tinyint, pa_nemonico, pa_tipo, pa_char, pa_producto,pa_parametro) 
values( 2 , 'DECMON', 'C', '8', 'BVI','NUMERO DECIMALES')

if not exists(select w_int_tesoreria = pa_char  from cobis..cl_parametro  where pa_producto = 'BVI'and   pa_nemonico = 'TESD')
insert into cobis..cl_parametro 
( pa_char, pa_nemonico, pa_tipo, pa_producto,pa_parametro) 
values( 'S' , 'TESD', 'C',  'BVI','TESORERIA')

delete from cob_cuentas..cc_ctacte where cc_cta_banco in (@acc_ctacte_number_double_auth_empresa)
insert into cob_cuentas..cc_ctacte(cc_filial,cc_oficina,cc_moneda,cc_cta_banco,cc_nombre,cc_cliente,cc_ced_ruc,cc_estado,cc_categoria,cc_tipo_promedio,cc_prod_bancario,cc_oficial,cc_fecha_aper,cc_fecha_ult_mov,cc_disponible,cc_12h,cc_24h,cc_48h,cc_remesas,cc_rem_hoy,cc_saldo_girar,cc_saldo_ayer,cc_monto_blq,cc_creditos_hoy,cc_creditos_mes,cc_debitos_hoy,cc_debitos_mes,cc_prom_disponible,cc_promedio1,cc_promedio2,cc_promedio3,cc_promedio4,cc_promedio5,cc_promedio6,cc_contador_trx,cc_dep_ini,cc_bloqueos,cc_num_blqmonto,cc_protestos,cc_prot_justificados,cc_prot_periodo_ant,cc_anulados,cc_revocados,cc_chequeras,cc_cheque_inicial,cc_sobregiros,cc_cta_funcionario,cc_estado_ejecucion,cc_monto_sob,cc_ant_dis_host,cc_ant_12h_host,cc_ant_24h_host,cc_ant_rem_host,cc_ant_sob_host,cc_ant_blq_host,cc_fecult_host,cc_fecha_prx_corte,cc_bloqueo,cc_ctitularidad)
values(1,1,@acc_ctacte_type_currency_double_auth_empresa,@acc_ctacte_number_double_auth_empresa,'CTA PRUEBA EMPRESA A',@ente_mis_prueba_empresa_A,3155674520 ,'A','N','M',6,800,getdate(),getdate(),10096139,0,0,0,0,0,0,0,60742,184402,184402,88263,88263,81754.56,81754.56,0,0,0,0,0,9,0,1,1,0,0,0,0,0,0,1,0,'N','N',0,10096139,0,0,0,0,60742,getdate(),null,'N','S')

delete from cob_ahorros..ah_cuenta where ah_cta_banco in (@acc_ctaaho_number_double_auth_empresa)
insert into cob_ahorros..ah_cuenta(ah_filial,ah_oficina,ah_moneda,ah_cta_banco,ah_nombre,ah_cliente,ah_ced_ruc,ah_estado,ah_categoria,ah_tipo_promedio,ah_capitalizacion,ah_tinteres,ah_prod_bancario,ah_oficial,ah_fecha_aper,ah_fecha_ult_mov,ah_disponible,ah_12h,ah_24h,ah_48h,ah_remesas,ah_rem_hoy,ah_saldo_girar,ah_saldo_ayer,ah_saldo_interes,ah_monto_blq,ah_creditos_hoy,ah_creditos_mes,ah_debitos_hoy,ah_debitos_mes,ah_prom_disponible,ah_promedio1,ah_promedio2,ah_promedio3,ah_promedio4,ah_promedio5,ah_promedio6,ah_contador_trx,ah_dep_ini,ah_bloqueos,ah_num_blqmonto,ah_linea,ah_num_lib,ah_cta_funcionario,ah_estado_ejecucion,ah_ant_dis_host,ah_ant_12h_host,ah_ant_24h_host,ah_ant_rem_host,ah_ant_blq_host,ah_fecult_host,ah_fecha_prx_corte,ah_bloqueo,ah_ctitularidad)
values(1,1,@acc_ctaaho_type_currency_double_auth_empresa,@acc_ctaaho_number_double_auth_empresa,'CTA PRUEBA EMPRESA A',@ente_mis_prueba_empresa_A,0108220657,'A','E','M','M','N',1,200,getdate(),getdate(),40543,0,0,0,0,0,0,0,0,0,37900,0,2,0,28118.45,28118.45,0,0,0,0,0,3,0,1,0,0,0,'N','N',2616997.4,0,0,0,0,getdate(),getdate(),'N','S')


/*------------------------------------------------------------------------------------------------------------*/
/* PARAMETRIA PARA NOTIFICACIONES                                                                             */
/*------------------------------------------------------------------------------------------------------------*/

declare @w_sec int
delete cob_bvirtual..bv_notificaciones_despacho where nd_ente= 277 and  nd_num_dir='testCts' and nd_var4='PRUEBA DE REGRESION'      
exec cobis..sp_cseqnos 
@i_tabla = 'bv_notificaciones_despacho', 
@o_siguiente = @w_sec out 
    	  
insert  cob_bvirtual..bv_notificaciones_despacho
(nd_id,nd_servicio,nd_ente,nd_tipo,nd_tipo_mensaje,nd_prioridad,nd_num_dir,nd_estado,
nd_num_err,nd_txt_err,nd_ret_status,nd_fecha_reg,nd_fecha_mod,nd_fecha_auto,nd_var1,nd_var2,
nd_var3,nd_var4,nd_var5,nd_var6,nd_producto,nd_subproducto)
values(@w_sec,	1,	277,'I','N',		1,	'testCts',	'N',
0,		'',	0,		getdate(),getdate(), getdate(),'infocfa@cfa.com.co',	'PRUEBA DE REGRESION', 
'BVI','PRUEBA DE REGRESION',	'prueba de regresion',null,null,	null)
	

delete cob_bvirtual..bv_notificaciones_despacho where nd_ente= 279 and  nd_num_dir='testCtsEmp' and nd_var4='PRUEBA DE REGRESION'
exec cobis..sp_cseqnos 
@i_tabla = 'bv_notificaciones_despacho', 
@o_siguiente = @w_sec out 
    	  
insert  cob_bvirtual..bv_notificaciones_despacho
(nd_id,nd_servicio,nd_ente,nd_tipo,nd_tipo_mensaje,nd_prioridad,nd_num_dir,nd_estado,
nd_num_err,nd_txt_err,nd_ret_status,nd_fecha_reg,nd_fecha_mod,nd_fecha_auto,nd_var1,nd_var2,
nd_var3,nd_var4,nd_var5,nd_var6,nd_producto,nd_subproducto)
values(@w_sec,	1,	279,'I','N',		1,	'testCtsEmp',	'N',
0,		'',	0,		getdate(),getdate(), getdate(),'infocfa@cfa.com.co',	'PRUEBA DE REGRESION', 
'BVI','PRUEBA DE REGRESION',	'prueba de regresion',null,null,	null)


delete cob_bvirtual..bv_notificaciones_despacho where nd_ente= 275 and  nd_num_dir='testCtsGrupo' and nd_var4='PRUEBA DE REGRESION'
exec cobis..sp_cseqnos 
@i_tabla = 'bv_notificaciones_despacho', 
@o_siguiente = @w_sec out 

insert  cob_bvirtual..bv_notificaciones_despacho
(nd_id,nd_servicio,nd_ente,nd_tipo,nd_tipo_mensaje,nd_prioridad,nd_num_dir,nd_estado,
nd_num_err,nd_txt_err,nd_ret_status,nd_fecha_reg,nd_fecha_mod,nd_fecha_auto,nd_var1,nd_var2,
nd_var3,nd_var4,nd_var5,nd_var6,nd_producto,nd_subproducto)
values(@w_sec,	1,	295,'I','N',		1,	'testCtsGrupo',	'N',
0,		'',	0,		getdate(),getdate(), getdate(),'infocfa@cfa.com.co',	'PRUEBA DE REGRESION', 
'BVI','PRUEBA DE REGRESION',	'prueba de regresion',null,null,	null)
	

/*------------------------------------------------------------------------------------------------------------*/
/* PARAMETRIA PARA AUTORIZACIONES  COMPANY                                                                    */
/*------------------------------------------------------------------------------------------------------------*/
declare @w_seq_autorizador  int, @w_seq_autorizaciones int, @w_seq_lpendiente  int, @w_seq_autorizadorOld int 
/*Se elimina parametria*/
select @w_seq_autorizadorOld= au_id   from cob_bvirtual..bv_autorizador where au_ssn_branch = 123456
delete cob_bvirtual..bv_autorizaciones_realizadas where ar_au_id= @w_seq_autorizadorOld
delete cob_bvirtual..bv_autorizador where au_login='testCtsEmp' and au_ssn_branch = 123456 and au_id=@w_seq_autorizadorOld
delete cob_bvirtual..bv_login_pendiente where lp_au_id= @w_seq_autorizadorOld and lp_login= 'testCtsEmp'

/*Se crea parametria*/
EXEC cobis..sp_cseqnos
@i_tabla  = 'bv_autorizador', @o_siguiente  = @w_seq_autorizador OUT
INSERT INTO cob_bvirtual..bv_autorizador
(au_id, au_login, au_condicion, au_estado, au_fecha_inicio, au_trn_ejecutada,au_terminada, au_ssn_branch, au_condicion_firmas)
VALUES
(@w_seq_autorizador, 'testCtsEmp', NULL,'P', getdate(), '1875009','N', '123456', null)



EXEC cobis..sp_cseqnos
@i_tabla = 'bv_autorizaciones_realizadas', @o_siguiente = @w_seq_autorizaciones OUT
INSERT INTO cob_bvirtual..bv_autorizaciones_realizadas
(ar_id, ar_au_id, ar_tipo_login, ar_numero)
VALUES--(@w_seq_autorizaciones, @w_seq_autorizador, (SELECT lo_tipo_autorizacion FROM bv_login WHERE lo_login = 'testCtsEmp' AND lo_servicio IN (1, 0))   , 1)
(@w_seq_autorizaciones, @w_seq_autorizador, 'D'   , 1)



EXEC cobis..sp_cseqnos
@i_tabla = 'bv_login_pendiente', @o_siguiente = @w_seq_lpendiente out
INSERT INTO cob_bvirtual..bv_login_pendiente
( lp_id, lp_au_id, lp_login, lp_motivo, lp_estado, lp_fecha_res)
VALUES--( @w_seq_lpendiente, @w_seq_autorizador, 'testCtsEmp',(SELECT re_valor FROM cobis..ad_etiqueta_i18n WHERE  pc_identificador= 'b-cob_bvirtual' AND pc_codigo= 'CREACION' AND re_cultura = 'ES-EC'),    'A', getdate())
( @w_seq_lpendiente, @w_seq_autorizador, 'testCtsEmp','Creacion',    'P', getdate() )

/*------------------------------------------------------------------------------------------------------------*/
/* PARAMETRIA PARA AUTORIZACIONES  GRUPO                                                                    */
/*------------------------------------------------------------------------------------------------------------*/

/*Se elimina parametria*/
select @w_seq_autorizadorOld= au_id   from cob_bvirtual..bv_autorizador where au_ssn_branch = 12345678
delete cob_bvirtual..bv_autorizaciones_realizadas where ar_au_id= @w_seq_autorizadorOld
delete cob_bvirtual..bv_autorizador where au_login='testCtsGrupo' and au_ssn_branch = 12345678 and au_id=@w_seq_autorizadorOld
delete cob_bvirtual..bv_login_pendiente where lp_au_id= @w_seq_autorizadorOld and lp_login= 'testCtsGrupo'

/*Se crea parametria*/
EXEC cobis..sp_cseqnos
@i_tabla  = 'bv_autorizador', @o_siguiente  = @w_seq_autorizador OUT
INSERT INTO cob_bvirtual..bv_autorizador
(au_id, au_login, au_condicion, au_estado, au_fecha_inicio, au_trn_ejecutada,au_terminada, au_ssn_branch, au_condicion_firmas)
VALUES
(@w_seq_autorizador, 'testCtsGrupo', NULL,'P', getdate(), '1875009','N', '12345678', null)


EXEC cobis..sp_cseqnos
@i_tabla = 'bv_autorizaciones_realizadas', @o_siguiente = @w_seq_autorizaciones OUT
INSERT INTO cob_bvirtual..bv_autorizaciones_realizadas
(ar_id, ar_au_id, ar_tipo_login, ar_numero)
VALUES--(@w_seq_autorizaciones, @w_seq_autorizador, (SELECT lo_tipo_autorizacion FROM bv_login WHERE lo_login = 'testCtsGrupo' AND lo_servicio IN (1, 0))   , 1)
(@w_seq_autorizaciones, @w_seq_autorizador, 'D'   , 1)


EXEC cobis..sp_cseqnos
@i_tabla = 'bv_login_pendiente', @o_siguiente = @w_seq_lpendiente out
INSERT INTO cob_bvirtual..bv_login_pendiente
( lp_id, lp_au_id, lp_login, lp_motivo, lp_estado, lp_fecha_res)
VALUES
( @w_seq_lpendiente, @w_seq_autorizador, 'testCtsGrupo','Creacion',    'P', getdate() )


/*------------------------------------------------------------------------------------------------------------*/
/* PARAMETRIA PARA INTERNACIONALES                                                                            */
/*------------------------------------------------------------------------------------------------------------*/
delete from cob_bvirtual..bv_plantilla where pl_secuencial=8474244
INSERT INTO dbo.bv_plantilla ( pl_mail_1, pl_mail_2, pl_cta_credito, pl_tipo_ben, pl_beneficiario, pl_p_apellido, pl_s_apellido, pl_direccion_ben, pl_id_banco_ben, pl_id_direccion_banco_ben, pl_id_pais_banco_ben, pl_id_direccion_banco_int, pl_id_pais_banco_int, pl_id_banco_int, pl_secuencial, pl_login, pl_nombre_plt, pl_fecha_reg, pl_fecha_mod, pl_razon_social, pl_dni_ruc, pl_swif_aba, pl_swif_aba_int, pl_nombre_banco_ben, pl_id_pais_ben, pl_id_ciudad_ben ) 
VALUES ( 'pruebatestCts@cobiscorp.com', NULL, '1721086088', '1.1', 'PRUEBA', 'TEST', 'PRUEBAT', 'CUMBAYA', '3357', 12, 218, 1, 218, '3348', 8474244, @login, 'PLANTILLA PRUEBA', '4/24/2013 4:10:05.140 PM', '4/24/2013 4:10:05.140 PM', NULL, '1721086088', 'PICHECEQXXX', 'PICHECEQXXX', 'BANCO PRUEBA', NULL, NULL )
	
go
