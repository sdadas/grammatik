grammar Grammatik;

fragment DIGIT: [0-9];

STRING_TYPE: 'string';
NUMBER_TYPE: 'number';
BOOLEAN_TYPE: 'boolean';
DECIMAL: DIGIT+ ('.' DIGIT+)?;
STRING: '"'(~'"')*'"';
BOOLEAN: 'true' | 'false';
NULL: 'null';
IDENTIFIER: [a-zA-Z0-9._]+;
WHITESPACE: [ \r\n\t]+ -> skip;
COMMENT:  '#' ~( '\r' | '\n' )*;
NEWLINE: (EOF | '\n');
ENDLINE: ';';


eval: (functionDeclaration | COMMENT)* (group | COMMENT)*;

name: IDENTIFIER;
functionArg: STRING_TYPE | NUMBER_TYPE | BOOLEAN_TYPE;
functionValue: DECIMAL | STRING | BOOLEAN | NULL;
functionDeclaration: 'declare' name '(' functionArgs? ')' ENDLINE;
functionInvokation: name '(' functionValues? ')';
functionArgs: functionArg (',' functionArg)*;
functionValues: functionValue (',' functionValue)*;
probability: '[' DECIMAL ']';

group: name tagName? '{' (entry)+ '}';
entry: (expression)+ probability? ENDLINE COMMENT?;
expression: dynamicExpression | constantExpression;
dynamicExpression: '${' (name | functionInvokation | constantExpression) tagName? probability? '}';
constantExpression: STRING;
tagName: ':' constantExpression;

