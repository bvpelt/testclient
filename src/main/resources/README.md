# Introduction

## Maven dependencies
Find maven dependencies by using [https://repository.sonatype.org/](https://repository.sonatype.org/)

## Maven assembly
[http://www.mkyong.com/maven/create-a-fat-jar-file-maven-assembly-plugin/](http://www.mkyong.com/maven/create-a-fat-jar-file-maven-assembly-plugin/)

## Slf4J
[http://www.slf4j.org/manual.html](http://www.slf4j.org/manual.html)

## SSL connection
Import certificates into keystore proxykeystore.jks
Make sure the proxykeystore.jks is at the root of the class path (for now )

```
keytool -importcert -file certificate.cer -keystore keystore.jks -alias "Alias"
```
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
```

## References
### Springboot - package hello
+ Documentation found on [http://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html](http://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html)
+ Springboot example [https://spring.io/guides/gs/consuming-rest/](https://spring.io/guides/gs/consuming-rest/)
+ for basic authentication [http://www.baeldung.com/2012/04/16/how-to-use-resttemplate-with-basic-authentication-in-spring-3-1/](http://www.baeldung.com/2012/04/16/how-to-use-resttemplate-with-basic-authentication-in-spring-3-1/)
+ Example [http://www.baeldung.com/2012/03/12/project-configuration-with-spring/](http://www.baeldung.com/2012/03/12/project-configuration-with-spring/)

### Markdown
+ Documentation [https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#tables](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#tables)

### Rest
+ httpclient components [https://hc.apache.org/](https://hc.apache.org/)
+ sonatype nexus [https://repository.sonatype.org/](https://repository.sonatype.org/)
