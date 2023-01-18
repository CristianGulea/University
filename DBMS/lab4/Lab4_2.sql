insert into Angajat(Nume, Prenume, Salar, MId) values ('Paul', 'Marian', 123, 15), ('Maria', 'Ioana', 2345, 15), ('Andrei', 'George', 435, 15);
select * from Magazin

--------DirtyRead------------

set transaction isolation level read uncommitted
begin tran
select * from Angajat
waitfor delay '00:00:08'
select * from Angajat
commit tran


set transaction isolation level read committed
begin tran
select * from Angajat
waitfor delay '00:00:08'
select * from Angajat
commit tran


--------UnrepeatableReads------------

set transaction isolation level read committed
begin tran
select * from Angajat
waitfor delay '00:00:08'
select * from Angajat
commit tran


set transaction isolation level repeatable read
begin tran
select * from Angajat
waitfor delay '00:00:08'
select * from Angajat
commit tran


--------PhantomReads------------
set transaction isolation level repeatable read
begin tran
select * from Angajat
waitfor delay '00:00:08'
select * from Angajat
commit tran

set transaction isolation level serializable
begin tran
select * from Angajat
waitfor delay '00:00:08'
select * from Angajat
commit tran


--------DeadLock------------
select * from Angajat
select * from Client

--set deadlock_priority low
set deadlock_priority high
begin tran
update Client set Nume='tran2' where CId = 1
waitfor delay '00:00:08'
update Angajat set Nume='tran2' where AId = 10
commit tran