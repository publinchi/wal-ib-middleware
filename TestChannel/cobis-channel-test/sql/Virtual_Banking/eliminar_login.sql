use cob_bvirtual
go

--delete bv_login where lo_login = 'testCTS'
--delete bv_ente_servicio_perfil where es_login = 'testCTS'

delete cob_bvirtual..bv_in_login where il_login in ('testCts','testCtsEmp','testCtsEmpA', 'testCtsGrupo')

go