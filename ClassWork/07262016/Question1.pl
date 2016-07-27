motorway(53,62).
motorway(62,83).
motorway(62,63).
motorway(62,64).
motorway(62,76).
motorway(62,66).
motorway(53,68).
motorway(54,56).
motorway(54,62).
motorway(54,68).
motorway(54,83).
motorway(63,66).
motorway(83,62).
motorway(60,83).
motorway(60,68).
motorway(60,83).
motorway(63,64).
motorway(63,76).
motorway(63,64).
motorway(63,68).
motorway(64,69).
motorway(68,66).
motorway(69,66).
footpath(52,53).
footpath(53,54).
footpath(56,58).
footpath(56,57).
footpath(57,58).
footpath(57,59).
footpath(58,59).
footpath(59,60).
footpath(65,68).
footpath(76,69).
footpath(83,65).
footpath(68,65).
route(A,B):- footpath(A,B);motorway(A,B).
route(A,B):- footpath(B,A);motorway(B,A).
route(A,B):- move(A,B,[]).
%%%route(A,B):- move(B,A,[]).

len([],0).
len([_|T],N) :- len(T,X),N is X+1.
move(A,B,S):- (footpath(A,X);motorway(A,X)),
              not(member(X,S)),
			  (B=X ;
			   move(X,B,[A|S])),
			   len(S,D),
			   D<2.
			   

