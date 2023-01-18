%{
#include <string.h>
#include <stdio.h>
#include "bisonFile.tab.h"
%}

%option noyywrap

%%
[+ -]{0,1}[1-9][0-9]*"."[0-9]+ {strcpy(yylval.val, yytext); return CONST;}
[+ -]{0,1}0"."[0-9]+ {strcpy(yylval.val, yytext); return CONST;}
[+ -]{0,1}0|[1-9][0-9]* {strcpy(yylval.val, yytext); return CONST;}
"begin"   return BEGIN_PROGRAM;
"end"   return END_PROGRAM;
"."     return POINT;
"var"   return VAR;
","     return COMMA;
";"     return SEMICOLON;
":"     return COLON;
":="    return COLON_EQUAL;
"integer"       return INTEGER;
"real"  return REAL;
"+"     return PLUS;
"-"     return MINUS;
"*"     return MULTIPLICATION;
"/"	return DIVISION;
"("     return OPEN_ROUND_BRACKET;
")"     return CLOSE_ROUND_BRACKET;
"readln"        return READLN;
"writeln"       return WRITELN;
[a-zA-Z_][a-zA-Z0-9_]{0,7} {sprintf(yylval.val, "%s", yytext); return ID;}
" "+    ;
[\n]    {yylineno++;};
[\t]    ;
.       return UNKNOWN_CHARACTER;
%%
