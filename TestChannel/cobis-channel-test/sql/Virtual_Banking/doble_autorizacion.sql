/*
 * Wendy Sánchez 
 */


use cob_bvirtual
go


Declare 
	@ente_prueba_empresa 		int,
	@ente_prueba_grupo   		int,
	@w_secuencial_condicion1 	int,
	@w_secuencial_condicion2    int,
	@w_secuencial_combinacion1	int,
	@w_secuencial_combinacion2	int,
	@w_secuencial_atributo1		int,
	@w_secuencial_atributo2		int,
	@number_transaction         int,
	@cont                       int

--ENTE EMPRESA
        set @ente_prueba_empresa = 279
        set @ente_prueba_grupo =  295
        set @cont = 1
        
		
		/* Eliminacion de registros anteriores */
		delete from cob_bvirtual..bv_atributo_combinacion 
		where ac_codigo_comb in (Select cc_codigo
						from cob_bvirtual..bv_condicion a 
						inner join cob_bvirtual..bv_combinacion_condicion b on a.co_id = b.cc_condicion
						where a.co_ente in( @ente_prueba_empresa, @ente_prueba_grupo))

		delete from cob_bvirtual..bv_combinacion_condicion 
		where cc_condicion in (Select co_id from cob_bvirtual..bv_condicion where co_ente in( @ente_prueba_empresa, @ente_prueba_grupo))
        
		delete from cob_bvirtual..bv_condicion where co_ente in( @ente_prueba_empresa, @ente_prueba_grupo)
		
		
		
        /*********************  Registro de la condición para Empresa  ********************/
        
        --delete from cob_bvirtual..bv_condicion where co_ente = @ente_prueba_empresa
		
		while(@cont < 5)
		begin
			if(@cont = 1)
		    	select @number_transaction = 18056
	
            if(@cont = 2)
		    	select @number_transaction = 18059

            if(@cont = 3)
		    	select @number_transaction = 18057
		    
			if(@cont = 4)
		    	select @number_transaction = 18862
		
		
			exec cobis..sp_cseqnos 
			@i_tabla = 'bv_condicion', 
			@o_siguiente = @w_secuencial_condicion1 out
			
			
			insert into cob_bvirtual..bv_condicion 
					(co_id,        		co_ente,     			co_canal, 	 		co_moneda, 			co_transaccion,
					co_minimo,    		co_maximo,    			co_tipo_vigencia,	co_fecha_desde,     co_fecha_hasta,
					co_tipo_ejecucion,	co_estado,				co_fecha_reg,       co_fecha_mod,       co_autorizado,
					co_producto,		co_cuenta,      		co_grupo,		    co_empresa  )
			values 
				(@w_secuencial_condicion1, @ente_prueba_empresa,   1,			0,					@number_transaction,
				0.00,				99999999999999,			'I',				null,				null,
				'S',				'V',					getdate(),			getdate(),			'S',
				null, 				null, 					null,				null )
			
			
			
			/*Registro de Conbinación condición para Empresa*/
				
			exec cobis..sp_cseqnos 
			@i_tabla = 'bv_combinacion_condicion', 
			@o_siguiente = @w_secuencial_combinacion1 out
			
			insert into cob_bvirtual..bv_combinacion_condicion
					(cc_codigo, cc_condicion, cc_descripcion, cc_fecha_reg, cc_fecha_mod )
			values  (@w_secuencial_combinacion1,	@w_secuencial_condicion1,	 'Test Autorizacion Empresa' ,	getdate(), getdate())
			
			
			/*Registro de la combinacion requerida para la autorización Empresa*/		
			
			--Registros para la primera combinación de Empresa
			exec cobis..sp_cseqnos 
			@i_tabla = 'bv_atributo_combinacion', 
			@o_siguiente = @w_secuencial_atributo1 out
			
			insert into cob_bvirtual..bv_atributo_combinacion 
				   (ac_codigo,   ac_codigo_comb, ac_tipo_login, ac_numero, ac_fecha_reg, ac_fecha_mod)
			values (@w_secuencial_atributo1,  @w_secuencial_combinacion1,  'A',  1,	getdate(), getdate())
			
			exec cobis..sp_cseqnos 
			@i_tabla = 'bv_atributo_combinacion', 
			@o_siguiente = @w_secuencial_atributo2 out
			
			insert into cob_bvirtual..bv_atributo_combinacion 
					(ac_codigo,   ac_codigo_comb, ac_tipo_login, ac_numero, ac_fecha_reg, ac_fecha_mod)
			values	(@w_secuencial_atributo2, @w_secuencial_combinacion1,  'D',  1,	getdate(), getdate())
		
		    select @cont = @cont + 1	
		
		end

        select @cont = 1 
		
		/*******************  Registro de la condición para Grupo  *******************/
       while(@cont < 5)
		begin
			if(@cont = 1)
		    	select @number_transaction = 18056
	
            if(@cont = 2)
		    	select @number_transaction = 18059

            if(@cont = 3)
		    	select @number_transaction = 18057
		    
			if(@cont = 4)
		    	select @number_transaction = 18862

        	exec cobis..sp_cseqnos 
			@i_tabla = 'bv_condicion', 
			@o_siguiente = @w_secuencial_condicion1 out
        
        
        	insert into cob_bvirtual..bv_condicion 
        		(co_id,        		co_ente,     			co_canal, 	 		co_moneda, 			co_transaccion,
         		 co_minimo,    		co_maximo,    			co_tipo_vigencia,	co_fecha_desde,     co_fecha_hasta,
         		 co_tipo_ejecucion,	co_estado,				co_fecha_reg,       co_fecha_mod,       co_autorizado,
         		 co_producto,		co_cuenta,      		co_grupo,		    co_empresa  )
			values 
		 		(@w_secuencial_condicion1, @ente_prueba_grupo,   1,			0,					@number_transaction,
				 0.00,				99999999999999,			'I',				null,				null,
				 'S',				'V',					getdate(),			getdate(),			'S',
				 null, 				null, 					null,				null )
		
		
		
			/*Registro de Combinación condición para Grupo*/
		
			exec cobis..sp_cseqnos 
			@i_tabla = 'bv_combinacion_condicion', 
			@o_siguiente = @w_secuencial_combinacion1 out
		
			insert into cob_bvirtual..bv_combinacion_condicion
					(cc_codigo, cc_condicion, cc_descripcion, cc_fecha_reg, cc_fecha_mod )
			values  (@w_secuencial_combinacion1,	@w_secuencial_condicion1,	 'Test Autorizacion Grupo' ,	getdate(), getdate())
		
	
			/*Registro de la combinacion requerida para la autorización Grupo*/		
		
			exec cobis..sp_cseqnos 
			@i_tabla = 'bv_atributo_combinacion', 
			@o_siguiente = @w_secuencial_atributo1 out
		
			insert into cob_bvirtual..bv_atributo_combinacion 
			   	(ac_codigo,   ac_codigo_comb, ac_tipo_login, ac_numero, ac_fecha_reg, ac_fecha_mod)
			values (@w_secuencial_atributo1,  @w_secuencial_combinacion1,  'A',  1,	getdate(), getdate())
		
			exec cobis..sp_cseqnos 
			@i_tabla = 'bv_atributo_combinacion', 
			@o_siguiente = @w_secuencial_atributo2 out
		
			insert into cob_bvirtual..bv_atributo_combinacion 
				    (ac_codigo,   ac_codigo_comb, ac_tipo_login, ac_numero, ac_fecha_reg, ac_fecha_mod)
			values	(@w_secuencial_atributo2, @w_secuencial_combinacion1,  'D',  1,	getdate(), getdate())
		
		
			select @cont = @cont + 1
		
		
		end
				
		/*Permite doble autorización para transacciones de transferencias y pagos*/
		--Transferencia a mis Cuentas Davivienda , Transferencias a Otras Cuentas Daviviend, Pagos Préstamos
		--update cob_bvirtual..bv_transaccion set tr_doble_autorizacion = 'S' where tr_transaccion in (18056,18059,1800025)
		
go		
		
		
