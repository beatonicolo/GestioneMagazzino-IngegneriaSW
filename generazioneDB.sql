drop table segreteria CASCADE;
drop table magazziniere CASCADE;
drop table composto CASCADE;
drop table articolo CASCADE;
drop table uscita CASCADE;
drop table ordine CASCADE;
drop table negozio CASCADE;
drop table ingresso CASCADE;
drop table spedizioniere CASCADE;
drop table storico CASCADE;
drop table tipoArticolo CASCADE;
drop table sport CASCADE;

CREATE TABLE sport
(
NomeSport varchar(50) NOT NULL DEFAULT 'altri sport',
PRIMARY KEY (NomeSport)
);

CREATE TABLE tipoArticolo
(
NomeTipo varchar(50) NOT NULL,
Descrizione varchar(300) NOT NULL,
Materiale varchar(100) NOT NULL,
NomeSportE varchar(50),
PRIMARY KEY (NomeTipo),
FOREIGN KEY (NomeSportE) REFERENCES sport (NomeSport)
ON DELETE SET DEFAULT ON UPDATE CASCADE
);

CREATE TABLE storico
(
TipoA varchar(50) NOT NULL,
Mese integer NOT NULL,
Anno integer NOT NULL,
Entrate varchar(20),
Uscite varchar(20),
PRIMARY KEY (TipoA,Mese,Anno)
);

CREATE TABLE spedizioniere
(
NomeS varchar(20) NOT NULL,
PRIMARY KEY (NomeS)
);

CREATE TABLE ingresso
(
CodiceIngresso varchar(10) NOT NULL,
DataIngresso timestamp NOT NULL,
PRIMARY KEY (CodiceIngresso)
);

CREATE TABLE negozio
(
CF varchar(20) NOT NULL,
Nome varchar(30) NOT NULL,
Indirizzo varchar(100) NOT NULL,
Citta varchar(50) NOT NULL,
Username varchar(20) UNIQUE,
Password varchar(40) NOT NULL,
PRIMARY KEY (CF)
);

CREATE TABLE ordine
(
CodiceOrdine varchar(10) NOT NULL,
DataOrdine timestamp NOT NULL,
CFNegozio varchar(20) NOT NULL,
PrezzoTotale numeric(10,2) NOT NULL,
PRIMARY KEY (CodiceOrdine),
FOREIGN KEY (CFNegozio) REFERENCES negozio (CF)
ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE uscita
(
NumBolla varchar(20) NOT NULL,
DataUscita timestamp NOT NULL,
NomeSped varchar(20) NOT NULL,
RifOrdine varchar(10) NOT NULL,
PRIMARY KEY (NumBolla),
FOREIGN KEY (RifOrdine) REFERENCES ordine (CodiceOrdine)
ON DELETE NO ACTION ON UPDATE CASCADE,
FOREIGN KEY (NomeSped) REFERENCES spedizioniere (NomeS)
ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE articolo
(
CodiceArticolo varchar(10) NOT NULL,
Prezzo decimal(6,2) NOT NULL CHECK (prezzo >= 0.00),
DataProd varchar(20) NOT NULL,
Posizione varchar(20) NOT NULL,
TipoArt varchar(50),
CodIngresso varchar(20),
BollaU varchar(20),
PRIMARY KEY (CodiceArticolo),
FOREIGN KEY (TipoArt) REFERENCES tipoArticolo (NomeTipo)
ON DELETE NO ACTION ON UPDATE CASCADE,
FOREIGN KEY (CodIngresso) REFERENCES ingresso (CodiceIngresso)
ON DELETE SET NULL ON UPDATE CASCADE,
FOREIGN KEY (BollaU) REFERENCES uscita (NumBolla)
ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE composto
(
CodiceO varchar(10) NOT NULL,
NomeT varchar(50) NOT NULL,
Quantita numeric(4) NOT NULL,
PRIMARY KEY (CodiceO, NomeT),
FOREIGN KEY (CodiceO) REFERENCES ordine (CodiceOrdine)
ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (NomeT) REFERENCES tipoArticolo (nomeTipo)
ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE magazziniere
(
UsernameM varchar(20) NOT NULL,
PasswordM varchar(50) NOT NULL,
PRIMARY KEY (UsernameM)
);

CREATE TABLE segreteria
(
UsernameS varchar(20) NOT NULL,
PasswordS varchar(50) NOT NULL,
PRIMARY KEY (UsernameS)
);

INSERT INTO magazziniere (UsernameM,PasswordM) 
VALUES ('nico','nico'),('lazza','lazza'),('filippo','filippo');

INSERT INTO segreteria (UsernameS,PasswordS) 
VALUES ('nico','nico'),('lazza','lazza'),('filippo','filippo');

INSERT INTO sport (NomeSport) VALUES ('calcio'),
('tennis'),
('basket'),
('pallavolo'),
('golf'),
('rugby'),
('arti marziali'),
('nuoto'),
('palestra'),
('motori'),
('ciclismo'),
('alpinismo'),
('altri sport');

INSERT INTO tipoArticolo (nomeTipo, Descrizione, Materiale, NomeSportE) VALUES ('pallone in cuoio','pallone in cuoio adatto al calcio','cuoio','calcio'),
('pallone leggero','pallone leggero tecnico per la pallavolo','tela','pallavolo'),
('racchetta professionale','racchetta professionale adatta al tennis competitivo','alluminio','tennis'),
('cuffia','cuffia semplice in cotone elasticizzato','cotone','nuoto'),
('occhialini','occhialini per nuoto agonistico','gomma','nuoto'),
('canottiera cleveland','canottiera per basket dei cleveland cavaliers','cotone','basket'),
('kit golf principiante','kit composto da tre mazze diverse, borsone e cinque palline','kit','golf'),
('scarpe mercurial','scarpe da calcio di tipo mercurial','cuoio','calcio'),
('racchettoni','kit di racchettoni adatti al gioco in spiaggia','kit','altri sport'),
('ciabatte uomo nere','ciabatte nere da uomo adatte ad ogni occasione','gomma','altri sport'),
('casco sportivo','casco sportivo omologato adatto a tutti i ciclomotori','plastica','motori'),
('guanti palestra','guanti con rivestimento in gel adatti al sollevamento pesi','tessuto','palestra'),
('casco rugby','casco da rugby omologato e adatto al rugby agonistico','vari','rugby'),
('borraccia ciclismo','borraccia di dimensioni standard per il ciclismo ','plastica','ciclismo'),
('scarponi unisex alpinismo','scarponi unisex adatti alla montagna','vari','alpinismo');

INSERT INTO storico (TipoA, Mese, Anno, Entrate, Uscite) VALUES ('pallone in cuoio',1,2018,'0','0'),
('pallone leggero',1,2018,'0','0'),
('racchetta professionale',1,2018,'0','0'),
('cuffia',1,2018,'0','0'),
('occhialini',1,2018,'0','0'),
('canottiera cleveland',1,2018,'0','0'),
('kit golf principiante',1,2018,'0','0'),
('scarpe mercurial',1,2018,'0','0'),
('racchettoni',1,2018,'0','0'),
('ciabatte uomo nere',1,2018,'0','0'),
('casco sportivo',1,2018,'0','0'),
('guanti palestra',1,2018,'0','0'),
('casco rugby',1,2018,'0','0'),
('borraccia ciclismo',1,2018,'0','0'),
('scarponi unisex alpinismo',1,2018,'0','0');

INSERT INTO spedizioniere (NomeS) VALUES ('ups'),
('poste italiane'),
('dhl'),
('tnt'),
('gls'),
('bartolini'),
('sda');

INSERT INTO ingresso (CodiceIngresso, DataIngresso) VALUES ('1','2017-12-16'),
('2','2018-01-17'),
('3','2018-01-18'),
('4','2018-02-19'),
('5','2018-02-19'),
('6','2018-03-19'),
('7','2018-03-19'),
('8','2018-04-19'),
('9','2018-04-19'),
('10','2018-05-19'),
('11','2018-05-19'),
('12','2018-05-24');

INSERT INTO negozio (CF, Nome, Indirizzo, Citta, Username, Password) VALUES ('lzzmnl','lazzaretti merci','via roma, 119','monticello conte otto (vi)','lazza','lazza'),
('beatto','beato merci','via milano, 32','brendola (vi)','beato','beatopass'),
('nclbts','nicolo merci','via napoli, 71','altavilla (vi)','nico','nico'),
('flirro','filippo merci','viale torino, 16','vicenza','filippo','filippo'),
('dgiflo','de guio merci','strada italia, 22','verona','deguio','password'),
('emnlar','san bonifacio shop','piazza dolomiti, 3','san bonifacio (vr)','sbshop','qwerty');

INSERT INTO ordine (CodiceOrdine, DataOrdine, CFNegozio, PrezzoTotale) VALUES ('1','2018-03-16','lzzmnl','50.00'),
('2','2018-03-16','lzzmnl','30.00'),
('3','2018-03-22','beatto','20.00'),
('4','2018-03-25','beatto','119.99'),
('5','2018-04-10','nclbts','68.98'),
('6','2018-04-12','nclbts','50.99'),
('7','2018-05-09','flirro','205.00'),
('8','2018-05-18','flirro','110.00'),
('9','2018-05-20','lzzmnl','134.00'),
('10','2018-05-23','nclbts','98.00');

INSERT INTO uscita (DataUscita, NumBolla, NomeSped, RifOrdine) VALUES ('2018-03-18','1','gls','2'),
('2018-03-21','2','poste italiane','1'),
('2018-03-26','3','bartolini','3'),
('2018-04-05','4','dhl','4'),
('2018-04-20','5','gls','6'),
('2018-04-21','6','tnt','5'),
('2018-05-27','7','bartolini','7');

INSERT INTO articolo (CodiceArticolo, Prezzo, DataProd, Posizione, TipoArt, CodIngresso, BollaU) VALUES ('1',20.00,'2017-03-10','a-1','pallone in cuoio','1','1'),
('2',20.00,'2017-03-10','a-1','pallone in cuoio','1','2'),
('3',20.00,'2017-03-10','a-1','pallone in cuoio','2','2'),
('4',20.00,'2017-03-10','a-1','pallone in cuoio','2',null),
('5',10.00,'2017-02-21','a-2','pallone leggero','3','1'),
('6',10.00,'2017-02-21','a-2','pallone leggero','3','2'),
('7',10.00,'2017-02-21','a-2','pallone leggero','3','3'),
('8',10.00,'2017-02-21','a-2','pallone leggero','4','3'),
('9',10.00,'2017-02-21','a-2','pallone leggero','4','4'),
('10',95.00,'2017-12-11','f-1','racchetta professionale','4','4'),
('11',95.00,'2017-12-11','f-1','racchetta professionale','5','7'),
('12',95.00,'2017-12-11','f-1','racchetta professionale','5',null),
('13',14.99,'2018-01-30','c-1','cuffia','6','5'),
('14',14.99,'2018-01-30','c-1','cuffia','6','4'),
('15',14.99,'2018-01-30','c-1','cuffia','6','6'),
('16',19.95,'2017-10-22','f-3','occhialini','6',null),
('17',19.95,'2017-10-22','f-3','occhialini','6',null),
('18',19.95,'2017-10-22','f-3','occhialini','6',null),
('19',14.99,'2018-03-11','c-1','cuffia','7','6'),
('20',14.99,'2018-03-11','c-1','cuffia','7',null),
('21',14.99,'2018-03-11','c-1','cuffia','7',null),
('22',39.00,'2017-03-30','a-2','canottiera cleveland','8','6'),
('23',39.00,'2017-03-30','a-2','canottiera cleveland','8',null),
('24',39.00,'2017-03-30','a-2','canottiera cleveland','8',null),
('25',110.00,'2017-11-04','b-1','kit golf principiante','9','7'),
('26',110.00,'2017-11-04','b-1','kit golf principiante','9',null),
('27',140.00,'2018-02-22','a-3','scarpe mercurial','10',null),
('28',140.00,'2018-02-22','a-3','scarpe mercurial','10',null),
('29',12.00,'2017-06-05','c-2','racchettoni','11','5'),
('30',12.00,'2017-06-05','c-2','racchettoni','11','5'),
('31',12.00,'2017-06-05','c-2','racchettoni','11','5'),
('32',14.00,'2017-06-09','c-3','ciabatte uomo nere','11',null),
('33',14.00,'2017-06-09','c-3','ciabatte uomo nere','11',null),
('34',89.00,'2018-04-12','d-1','casco sportivo','12',null),
('35',89.00,'2018-04-12','d-1','casco sportivo','12',null),
('36',16.00,'2017-08-30','e-1','guanti palestra','12',null),
('37',16.00,'2017-08-30','e-1','guanti palestra','12',null),
('38',59.00,'2018-01-15','d-2','casco rugby','12',null),
('39',59.00,'2018-01-15','d-2','casco rugby','12',null),
('40',59.00,'2018-01-15','d-2','casco rugby','12',null);

INSERT INTO composto (CodiceO, NomeT, Quantita) VALUES ('1','pallone in cuoio',2),
('1','pallone leggero',1),
('2','pallone in cuoio',1),
('2','pallone leggero',1),
('3','pallone leggero',2),
('4','pallone leggero',1),
('4','cuffia',1),
('4','racchetta professionale',1),
('5','cuffia',2),
('5','canottiera cleveland',1),
('6','cuffia',1),
('6','racchettoni',3),
('7','kit golf principiante',1),
('7','racchetta professionale',1),
('8','casco sportivo',3),
('8','guanti palestra',3),
('9','racchetta professionale',2),
('9','canottiera cleveland',2),
('10','canottiera cleveland',1),
('10','casco rugby',1);

CREATE INDEX ON tipoArticolo ((lower(nomeTipo)));
CREATE INDEX ON ordine ((lower(CFNegozio)));
CREATE INDEX ON ordine (CodiceOrdine, DataOrdine, PrezzoTotale, lower(CFNegozio));
CREATE INDEX ON uscita (DataUscita, NumBolla, NomeSped, RifOrdine);
CREATE INDEX ON ingresso (CodiceIngresso, DataIngresso);
CREATE INDEX ON segreteria (lower(UsernameS), PasswordS);
CREATE INDEX ON articolo ((lower(tipoArt)));
CREATE INDEX ON articolo (CodiceArticolo, Prezzo, DataProd, Posizione, TipoArt, CodIngresso, Bollau);
CREATE INDEX ON articolo (BollaU);
CREATE INDEX ON composto (codiceO, lower(nomeT), quantita);
CREATE INDEX ON composto (codiceO, nomeT, quantita);