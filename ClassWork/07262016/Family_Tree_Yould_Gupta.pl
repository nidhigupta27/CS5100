'Queen Elizabeth II'.
'Philip'.
'Charles'.
'Anne'.
'Andrew'.
'Edward'.
'Diana'.
'Camilla'.
'Captain Mark Philips'.
'Anne'.
'Vice-Admiral Timothy Laurence'.
'Sarah'.
'Sophie'.
'William'.
'Kate'.
'Harry'.
'Autumn Philips'.
'Peter'.
'Zara'.
'Mike'.
'Beatrice'.
'Eugenie'.
'Lousie'.
'James'.
'George'.
'Savannah'.
'Isla'.
'Mia Grace'.
wife('Queen Elizabeth II','Philip').
wife('Camilla','Captain Mark Philips').
wife('Sophie','Edward').
wife('Kate','William').
wife('Autumn Philips','Peter Philips').
wife('Zara Philips','Mike Tindall').
wife('Sarah','Andrew').
wife('Diana','Charles').
son('Charles','Philip').
son('Andrew','Philip').
son('Edward','Philip').
son('William','Charles').
son('Harry','Charles').
son('Peter Philips','Captain Mark Philips').
son('James','Edward').
son('George','William').
son(Z,X):- wife(X,Y),son(Z,Y);
daughter('Anne','Philip').
daughter('Zara Philips','Captain Mark Philip').
daughter('Beatrice','Andrew').
daughter('Eugenie','Andrew').
daughter('Lousie','Edward').
daughter('Savannah','Peter Philips').
daughter('Isla','Peter Philps').
daughter('Mia Grace','Mike Tindall').
daughter(Z,X):- wife(X,Y),daughter(Z,Y).
husband(X,Y) :- wife(Y,X).
child(X,Y):- son(X,Y);daughter(X,Y).
spouse(X,Y):- wife(X,Y).
spouse(X,Y):-husband(X,Y).
parent(X,Y):-son(X,Y).
parent(X,Y):-daughter(X,Y).
grandchild(X,Y) :- son(X,Z),son(Z,Y);daughter(X,Z),daughter(Z,Y).
greatGrandParent(X,Y):-parent(X,W),parent(W,Z),parent(Z,Y).
greatGrandChild(X,Y):-son(X,W),son(W,Z),son(Z,Y).
greatGrandChild(X,Y):-daughter(X,W),daughter(W,Z),daughter(Z,Y).
brother(X,Y):-parent(Z,X),parent(Z,Y),son(X,Z).
sister(X,Y):-parent(Z,X),parent(Z,Y),daughter(X,Z).
uncle(X,Y):-parent(Z,Y),brother(X,Z).
brotherInLaw(X,Y):-spouse(Y,Z),brother(X,Z).
brotherInLaw(X,Y):-spouse(Y,Z),brother(W,Z),spouse(W,Y).
sisterInLaw(X,Y):-spouse(Y,Z),sister(X,Z).
sisterInLaw(X,Y):-spouse(Y,Z),brother(W,Z),spouse(W,Y).
sisterInLaw(X,Y):-spouse(Y,Z),sister(W,Z),spouse(W,Y).
neice(X,Y):-parent(Z,X),brother(Z,Y).
neice(X,Y):-parent(Z,X),sister(Z,Y).