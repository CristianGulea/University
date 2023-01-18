bits 32
global start
extern exit, scanf, printf
import exit msvcrt.dll
import scanf msvcrt.dll
import printf msvcrt.dll
segment data use 32 class=data
a dd 0
b dd 0
c dd 0
tempVar0 dd 0
tempVar1 dd 0
tempVar2 dd 0
format_decimal db "%d", 0
segment code use32 class=code
start:
mov eax, 4
mov ebx, 5
mul ebx
mov [tempVar0], eax
mov eax, 12
add eax, [tempVar0]
mov [tempVar1], eax
mov eax, [tempVar1]
sub eax, 2
mov [tempVar2], eax
mov eax, [tempVar2]
mov [a], eax
push dword [a]
push format_decimal
call [printf]
add esp, 4*2
push dword 0
call [exit]
