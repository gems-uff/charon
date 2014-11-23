%currentSelection('B1', ['3', '4']).
%currentSelection('B1', ['1', '2']).

isSelected(E, Path) :- currentSelection(E, Path), !.
isSelected(E, [ID|Path]) :- not(currentSelection(E, [ID|Path])), isSelected(E, Path).

%selectElement(E, Path) :- not(isSelected(E, Path)), assertz(currentSelection(E, Path)), rule(R, [E|Path]).
selectElement(E, Path) :- not(isSelected(E, Path)), assertz(currentSelection(E, Path)).

rule('X', Path) :- (abstractWorkflow('B1'); isSelected('B1', Path)) -> (selectElement('E1', Path); selectElement('D1', Path)).
rule('Y', Path) :- (abstractWorkflow('D1'); isSelected('D1', Path)) -> (selectElement('F1', Path)). 


%selectElement('B1', []), currentSelection(A, _).
%selectElement('B1', []), isSelected('B1', ['1','2']).
%currentSelection('B1', []).
%isSelected('B1', ['1', '2', '3', '4']).
%selectElement('B1', []), rule('X', ['B1']), currentSelection('D1', _).