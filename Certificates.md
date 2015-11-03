# Certificates

In order for the testclient to work fine there are a few things to take into consideration.
+ The proxy uses ssl termination. In order to trust the proxy the root certificate kadasterca
must be inserted into a trust store
+ The test.geodatastore.pdok.nl url uses a pki test certificate so the trial pkioverheid test root ca - g2
must be inserted into a trust store

This document describes how to fill the truststore. Java uses a keystore.

The password for proxykeystore.jks is geodatastore

```
$ keytool -importcert -file kadasterCA.cer -keystore proxykeystore.jks -alias "KadasterCA"
Enter keystore password:  geodatastore
Re-enter new password: geodatastore
Owner: CN=Kadaster CA, DC=kadaster, DC=local
Issuer: CN=Kadaster CA, DC=kadaster, DC=local
Serial number: 15d50ae9a9f8188f47afef61b4522dad
Valid from: Fri Jan 25 09:40:04 CET 2013 until: Thu Jan 25 09:49:08 CET 2018
Certificate fingerprints:
         MD5:  31:91:79:FE:A6:A7:21:25:0F:DB:48:4A:D8:B9:32:89
         SHA1: 8E:8C:08:4F:3E:AE:65:78:01:E0:77:5B:5B:F8:34:58:DF:F0:A2:69
         SHA256: E0:16:6F:A2:6A:EE:B5:28:30:6F:90:91:77:A8:CF:18:F1:EC:8D:F0:4C:29:3F:7C:69:8F:01:C9:79:25:2D:2B
         Signature algorithm name: SHA1withRSA
         Version: 3

Extensions:

#1: ObjectId: 1.3.6.1.4.1.311.20.2 Criticality=false
0000: 1E 04 00 43 00 41                                  ...C.A


#2: ObjectId: 1.3.6.1.4.1.311.21.1 Criticality=false
0000: 02 01 00                                           ...


#3: ObjectId: 2.5.29.19 Criticality=true
BasicConstraints:[
  CA:true
  PathLen:2147483647
]

#4: ObjectId: 2.5.29.31 Criticality=false
CRLDistributionPoints [
  [DistributionPoint:
     [URIName: ldap:///CN=Kadaster%20CA,CN=CSDC162,CN=CDP,CN=Public%20Key%20Services,CN=Services,CN=Configuration,DC=kadaster,DC=local?certificateRevocationList?base?objectClass=cRLDistributionPoint, URIName: http://csdc162.kadaster.local/CertEnroll/Kadaster%20CA.crl]
]]

#5: ObjectId: 2.5.29.15 Criticality=false
KeyUsage [
  DigitalSignature
  Key_CertSign
  Crl_Sign
]

#6: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: F2 0A AA 0C 78 FC 2A D6   BA B9 56 C1 4E F1 5D 43  ....x.*...V.N.]C
0010: F2 E5 BF 41                                        ...A
]
]

Trust this certificate? [no]:  yes
Certificate was added to keystore
```
```
$ keytool -importcert -file www-proxy.cs.kadaster.nl.cer -keystore proxykeystore.jks -alias "www-proxy.cs.kadaster.nl"
Enter keystore password:  geodatastore
Certificate was added to keystore
```
```
$ keytool -keystore proxykeystore.jks -list
Enter keystore password:  geodatastore

Keystore type: JKS
Keystore provider: SUN

Your keystore contains 2 entries

kadasterca, Sep 24, 2015, trustedCertEntry,
Certificate fingerprint (SHA1): 8E:8C:08:4F:3E:AE:65:78:01:E0:77:5B:5B:F8:34:58:DF:F0:A2:69
www-proxy.cs.kadaster.nl, Sep 24, 2015, trustedCertEntry,
Certificate fingerprint (SHA1): 5B:90:99:30:C0:29:0C:34:59:BD:8D:63:D9:2B:B4:1C:CC:F8:46:30

keytool -keystore proxykeystore.jks -importcert -file "TRIAL PKIoverheid TEST Root CA - G2.pem" -alias "TRIAL PKIoverheid TEST Root CA - G2"
Enter keystore password:
Owner: CN=TRIAL PKIoverheid TEST Root CA - G2, O=PKIoverheid TEST, C=NL
Issuer: CN=TRIAL PKIoverheid TEST Root CA - G2, O=PKIoverheid TEST, C=NL
Serial number: 13105f1
Valid from: Wed Oct 29 13:38:44 CET 2008 until: Wed Mar 25 14:27:19 CET 2020
Certificate fingerprints:
	 MD5:  A2:E0:74:A3:EE:4F:9D:B3:B9:12:A9:5A:E6:B0:CD:26
	 SHA1: FB:C4:7C:0B:BC:87:73:14:43:D2:DB:46:9D:B6:98:F6:AF:2A:9D:DE
	 SHA256: D9:CC:6A:E1:7D:85:9A:CF:82:C3:CC:E8:45:00:31:90:89:3B:4D:36:DB:E6:F9:12:86:34:31:AB:01:6C:2C:78
	 Signature algorithm name: SHA256withRSA
	 Version: 3

Extensions:

#1: ObjectId: 2.5.29.19 Criticality=true
BasicConstraints:[
  CA:true
  PathLen:2147483647
]

#2: ObjectId: 2.5.29.32 Criticality=false
CertificatePolicies [
  [CertificatePolicyId: [2.5.29.32.0]
[PolicyQualifierInfo: [
  qualifierID: 1.3.6.1.5.5.7.2.1
  qualifier: 0000: 16 35 68 74 74 70 3A 2F   2F 77 77 77 2E 70 6B 69  .5http://www.pki
0010: 6F 76 65 72 68 65 69 64   2E 6E 6C 2F 70 6F 6C 69  overheid.nl/poli
0020: 63 69 65 73 2F 54 45 53   54 72 6F 6F 74 2D 70 6F  cies/TESTroot-po
0030: 6C 69 63 79 2D 47 32                               licy-G2

]]  ]
]

#3: ObjectId: 2.5.29.15 Criticality=true
KeyUsage [
  Key_CertSign
  Crl_Sign
]

#4: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 11 56 07 49 A3 36 0B CF   99 8D F7 C7 04 94 F3 9B  .V.I.6..........
0010: 06 A9 EE 79                                        ...y
]
]

Trust this certificate? [no]:  yes
Certificate was added to keystore

```
