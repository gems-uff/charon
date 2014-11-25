and(A, B) :- A, B.
imp(A, B) :- not(A); B.
or(A, B) :- A ; B.

evaluate(E, true) :- E, !.

bool(true).
bool(false).
boolA(true).

tableBody(A,B,C,E) :-
  bool(A),
  bool(B),
  bool(C),
  evaluate(E, Result).
  
tableBody(A,B,C,D,E) :-
  boolA(A),
  bool(B),
  bool(C),
  bool(D),
  evaluate(E, Result). 
  
tableBody(A,B,E) :-
  bool(A),
  boolA(B),
  evaluate(E, Result).
  
  
%tableBody(A,B,C, and(and(imp(A,B),imp(B, C)),imp(C,not(A)))). 
%tableBody(A,B, and(imp(A, B),imp(B,not(A)))).
%tableBody(A,B, imp( or(A, not(A)), and(B, not(B)))).
%tableBody(A,B,C,D,and(imp(A, or(B,C)),imp(C, D))).