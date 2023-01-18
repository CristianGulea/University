use SGBDMagazinElectronice;
-------------------------------------------------------
create table LogTable(
Lid int identity primary key, 
TypeOperation varchar(50), 
TableOperation varchar(50), 
ExecutionDate date
)
-------------------------------------------------------
select * from MagazinProdus
select * from Magazin
select * from Produs
-------------------------------------------------------
--Functii de validare
go
create or alter function dbo.ValidareNumarAngajati(@NumarAngajati int)
returns int
as
begin
	declare @flg int;
	if (@NumarAngajati < 0) set @flg = 0;
	else set @flg = 1;
	return @flg;
end
go

go
create or alter function dbo.ValidareNumeProdus(@Nume varchar(50))
returns int
as
begin
	declare @flg int;
	if (len(@Nume) = 0) set @flg = 0;
	else set @flg = 1;
	return @flg;
end

go 
create or alter function dbo.ValidarePretPeodus(@Pret int)
returns int
as 
begin
	declare @flg int;
	if (@Pret <= 0) set @flg = 0;
	else set @flg = 1;
	return @flg;
end
go

go
create or alter function dbo.ValidareIdProducator(@Fid int)
returns int
as
begin
	declare @flg int;
	if (exists(select * from Producator where FId = @Fid)) set @flg = 1;
	else set @flg = 0;
	return @flg;
end
go
-------------------------------------------------------

go
create or alter procedure InsertProcedure
@NumarAngajati int,
@Nume varchar(50),
@Pret int,
@FId int
as
begin
	set nocount on;
	declare @cont int;
	set @cont = 0;
	begin tran
		begin try
		-----------------------------------------------------------------------------------------------
			if (dbo.ValidareNumarAngajati(@NumarAngajati) = 0)
				begin
					raiserror('Nummarul de angajati trebuie sa fie numar strinct pozitiv!', 14, 1)
				end
			declare @MId int;
			insert into Magazin (NumarAngajati) values (@NumarAngajati);
			set @MId = SCOPE_IDENTITY();
			insert into LogTable (TypeOperation, TableOperation, ExecutionDate) values ('COMMIT', 'Magazin', GETDATE());
			set @cont = 1;
		-----------------------------------------------------------------------------------------------

			if (dbo.ValidareNumeProdus(@Nume) = 0)
				begin
					raiserror('Numele produsului trebuie sa nu fie vid!', 14, 1);
				end
			if (dbo.ValidarePretPeodus(@Pret) = 0)
				begin
					raiserror('Pretul trebuie sa fie strict pozitiv!', 14, 1);
				end
			if (dbo.ValidareIdProducator(@FId) = 0)
				begin
					raiserror('Nu exista id-ul producatorului!', 14, 1);
				end
			declare @Pid int;
			insert into Produs(Nume, Pret, FId) values (@Nume, @Pret, @FId);
			set @Pid = SCOPE_IDENTITY();
			insert into LogTable (TypeOperation, TableOperation, ExecutionDate) values ('COMMIT', 'Produs', GETDATE());
			set @cont = 2;	
		-----------------------------------------------------------------------------------------------
			insert into MagazinProdus(MId, PCod) values (@Mid, @Pid)
			insert into LogTable (TypeOperation, TableOperation, ExecutionDate) values ('COMMIT', 'MagazinProdus', GETDATE());
			set @cont = 3;
		-----------------------------------------------------------------------------------------------
			commit tran
			print 'Tran-COMMIT'
		end try
		begin catch
			rollback tran
			if (@cont >= 1)
				begin
					insert into LogTable(TypeOperation, TableOperation, ExecutionDate) values ('ROLLBACK', 'Magazin', GETDATE());
				end
			if (@cont >= 2)
				begin
					insert into LogTable(TypeOperation, TableOperation, ExecutionDate) values ('ROLLBACK', 'Produs', GETDATE());
				end
			if (@cont >= 1)
				begin
					insert into LogTable(TypeOperation, TableOperation, ExecutionDate) values ('ROLLBACK', 'MagazinProdus', GETDATE());
				end
			print 'Tran-ROLLBACK'
		end catch
end
go




go
create or alter procedure InsertProcedure1
@NumarAngajati int,
@Nume varchar(50),
@Pret int,
@FId int
as
begin
	set nocount on;
	-----------------------------------------------------------------------------------------------
	begin tran
		begin try
		
			if (dbo.ValidareNumarAngajati(@NumarAngajati) = 0)
				begin
					raiserror('Nummarul de angajati trebuie sa fie numar strinct pozitiv!', 14, 1)
				end
			declare @MId int;
			set @Mid = -1;
			insert into Magazin (NumarAngajati) values (@NumarAngajati);
			set @MId = SCOPE_IDENTITY();
			insert into LogTable (TypeOperation, TableOperation, ExecutionDate) values ('COMMIT', 'Magazin', GETDATE());
			commit tran;
			print 'Tran-COMMIT-Magazin';
		end try
		begin catch
			rollback tran;
			insert into LogTable(TypeOperation, TableOperation, ExecutionDate) values ('ROLLBACK', 'Magazin', GETDATE());
		end catch
	
		-----------------------------------------------------------------------------------------------
		begin tran
			begin try
				if (dbo.ValidareNumeProdus(@Nume) = 0)
					begin
						raiserror('Numele produsului trebuie sa nu fie vid!', 14, 1);
					end
				if (dbo.ValidarePretPeodus(@Pret) = 0)
					begin
						raiserror('Pretul trebuie sa fie strict pozitiv!', 14, 1);
					end
				if (dbo.ValidareIdProducator(@FId) = 0)
					begin
						raiserror('Nu exista id-ul producatorului!', 14, 1);
					end
				declare @Pid int;
				set @Pid = -1;
				insert into Produs(Nume, Pret, FId) values (@Nume, @Pret, @FId);
				set @Pid = SCOPE_IDENTITY();
				insert into LogTable (TypeOperation, TableOperation, ExecutionDate) values ('COMMIT', 'Produs', GETDATE());
				commit tran
				print 'Tran-COMMIT-Produs';
			end try
			begin catch
				rollback tran;
				insert into LogTable(TypeOperation, TableOperation, ExecutionDate) values ('ROLLBACK', 'Produs', GETDATE());
			end catch
		-----------------------------------------------------------------------------------------------
		begin tran
			if ((@Mid <> -1) and (@Pid <> -1))
				begin
					insert into MagazinProdus(MId, PCod) values (@Mid, @Pid)
					insert into LogTable (TypeOperation, TableOperation, ExecutionDate) values ('COMMIT', 'MagazinProdus', GETDATE());
					commit tran
					print 'Tran-COMMIT-MagazinProdus';
				end
			else
				begin
					rollback tran;
					insert into LogTable(TypeOperation, TableOperation, ExecutionDate) values ('ROLLBACK', 'MagazinProdus', GETDATE());
				end
		-----------------------------------------------------------------------------------------------
end
go



select dbo.ValidareIdProducator(12)
select * from Producator
delete from Magazin
delete from Produs
select * from Produs
select * from MagazinProdus
select * from Magazin
exec InsertProcedure 12, 'prrr3', 123, 1
exec InsertProcedure1 -1, 'produs nou2', 123, 1
select * from CommitTable;
select * from LogTable
delete from LogTable

