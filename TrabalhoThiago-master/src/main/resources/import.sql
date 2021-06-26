-- create default activities
INSERT INTO Activity (id, filename,lps,problem)
VALUES(999, "zerinho.py",	"c|cpp|java", "A");

INSERT INTO Activity (id, filename,lps,problem)
VALUES(998, "mergulho.py",	"c|cpp|java", "B");

-- create response to them
-- content has both input and output in the following format:
-- input | output in base64
INSERT INTO Response (id, filename,content,problem)
VALUES(999, "mergulho.py",	"NSAzCjMgMSA1IHwyIDQ=", "B");
INSERT INTO Response (id, filename,content,problem)
VALUES(998, "mergulho.py",	"NiA2CjYgMSAzIDIgNSA0fCo=", "B");


INSERT INTO Response (id, filename,content,problem)
VALUES(997, "zerinho.py",	"MCAwIDB8Kg==", "A");

INSERT INTO Response (id, filename,content,problem)
VALUES(996, "zerinho.py",	"MSAxIDB8Qw==", "A");
