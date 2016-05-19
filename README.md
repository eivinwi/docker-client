## Docker Client

This is a client application with __ used for our Docker workshop. The workshop ~~probably~~
won't make ~~much~~ any sense unless you speak Norwegian, so all the instructions will be in Norwegian.


### Oppgavene
Oppgavene vil være beskrevet på en nettside der server-applikasjonen kjører.
Noen av oppgavene vil gjøres utelukkende i Docker, mens noen bruker filer fra
dette prosjektet (se mappene 'oppgave_1' osv).


### Java-applikasjonen
Prosjektet inneholder en Java-applikasjon under 'src/main/java/no/kantega/dockerworkshop'.
Denne kan bygges og kjøres med kommandoene:
```
mvn clean install
java -jar target/docker-client-1.0-SNAPSHOT.jar
```

Applikasjonen starter opp spring, leser konfigurasjonsfilen 'team.properties'
og starter opp et endpoint '/test'. For å teste at applikasjonen kjører kan man
gå til [http://localhost/8080:test](http://localhost/8080:test).
Merk at applikasjonen vil feile dersom noe allerede kjører på 8080
(kan byttes ved å sende med '-Dserver.port=<port>' som parameter).


### Server-applikasjonen
**Merk: denne trengs ikke å kjøres på workshoppen, den kjører allerede på en URL som vil bli utdelt.**

Prosjektet inneholder også en server-applikasjon som hver av deltakerene på workshoppen skal bruke,
[denne er beskrevet her](https://github.com/eivinwi/docker-rest-service).





