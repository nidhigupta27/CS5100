wife('Elizabeth','Philip').
wife('Camilla','Captain Mark Philips').
wife('Sophie','Edward').
wife('Kate','William').
wife('Autumn Philips','Peter').
wife('Zara','Mike').
wife('Sarah','Andrew').
wife('Diana','Charles').
wife('Anne','Captain Mark Philip').
wife('Anne','Vice-Admiral Timothy Laurence').
wife('Autumn Philips','Peter').
son('Charles','Philip').
son('Andrew','Philip').
son('Edward','Philip').
son('William','Charles').
son('Harry','Charles').
son('Peter','Captain Mark Philips').
son('James','Edward').
son('George','William').
son(Z,X):- wife(X,Y),son(Z,Y);
daughter('Anne','Philip').
daughter('Zara','Anne').
daughter('Beatrice','Andrew').
daughter('Eugenie','Andrew').
daughter('Lousie','Edward').
daughter('Savannah','Peter').
daughter('Isla','Peter').
daughter('Mia Grace','Mike').
daughter(Z,X):- wife(X,Y),daughter(Z,Y).
husband(X,Y) :- wife(Y,X).
child(X,Y):- parent(Y,X).
spouse(X,Y):- wife(X,Y).
spouse(X,Y):-husband(X,Y).
parent(X,Y):-son(Y,X).
parent(X,Y):-daughter(Y,X).
grandchild(X,Y) :- child(X,Z),child(Z,Y).
grandparent(X,Y) :- grandchild(Y,X).
greatGrandParent(X,Y):-parent(X,W),parent(W,Z),parent(Z,Y).
greatGrandChild(X,Y):-child(X,W),child(W,Z),child(Z,Y).
brother(X,Y):-son(X,Z),parent(Z,X),parent(Z,Y),X\=Y.
sister(X,Y):-parent(Z,X),parent(Z,Y),daughter(X,Z),X\=Y.
uncle(X,Y):-parent(Z,Y),brother(X,Z).
brotherInLaw(X,Y):-spouse(Y,Z),brother(X,Z).
brotherInLaw(X,Y):-spouse(Y,Z),brother(W,Z),spouse(W,Y).
sisterInLaw(X,Y):-spouse(Y,Z),sister(X,Z).
sisterInLaw(X,Y):-spouse(Y,Z),brother(W,Z),spouse(X,W).
niece(X,Y):-daughter(X,Z),brother(Z,Y).
niece(X,Y):-daughter(X,Z),sister(Z,Y).