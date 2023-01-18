--------DirtyRead------------
begin transaction
update Angajat set Prenume='------' where AId = 9;
waitfor delay '00:00:04'
rollback transaction

--------UnrepeatableReads------------
delete from Angajat where Prenume='------'

insert into Angajat(Nume, Prenume, Salar, MId) values ('Paul', 'Marian', 123, 15)
begin transaction
waitfor delay '00:00:04'
update Angajat set Prenume='------' where Prenume='Marian';
commit transaction

--------PhantomReads------------
delete from Angajat where Nume='Phantom'

begin tran
waitfor delay '00:00:04'
insert into Angajat(Nume, Prenume, Salar, MId) values ('Phantom', 'Reads', 777, 15)
commit tran

--------DeadLock------------
select * from Angajat
select * from Client

begin tran
update Angajat set Nume='tran1' where AId = 10
waitfor delay '00:00:08'
update Client set Nume='tran1' where CId = 1
commit tran
