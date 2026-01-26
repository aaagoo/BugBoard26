# BugBoard26
Questo progetto nasce come esercitazione universitaria per il corso di Ingegneria Del Software (CdL Triennale in Informatica – A.A. 2025/2026). L’obiettivo è lo sviluppo del sistema BugBoard26.

---

## Svolto da:
- Agostino Sorrentino . N86005123
- Mariateresa Principato . N86005284

---

## Descrizione del Progetto
BugBoard26 è un sistema per la gestione di issue, implementato con un'architettura Client-Server. Il frontend è sviluppato in Java Swing, con un'interfaccia utente desktop nativa, mentre il backend è basato su Spring Boot, che fornisce un'API RESTful per la gestione della logica di business e persistenza di dati.
Il database utilizzato è PostgreSQL, ospitato su Supabase.
## Installazione e Avvio
Per eseguire BugBoard26, assicurati di avere i seguenti requisiti:
- **Java Runtime Environment (JRE):** Versione 21 o superiore;
- **Connessione Internet:** Necessaria per accedere al database.
### Attenzione:
Il sistema è preimpostato con un account Amministratore già esistente con dati:
- username: admin
- password: admin  

Una volta eseguito l'accesso al sistema, è possibile creare nuove utenze tramite questo account (o altri account Amministratore).
## Avvio dell'Applicazione
L'applicazione è un singolo file eseguibile JAR, che include sia il frontend Swing che il backend Spring Boot.
1. Scarica il file "BugBoard26.jar" dalla cartella "out/artifacts/BugBoard26_jar";
2. Apri il file con un doppio click.
## Funzionalità Principali
* **Autenticazione Utente:** Login sicuro con gestione dei ruoli (Utente, Amministratore).
* **Gestione Account:** Creazione, modifica ed eliminazione di account utente (riservato agli Amministratori).
* **Creazione Issue:** Apertura di nuove segnalazioni con titolo, descrizione, priorità, tipo, allegato, assegnatario.
* **Allegati Immagine:** possibilità di allegare immagini alle issue tramite upload su storage cloud.
* **Visualizzazione, Modifica e Eliminazione Issue:** Dettagli completi delle issue, con possibilità di modificare, risolvere ed eliminare issue (a seconda del ruolo e dell'appartenenza della issue).
* **Filtro e Ricerca:** Funzionalità per filtrare le issue secondo vari parametri.
* **Notifiche**: Sistema di notifica per assegnazioni e aggiornamenti sulle issue.
* **Assegnazione Automatica:** Algoritmo per l'assegnazione automatica delle issue all'utente col minor carico di lavoro
## Tecnologie Utilizzate
* **Frontend:** Java Swing, Flatlaf.
* **Backend:** Spring Boot (Java), RESTful API.
* **Database:** PostgreSQL (ospitato su Supabase).
* **Autenticazione:** JWT (JSON Web Tokens) per la gestione delle sessioni.
* **Build Tool.** Apache Maven.