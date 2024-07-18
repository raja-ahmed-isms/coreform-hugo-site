-- Currently this .sql file is only used for testing and must be kept synced with the production environment
CREATE TABLE cssversion (
  versionid INT NOT NULL
);

INSERT INTO cssversion (versionid) VALUES (29);

CREATE TABLE admin_log (
  action VARCHAR(64) NOT NULL,
  userid INT NOT NULL
);

CREATE TABLE users (
  userid INT NOT NULL,
  email VARCHAR(64) NOT NULL,
  password VARCHAR(64) NOT NULL,
  firstname VARCHAR(32) NOT NULL,
  lastname VARCHAR(32) NOT NULL,
  groupid INT NOT NULL,
  company VARCHAR(128),
  phone VARCHAR(32) NOT NULL,
  address VARCHAR(512) NOT NULL,
  country VARCHAR(32) NOT NULL,
  industry VARCHAR(64),
  edutype VARCHAR(64),
  trialorder TINYINT(1),
  planneduse VARCHAR(512),
  blogallowed TINYINT(1),
  PRIMARY KEY (userid)
);

CREATE TABLE usercount (
  userid INT NOT NULL
);

CREATE TABLE siteadmins (
  userid INT NOT NULL,
  PRIMARY KEY (userid),
  FOREIGN KEY (userid) REFERENCES users (userid)
);

INSERT INTO users (userid, email, password, firstname, lastname, groupid,
  company, phone, address, country)
  VALUES (1, 'tester@coreform.com', '$2a$10$3Rtmdub0/g1Sp1TXv85/yub7JUCkEE5xrKQqAuRZVWrcmvfD1vppa', 'tester', 'tester', 0,
  'coreform', ' ', ' ', 'USA');
INSERT INTO usercount (userid) VALUES (1);
INSERT INTO siteadmins (userid) VALUES (1);

CREATE TABLE distributorcount (
  groupid INT NOT NULL
);

CREATE TABLE distributors (
  userid INT NOT NULL,
  groupid INT NOT NULL,
  folder INT NOT NULL,
  reseller INT NOT NULL,
  PRIMARY KEY (userid),
  FOREIGN KEY (userid) REFERENCES users (userid)
);

CREATE TABLE downloadpath (
  directory VARCHAR(64) NOT NULL,
  PRIMARY KEY (directory)
);

CREATE TABLE productcount (
  productid INT NOT NULL
);

CREATE TABLE products (
  productid INT NOT NULL,
  product VARCHAR(32) NOT NULL,
  location VARCHAR(128) NOT NULL,
  parentid INT NOT NULL,
  depth INT NOT NULL,
  folder INT NOT NULL,
  processid INT,
  releasedate DATE,
  PRIMARY KEY (productid)
);

CREATE TABLE productpaths (
  productid INT NOT NULL,
  pathorder INT NOT NULL,
  parentid INT NOT NULL
);

CREATE TABLE featurecount (
  featureid INT NOT NULL
);

CREATE TABLE features (
  featureid INT NOT NULL,
  featurename VARCHAR(64) NOT NULL,
  featurekey VARCHAR(32) NOT NULL,
  PRIMARY KEY (featureid)
);

CREATE TABLE optioncount (
  optionid INT NOT NULL
);

CREATE TABLE options (
  optionid INT NOT NULL,
  optionname VARCHAR(64) NOT NULL,
  optionkey VARCHAR(32) NOT NULL,
  PRIMARY KEY (optionid)
);

CREATE TABLE platformcount (
  platformid INT NOT NULL
);

CREATE TABLE platforms (
  platformid INT NOT NULL,
  platformname VARCHAR(32) NOT NULL,
  PRIMARY KEY (platformid)
);

CREATE TABLE licensecount (
  licenseid INT NOT NULL
);

CREATE TABLE licenseproducts (
  licenseid INT NOT NULL,
  licensename VARCHAR(64) NOT NULL,
  version VARCHAR(16) NOT NULL,
  allowedinst INT NOT NULL,
  processid INT,
  PRIMARY KEY (licenseid)
);

CREATE TABLE licensefeatures (
  featureid INT NOT NULL,
  licenseid INT NOT NULL,
  autoadd INT NOT NULL
);

CREATE TABLE licenseoptions (
  optionid INT NOT NULL,
  licenseid INT NOT NULL,
  autoadd INT NOT NULL
);

CREATE TABLE platformdownloads (
  platformid INT NOT NULL,
  licenseid INT NOT NULL,
  productid INT NOT NULL,
  autoadd INT NOT NULL
);

CREATE TABLE upgradecount (
  upgradeid INT NOT NULL
);

CREATE TABLE upgradepaths (
  upgradeid INT NOT NULL,
  upgradename VARCHAR(64) NOT NULL,
  PRIMARY KEY (upgradeid)
);

CREATE TABLE upgradeitems (
  upgradeid INT NOT NULL,
  licenseid INT NOT NULL,
  upgradeorder INT NOT NULL
);

CREATE TABLE licensegroupcount (
  groupid INT NOT NULL
);

CREATE TABLE licensegroups (
  groupid INT NOT NULL,
  groupname VARCHAR(64) NOT NULL,
  PRIMARY KEY (groupid)
);

CREATE TABLE openproducts (
  productid INT NOT NULL,
  PRIMARY KEY (productid),
  FOREIGN KEY (productid) REFERENCES products (productid)
);

CREATE TABLE orderprocesscount (
  processid INT NOT NULL
);

CREATE TABLE orderprocess (
  processid INT NOT NULL,
  processname VARCHAR(64) NOT NULL,
  handlername VARCHAR(128) NOT NULL,
  PRIMARY KEY (processid)
);

CREATE TABLE ordercount (
  orderid INT NOT NULL
);

CREATE TABLE userorders (
  orderid INT NOT NULL,
  orderdate DATE NOT NULL,
  userid INT NOT NULL,
  processid INT NOT NULL,
  processstep INT NOT NULL,
  ordernumber VARCHAR(32),
  distnumber VARCHAR(64),
  srcnumber VARCHAR(32),
  PRIMARY KEY (orderid)
);

CREATE TABLE orderproducts (
  orderid INT NOT NULL,
  productid INT NOT NULL
);

CREATE TABLE orderlicenses (
  orderid INT NOT NULL,
  licenseid INT NOT NULL
);

CREATE TABLE userdownloads (
  userid INT NOT NULL,
  productid INT NOT NULL
);

CREATE TABLE userlicensecount (
  userlicenseid INT NOT NULL
);

CREATE TABLE licenses (
  userlicenseid INT NOT NULL,
  userid INT NOT NULL,
  licenseid INT NOT NULL,
  licensetype INT NOT NULL,
  licensekey VARCHAR(32) NOT NULL,
  quantity INT NOT NULL,
  allowedinst INT NOT NULL,
  vmallowed INT NOT NULL,
  rdallowed INT NOT NULL,
  deactivations INT NOT NULL,
  maxdeactivations INT NOT NULL,
  numdays INT NOT NULL,
  expiration DATE,
  startdate DATE,
  upgradeid INT NOT NULL,
  upgradedate DATE,
  version VARCHAR(16),
  PRIMARY KEY (userlicenseid)
);

CREATE TABLE userplatforms (
  userlicenseid INT NOT NULL,
  platformid INT NOT NULL
);

CREATE TABLE userfeatures (
  userlicenseid INT NOT NULL,
  featureid INT NOT NULL
);

CREATE TABLE useroptions (
  userlicenseid INT NOT NULL,
  optionid INT NOT NULL
);

CREATE TABLE activationcount (
  activationid INT NOT NULL
);

CREATE TABLE licenseactivations (
  activationid INT NOT NULL,
  userlicenseid INT NOT NULL,
  hostid VARCHAR(64) NOT NULL,
  hostname VARCHAR(64) NOT NULL
);

CREATE TABLE activationfeatures (
  activationid INT NOT NULL,
  featureid INT NOT NULL,
  featurechk VARCHAR(64) NOT NULL,
  featuresig VARCHAR(512) NOT NULL
);

CREATE TABLE vmsign (
  licenseid INT NOT NULL,
  featurechk VARCHAR(64) NOT NULL,
  featuresig VARCHAR(512) NOT NULL
);

CREATE TABLE distributorgroups (
  userid INT NOT NULL,
  groupid INT NOT NULL
);

CREATE TABLE distributorlicenses (
  userid INT NOT NULL,
  licenseid INT NOT NULL
);

CREATE TABLE distributorupgrades (
  userid INT NOT NULL,
  upgradeid INT NOT NULL
);

CREATE TABLE distributoremails (
  userid INT NOT NULL,
  emailtype INT NOT NULL,
  productid INT NOT NULL
);

CREATE TABLE processlicense (
  processid INT NOT NULL,
  productid INT,
  PRIMARY KEY (processid),
  FOREIGN KEY (processid) REFERENCES orderprocess (processid)
);

CREATE TABLE userapprovals (
  userid INT NOT NULL,
  productid INT NOT NULL,
  approval INT NOT NULL
);

CREATE TABLE licenseapprovals (
  userid INT NOT NULL,
  licenseid INT NOT NULL,
  approval INT NOT NULL
);

CREATE TABLE universitycount (
  universityid INT NOT NULL
);

CREATE TABLE universities (
  universityid INT NOT NULL,
  university VARCHAR(128) NOT NULL,
  fax VARCHAR(32),
  adminid INT NOT NULL,
  techid INT NOT NULL,
  PRIMARY KEY (universityid)
);

CREATE TABLE contacts (
  contactid INT NOT NULL,
  email VARCHAR(64) NOT NULL,
  firstname VARCHAR(32) NOT NULL,
  lastname VARCHAR(32) NOT NULL,
  phone VARCHAR(32),
  address VARCHAR(512),
  country VARCHAR(32) NOT NULL,
  PRIMARY KEY (contactid)
);

CREATE TABLE login_tokens(
  token VARCHAR(64),
  userid INT
);
