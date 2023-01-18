%{
#include <stdio.h>
extern FILE* yyin;
extern char* yytext;
extern int yylex();
extern int yyparse();
extern int yylineno;
char dataSegment[4000];
char codeSegment[4000];
int tempVarCount = 0;
%}

%token BEGIN_PROGRAM
%token END_PROGRAM
%token POINT
%token VAR
%token INTEGER
%token REAL
%token<val> ID
%token COMMA
%token SEMICOLON
%token COLON_EQUAL
%token<val> CONST
%token PLUS
%token MINUS
%token MULTIPLICATION
%token DIVISION
%token READLN
%token WRITELN
%token COLON
%token UNKNOWN_CHARACTER
%token OPEN_ROUND_BRACKET
%token CLOSE_ROUND_BRACKET
%type<val> variabila
%type<val> expresie
%type<val> termen

%union{
    char val[50];
}

%%
program : VAR multi_declaratii_variabile BEGIN_PROGRAM instructiune_compusa END_PROGRAM POINT
        ;



multi_declaratii_variabile : declaratie_variabila SEMICOLON multi_declaratii_variabile | 
                                        ;


declaratie_variabila : ID COLON INTEGER
{
    char *textTemp = (char*)malloc(sizeof(char)*200);
    sprintf(textTemp, "%s dd 0\n", $1);
    strcat(dataSegment, textTemp);
    free(textTemp);
};


instructiune_compusa : instructiune SEMICOLON instructiune_compusa
                      |
                       instructiune SEMICOLON
                     ;


instructiune : ID COLON_EQUAL expresie
{
    char *textTemp = (char*)malloc(sizeof(char)*200);
    sprintf(textTemp, "mov eax, [%s]\nmov [%s], eax\n", $3, $1);
    strcat(codeSegment, textTemp);
    free(textTemp);
}
             ;

expresie:
expresie PLUS termen
{
    char *textTemp = (char*)malloc(sizeof(char)*200);
    char *tempVar = (char*)malloc(sizeof(char)*200);
    sprintf(tempVar, "tempVar%d dd 0\n", tempVarCount);
    strcat(dataSegment, tempVar);
    sprintf(tempVar, "tempVar%d", tempVarCount);
    strcpy($$, tempVar);
    char *param1 = (char*)malloc(sizeof(char)*50);
    if (isNumber($1) == -1 && isNumber($3) == -1){
	sprintf(textTemp, "mov eax, [%s]\nadd eax, [%s]\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == -1){
        sprintf(textTemp, "mov eax, %s\nadd eax, [%s]\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == 0){
        sprintf(textTemp, "mov eax, %s\nadd eax, %s\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == -1 && isNumber($3) == 0){
        sprintf(textTemp, "mov eax, [%s]\nadd eax, %s\nmov [%s], eax\n", $1, $3, tempVar);
    }
    strcat(codeSegment, textTemp);
    free(textTemp);
    free(tempVar);
    tempVarCount++;
}
|
expresie MINUS termen
{ 
    char *textTemp = (char*)malloc(sizeof(char)*200);
    char *tempVar = (char*)malloc(sizeof(char)*200);
    sprintf(tempVar, "tempVar%d dd 0\n", tempVarCount);
    strcat(dataSegment, tempVar);
    sprintf(tempVar, "tempVar%d", tempVarCount);
    strcpy($$, tempVar);
    if (isNumber($1) == -1 && isNumber($3) == -1){
	 sprintf(textTemp, "mov eax, [%s]\nsub eax, [%s]\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == -1 && isNumber($3) == 0){
         sprintf(textTemp, "mov eax, [%s]\nsub eax, %s\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == 0){
         sprintf(textTemp, "mov eax, %s\nsub eax, %s\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == -1){
         sprintf(textTemp, "mov eax, %s\nsub eax, [%s]\nmov [%s], eax\n", $1, $3, tempVar);
    }
    strcat(codeSegment, textTemp);
    free(textTemp);
    free(tempVar);
    tempVarCount++;
}
|
termen 
;

termen: 
termen MULTIPLICATION variabila
{
    char *textTemp = (char*)malloc(sizeof(char)*200);
    char *tempVar = (char*)malloc(sizeof(char)*200);
    sprintf(tempVar, "tempVar%d dd 0\n", tempVarCount);
    strcat(dataSegment, tempVar);
    sprintf(tempVar, "tempVar%d", tempVarCount);
    strcpy($$, tempVar);
    if (isNumber($1) == -1 && isNumber($3) == -1){
	sprintf(textTemp, "mov eax, [%s]\nmov ebx, [%s]\nmul ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == -1 && isNumber($3) == 0){
        sprintf(textTemp, "mov eax, [%s]\nmov ebx, %s\nmul ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == 0){
        sprintf(textTemp, "mov eax, %s\nmov ebx, %s\nmul ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == -1){
        sprintf(textTemp, "mov eax, %s\nmov ebx, [%s]\nmul ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    strcat(codeSegment, textTemp);
    free(textTemp);
    free(tempVar);
    tempVarCount++;
}
|
termen DIVISION variabila
{
    char *textTemp = (char*)malloc(sizeof(char)*200);
    char *tempVar = (char*)malloc(sizeof(char)*200);
    sprintf(tempVar, "tempVar%d dd 0\n", tempVarCount);
    strcat(dataSegment, tempVar);
    sprintf(tempVar, "tempVar%d", tempVarCount);
    strcpy($$, tempVar);
    if (isNumber($1) == -1 && isNumber($3) == -1){
	sprintf(textTemp, "mov edx, 0\nmov eax, [%s]\nmov ebx, [%s]\ndiv ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == -1 && isNumber($3) == 0){
	sprintf(textTemp, "mov edx, 0\nmov eax, [%s]\nmov ebx, %s\ndiv ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == 0){
	sprintf(textTemp, "mov edx, 0\nmov eax, %s\nmov ebx, %s\ndiv ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    if (isNumber($1) == 0 && isNumber($3) == -1){
	sprintf(textTemp, "mov edx, 0\nmov eax, %s\nmov ebx, [%s]\ndiv ebx\nmov [%s], eax\n", $1, $3, tempVar);
    }
    strcat(codeSegment, textTemp);
    free(textTemp);
    free(tempVar);
    tempVarCount++;
}
|
variabila;


variabila : CONST
{
    strcpy($$, $1);
}
           | ID
{
    strcpy($$, $1);
}
instructiune : READLN OPEN_ROUND_BRACKET ID CLOSE_ROUND_BRACKET
{
    char *textTemp = (char*)malloc(sizeof(char)*100);
    sprintf(textTemp, "push dword %s\npush format_decimal\ncall [scanf]\nadd esp, 4*2 \n", $3);
    strcat(codeSegment, textTemp);
    free(textTemp);
}              


|
               WRITELN OPEN_ROUND_BRACKET ID CLOSE_ROUND_BRACKET
{
    char *textTemp = (char*)malloc(sizeof(char)*100);
    sprintf(textTemp, "push dword [%s]\npush format_decimal\ncall [printf]\nadd esp, 4*2\n", $3);    
    strcat(codeSegment, textTemp);
    free(textTemp);
};

%%

int a = 0;

yyerror()
{
	a = a + 1;
	printf("Syntax error\n");
}

int isNumber(char string[]){
	for (int i = 0; i < strlen(string); i++){
		if (!isdigit(string[i])){
			return -1;
		}
        }
        return 0;
}

int main(int argc, char **argv)
{
	yyin = fopen(argv[1], "r");
	while (!feof(yyin)){
		yyparse();
        }
	if (a == 0){
		printf("bits 32\nglobal start\nextern exit, scanf, printf\nimport exit msvcrt.dll\nimport scanf msvcrt.dll\nimport printf msvcrt.dll\n");
		printf("segment data use 32 class=data\n");
		strcat(dataSegment, "format_decimal db \"%%d\", 0\n");
		printf(dataSegment);
		printf("segment code use32 class=code\nstart:\n");
		printf(codeSegment);
		printf("push dword 0\ncall [exit]\n");
	}
}
