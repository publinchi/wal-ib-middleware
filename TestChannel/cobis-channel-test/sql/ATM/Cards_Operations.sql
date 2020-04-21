/*
 * ta_principal = 0 principal
 * ta_principal <> 0 adicional
 * 
 * Etapas del Estado de Tarjeta
 * T
 * E
 * A
*/
declare @principal_card int,
@aditional_card int

select
@principal_card=41,
@aditional_card=44

--tipo prod de tarhetas
select * from cob_atm..tm_emision where em_tarjeta in(@principal_card,@aditional_card)
--reseteo del estado de las tarjetas
update cob_atm..tm_tarjeta  set ta_estado_tarjeta="E" where ta_tarjeta in(@principal_card,@aditional_card)
--eliminacion de registros de cupos previos
delete from cob_atm..tm_cupos where cu_tarjeta in (select ta_codigo from cob_atm..tm_tarjeta where ta_tarjeta in (@principal_card,@aditional_card))