# Introduction

## Maven dependencies
Find maven dependencies by using [https://repository.sonatype.org/](https://repository.sonatype.org/)

## Github
See [Git Branching Basic Branching and Merging](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging)
for usefull documentation about using branches/merges in git.

## Maven assembly
[Example maven assembly](http://www.mkyong.com/maven/create-a-fat-jar-file-maven-assembly-plugin/)

## Slf4J
[Documentation](http://www.slf4j.org/manual.html)

## Json
See [Json](http://www.mkyong.com/java/how-to-enable-pretty-print-json-output-jackson/)

## Cucumber
See [Cucumber](Cucumber.md)

## SSL connection
Import certificates into keystore proxykeystore.jks
Make sure the proxykeystore.jks is at the root of the class path (for now )

for details see [Certificates](Certificates.md)
```
keytool -importcert -file certificate.cer -keystore keystore.jks -alias "Alias"
```
The content of the keystore is
```
keytool -keystore proxykeystore.jks -list
Enter keystore password:

Keystore type: JKS
Keystore provider: SUN

Your keystore contains 3 entries

trial pkioverheid test root ca - g2, Sep 26, 2015, trustedCertEntry,
Certificate fingerprint (SHA1): FB:C4:7C:0B:BC:87:73:14:43:D2:DB:46:9D:B6:98:F6:AF:2A:9D:DE
kadasterca, Sep 24, 2015, trustedCertEntry,
Certificate fingerprint (SHA1): 8E:8C:08:4F:3E:AE:65:78:01:E0:77:5B:5B:F8:34:58:DF:F0:A2:69
www-proxy.cs.kadaster.nl, Sep 24, 2015, trustedCertEntry,
Certificate fingerprint (SHA1): 5B:90:99:30:C0:29:0C:34:59:BD:8D:63:D9:2B:B4:1C:CC:F8:46:30

```


## References
### Springboot - package hello
+ Documentation found on [Spring boot RestTemplate](http://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html)
+ Springboot example [Consuming rest services](https://spring.io/guides/gs/consuming-rest/)
+ Example basic authentication [Resttemplate with basic authentication in spring-3-1](http://www.baeldung.com/2012/04/16/how-to-use-resttemplate-with-basic-authentication-in-spring-3-1/)
+ Example [project configuration with spring](http://www.baeldung.com/2012/03/12/project-configuration-with-spring/)

### Markdown
+ Documentation [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)

### Rest
+ httpclient components [Documentation](https://hc.apache.org/)
+ sonatype nexus [Search maven dependencies](https://repository.sonatype.org/)

### Icons
+ Icons downloaded from [http://iconiza.com/downloads.html](http://iconiza.com/downloads.html)
