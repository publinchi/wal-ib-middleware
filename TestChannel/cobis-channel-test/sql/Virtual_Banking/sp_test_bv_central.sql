use cobis
GO


CREATE PROCEDURE sp_test_bv
	-- Add the parameters for the stored procedure here
	@i_var1 int, 
	@i_var2 varchar(10)
AS
BEGIN
	
	SELECT @i_var1,@i_var2,'Response'
END

GO


