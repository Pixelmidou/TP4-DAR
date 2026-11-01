# Communication client/serveur UDP — TP4

## Description

Ce projet présente deux étapes de communication réseau en Java avec des
sockets **UDP** :

- **Etape 4-2** : échange simple d'un message entre un client et un serveur ;
- **Etape 4-3** : chat multi-threadé permettant à plusieurs clients et au
  serveur d'envoyer des messages.

## Structure du projet

```
src/
├── clientPackage/
│   ├── Client_4_2.java   → envoie un message au serveur
│   └── Client_4_3.java   → envoie et reçoit des messages de chat
└── serverPackage/
    ├── Server_4_2.java   → reçoit et affiche un message
    └── Server_4_3.java   → reçoit, envoie et diffuse les messages
```

## Etape 4-2 — Communication UDP simple

### Fonctionnement

Le client crée une socket UDP et envoie le message `"Mon message"` au serveur
sur le port `1234`. Le serveur attend un datagramme, récupère son contenu et
l'affiche dans la console.

Cette étape utilise une communication dans un seul sens :

```text
Client ───────── message UDP ────────> Serveur
```

### Protocole d'échange

| Étape | Sens | Type | Contenu |
|---|---|---|---|
| 1 | Client → Serveur | Datagramme UDP | `"Mon message"` |
| 2 | Serveur | Affichage console | message reçu |

### Exécution

Compiler les deux classes :

```bash
javac -d out src/serverPackage/Server_4_2.java src/clientPackage/Client_4_2.java
```

Lancer d'abord le serveur :

```bash
java -cp out serverPackage.Server_4_2
```

Puis lancer le client dans un autre terminal :

```bash
java -cp out clientPackage.Client_4_2
```

Exemple de sortie du serveur :

```text
Lancement du serveur
Donnees reçues = Mon message
```

## Etape 4-3 — Chat UDP multi-threadé

### Fonctionnement

Le serveur et chaque client utilisent deux threads indépendants :

- un thread de réception ;
- un thread d'envoi.

Les méthodes de réception et d'envoi sont définies directement dans les
classes `Server_4_3` et `Client_4_3`.

Le serveur :

- écoute sur le port UDP `1234` ;
- enregistre les clients avec le message `/join` ;
- reçoit les messages des clients ;
- diffuse chaque message reçu à tous les clients enregistrés ;
- peut envoyer ses propres messages à tous les clients ;
- retire un client avec le message `/quit`.

Le client :

- utilise un port UDP attribué automatiquement ;
- se connecte au serveur avec `/join` ;
- envoie plusieurs messages saisis avec `Scanner` ;
- affiche les messages reçus dans un thread séparé ;
- se déconnecte avec `/quit`.

### Protocole d'échange

| Étape | Sens | Type | Contenu |
|---|---|---|---|
| 1 | Client → Serveur | Datagramme UDP | `/join` |
| 2 | Client → Serveur | Datagramme UDP | message de discussion |
| 3 | Serveur → Clients | Datagramme UDP | message diffusé |
| 4 | Client → Serveur | Datagramme UDP | `/quit` |

Les messages sont encodés en UTF-8.

### Exécution

Compiler les deux classes :

```bash
javac -d out src/serverPackage/Server_4_3.java src/clientPackage/Client_4_3.java
```

Lancer le serveur :

```bash
java -cp out serverPackage.Server_4_3
```

Lancer plusieurs clients dans des terminaux différents :

```bash
java -cp out clientPackage.Client_4_3
```

Pour tester la diffusion, lancer au moins deux clients puis écrire un message
dans la console du serveur. Le message doit apparaître dans les consoles de
tous les clients connectés.

### Exemple

Console du serveur :

```text
Lancement du serveur
Ecrivez un message pour le diffuser aux clients.
Client 127.0.0.1:53120 : Bonjour serveur
```

Console d'un client :

```text
Connecte au serveur. Ecrivez vos messages :
Client 127.0.0.1:53120 : Bonjour serveur
Serveur : Bienvenue dans le chat
```

## Concepts abordés

- communication UDP avec `DatagramSocket` et `DatagramPacket` ;
- conversion d'un message en tableau de bytes ;
- création de threads avec `Thread` ;
- envoi et réception simultanés ;
- diffusion d'un message à plusieurs clients ;
- synchronisation de la liste des clients ;
- lecture des messages avec `Scanner`.

## Environnement

- JDK 8 ou version supérieure ;
- IntelliJ IDEA, Eclipse ou VS Code ;
- Windows, Linux ou macOS.
