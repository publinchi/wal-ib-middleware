--this script mus be executed before all the tests
--insert new product to test cts
insert into cobis..cl_pro_moneda (
pm_producto, pm_tipo, pm_moneda, pm_descripcion,
pm_fecha_aper,pm_estado
) values(80,'R',0,'PRUEBAS CTS/CIS/IEN','01/20/2013','V'         
)


