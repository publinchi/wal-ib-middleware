use cobis
GO

if exists (select * from sysobjects where name = 'sp_test_bv')
  drop proc sp_test_bv
go