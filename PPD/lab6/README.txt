Client-Server
Cerinta:
Clinica medicala
O statiune balneara ofera diverse spatii pentru diverse tratamente (acces piscina, masaj,
impachetari, etc.) Din cauza existentei unei cereri mari se solicita plata la momentul programarii,
existand totusi posibilitatea anularii, caz in care se face si returnarea banilor.
Exista n locatii (S1, S2,... Sn) la care se pot face programari, fiecare locatie oferind acelasi set de
tratamente (T(1), T(2), ... T(m)). Locatiile sunt deschise intre orele 10:00-18:00 si fiecare
tratament are asociat un cost C(j), un anumita durata de realizare Tdelta(j) si un numar maxim de
pacienti care pot fi tratati in acelasi timp N(i,j) 0<i<=n; 0<j<=m.
La prima iteratie de dezvoltare aplicatia de programari nu ofera interfata grafica prin care sa se
vizualizeze situatia la zi a programarilor. Un client va incerca sa isi faca o programare in ziua si
in intervalul dorit si va primi raspuns „programare reusita” sau „programare nereusita” in functie
de disponibilitate. Dupa mesajul de programare reusita clientul trimite cererea de plata.
Pentru planificare se adauga o inregistrare cu urmatoarele informatii: (nume, cnp, data,
locatie_tratament, tip_tratament, data_tratament, ora_tratament).
Pentru plata se adauga o inregistrare cu urmatoarele informatii: (data, cnp, suma).
Pentru anulare se sterge din inregistrarea corespunzatoare planificarii anterioare si pentru
returnare bani se adauga o inregistrare cu urmatoarele informatii: (data, cnp, -suma).
Observatie: Salvarea poate fi facuta intr-o baza de date sau in fisiere de tip text.
Periodic sistemul (2 cazuri testare: 5, 10 secunde) face o verificare a programarilor facute si a
incasarilor corespunzatoare prin verificarea corespondentei corecte intre numarul programarile
facute si sumele incasate dar si calcularea soldului total. Prin aceasta actiune se verifica si faptul
ca nu sunt suprapuneri in planificari (numarul total de clienti programati la locatia i, pentru
tratamentul T(j) la un anumit timp nu este mai mare decat numarul maxim N(i,j) admis).
Atentie: este important sa se asigure faptul ca verificarile nu se pot face la un moment in care o
anulare nu este complet realizata (actualizare planificare si returnare bani), dar se pot face intre o
cerere de programare confirmata si plata corespunzatoare ei. Verificarea trebuie sa semnalizeze
programarile neplatite.
Pentru simplificare se va considera ca toate cererile se fac pentru aceeasi zi.
Serverul va putea folosi maxim p threaduri pentru rezolvarea cererilor.
Pentru fiecare cerere de programare se va folosi o constructie de tip future.