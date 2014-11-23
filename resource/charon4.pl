selectElement(E,Path) :-
	not isSelected(E,Path),
	assertz(currentSelection(E,Path)),
	rules(_).  

rules(I) :- rule(A, B).

isSelected(E,Path) :- 
	currentSelection(E,Path), 
	!.

isSelected(E,[ID|Path]) :-
	not currentSelection(E,[ID|Path]), 
	isSelected(E,Path). 

rule('X',Path) :-
	(abstractWorkflow('B1') ; isSelected('B1',Path)) -> (selectElement('E1',Path) ; selectElement('D1',Path)). 

rule('Y',Path) :-
	(abstractWorkflow('D1') ; isSelected('D1',Path)) -> selectElement('F1',Path).     
 

%abstractWorkflow('B1').
%rule('Z',Path) :-
%	(abstractWorkflow('B1') ; isSelected('B1',Path)) -> (assertz(abstractWorkflow('1')) ; assertz(abstractWorkflow('2'))).    