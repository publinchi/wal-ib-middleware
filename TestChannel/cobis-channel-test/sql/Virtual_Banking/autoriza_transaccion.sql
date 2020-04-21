use cob_bvirtual
go

  if not exists (select * from bv_perfil_transaccion where pt_transaccion = 2000000 and pt_perfil = 1)
	begin
	print 'Autorizacion transaccion 2000000'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (1,18,2000000,'V')
	end
else
	begin
	   print 'transaccion 2000000 ya autorizada para perfil 1'
	end
GO

if not exists (select * from bv_perfil_transaccion where pt_transaccion = 1800022 and pt_perfil = 24)
	begin
	print 'Autorizacion transaccion 1800022'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (24,18,1800022,'V')
	end
else
	begin
	   print 'transaccion 1800022 ya autorizada para perfil 24'
	end
GO

if not exists (select * from bv_perfil_transaccion where pt_transaccion = 1800197 and pt_perfil = 27)
	begin
	print 'Autorizacion transaccion 1800197'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (27,18,1800197,'V')
	end
else
	begin
	   print 'transaccion 1800120 ya autorizada para perfil 4'
	end
GO


if not exists (select * from bv_perfil_transaccion where pt_transaccion = 1800022 and pt_perfil = 24)
	begin
	print 'Autorizacion transaccion 1800120'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (24,18,1800120,'V')
	end
else
	begin
	   print 'transaccion 1800120 ya autorizada para perfil 4'
	end
GO

if not exists (select * from bv_perfil_transaccion where pt_transaccion = 14158 and pt_perfil = 29)
	begin
	print 'Autorizacion transaccion 14158'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (29,18,14158,'V')
	end
else
	begin
	   print 'transaccion 14158 ya autorizada para perfil 29'
	end
GO

if not exists (select * from bv_perfil_transaccion where pt_transaccion = 14452 and pt_perfil = 28)
	begin
	print 'Autorizacion transaccion 14452'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (28,18,14452,'V')
	end
else
	begin
	   print 'transaccion 14452 ya autorizada para perfil 28'
	end
GO

if not exists (select * from bv_perfil_transaccion where pt_transaccion = 14805 and pt_perfil = 28)
	begin
	print 'Autorizacion transaccion 14805'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (28,18,14805,'V')
	end
else
	begin
	   print 'transaccion 14805 ya autorizada para perfil 28'
	end
GO

if not exists (select * from bv_perfil_transaccion where pt_transaccion = 14158 and pt_perfil = 28)
	begin
	print 'Autorizacion transaccion 14158'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (28,18,14158,'V')
	end
else
	begin
	   print 'transaccion 14158 ya autorizada para perfil 28'
	end
GO

if not exists (select * from bv_perfil_transaccion where pt_transaccion = 14463 and pt_perfil = 28)
	begin
	print 'Autorizacion transaccion 14463'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (28,18,14463,'V')
	end
else
	begin
	   print 'transaccion 14463 ya autorizada para perfil 28'
	end
GO
if not exists (select * from bv_perfil_transaccion where pt_transaccion = 18799 and pt_perfil = 22)
	begin
	print 'Autorizacion transaccion 18799'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (22,4,18799,'V')
	end
else
	begin
	   print 'transaccion 18799 ya autorizada para perfil 22 persona natural'
	end
GO
if not exists (select * from bv_perfil_transaccion where pt_transaccion = 18799 and pt_perfil = 24)
	begin
	print 'Autorizacion transaccion 18799'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (24,4,18799,'V')
	end
else
	begin
	   print 'transaccion 18799 ya autorizada para perfil 24 persona juridica'
	end
GO
if not exists (select * from bv_perfil_transaccion where pt_transaccion = 18799 and pt_perfil = 29)
	begin
	print 'Autorizacion transaccion 18799'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (29,4,18799,'V')
	end
else
	begin
	   print 'transaccion 18799 ya autorizada para perfil 29 grupo'
	end
GO
if not exists (select * from bv_perfil_transaccion where pt_transaccion = 18159 and pt_perfil = 22)
	begin
	print 'Autorizacion transaccion 18159'
	INSERT INTO bv_perfil_transaccion (pt_perfil, pt_producto, pt_transaccion, pt_estado) values (22,18,18159,'V')
	end
else
	begin
	   print '18159  ya autorizada para perfil 22 grupo'
	end
GO