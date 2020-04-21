use cobis
go

insert into cl_ttransaccion values (80000, 'test1', 'ROCSTFDBG', 'test1')
insert into ad_procedure values (80000, 'sp_test1', 'cobis','V', '2013-05-30', 'test1.sp')
insert into ad_pro_transaccion (pt_producto, pt_tipo, pt_moneda, pt_transaccion, pt_estado, pt_fecha_ult_mod, pt_procedure) values (80, 'R', 0, 80000, 'V', '2013-05-30',80000)
insert into ad_tr_autorizada values (80, 'R', 0, 80000, 1,getdate(), 1, 'V', getdate())


