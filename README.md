# **Projet Réseau 2022-2023: Implémentation du protocole Redis en JAVA**

## Projet Crée par: 

- **Zamouri Mohamed Rayène** étudiant en M1 MIAGE.

## Description du protocole Redis: 

- Le protocole Redis est un protocole de communication simple basé sur du texte. Il utilise une approche de requête-réponse, où les clients envoient des commandes au serveur Redis et reçoivent des réponses correspondantes.

- Le protocole Redis est orienté ligne. Chaque commande ou réponse est envoyée sur une ligne distincte. Les lignes sont délimitées par des caractères de fin de ligne, généralement représentés par la séquence de caractères "\r\n".

- Les commandes Redis sont constituées de plusieurs parties : un identifiant de commande, suivi de zéro, un ou plusieurs arguments. L'identifiant de commande est une chaîne de caractères en majuscules qui indique l'opération à effectuer, telle que GET, SET, HGETALL, etc. Les arguments sont les valeurs spécifiques à la commande, telles que les clés, les paramètres optionnels, etc. Les arguments sont également représentés sous forme de chaînes de caractères.

- Les réponses Redis suivent également une structure spécifique. Une réponse peut être un simple string (chaîne de caractères), un entier, un bulk string (chaîne en bloc), un tableau (array) ou un autre type spécifique, en fonction de la commande exécutée et de son résultat.

- Le protocole Redis prend également en charge des fonctionnalités avancées telles que la réplication maître-esclave. Dans ce cas, les serveurs Redis sont configurés pour former un cluster, où un serveur maître accepte les écritures et les serveurs esclaves répliquent les données du maître. Les serveurs communiquent en utilisant le protocole Redis pour assurer une cohérence des données.

- En résumé, le protocole Redis est un protocole de communication basé sur du texte, orienté ligne, utilisé pour envoyer des commandes au serveur Redis et recevoir des réponses. Il offre une structure de requête-réponse simple et prend en charge une variété de types de données et de fonctionnalités avancées pour la gestion des bases de données clé-valeur en mémoire.

## Description du Projet: 

### Introduction: 

Ce projet est réalisé dans le cadre du module Réseau pour les M1 MIAGE.

Le but de ce projet est de créer un Serveur en JAVA implémentant le protocole REDIS.

Ce Projet a été testé avec le client Redis officiel que vous pouvez télécharger [ici](https://github.com/microsoftarchive/redis/releases/tag/win-3.0.504). 

Voici les commandes Redis implémentées dans le serveur :

- `SET key value` : Définit la valeur de la clé spécifiée.
- `SETNX key value` : Définit la valeur de la clé spécifiée si elle n'existe pas déjà.
- `GET key` : Récupère la valeur associée à la clé spécifiée.
- `STRLEN key` : Renvoie la longueur de la valeur de la clé spécifiée.
- `APPEND key "chaîne à concaténer"` : Concatène la chaîne spécifiée à la valeur existante de la clé.
- `INCR key` : Incrémente la valeur numérique de la clé.
- `DECR key` : Décrémente la valeur numérique de la clé.
- `EXISTS key` : Vérifie si la clé spécifiée existe.
- `DEL key` : Supprime la clé spécifiée.
- `EXPIRE key duration` : Définit une durée d'expiration pour la clé spécifiée.
 
### Conception: 

Aucours du projet on a utiliseé principalement la structure de données `Map`, c'est une structure qui permet de stocker des données dans des paires <clé,valeur> ce qui a permis de réplique le fonctionnement du protocole Redis. 

On a créé 4 classes: 

- `ServeurRedis`: représentant le serveur implémentant notre protocole, il peut être soit maitre ou esclave. Les serveurs esclaves n'ont droit qu'à trouver les données cependant les maitres peuvent trouver , ajouter ou modifier des données.
- `ClientRedis`: représentant le client qui se connecte au serveur. Il traite et gère les commandes.
- `DonneesRedis`: représentant les structures de données dans lesquels sont stockées les informations entrées par les clients.
- `Utils`: une classe qui contient des fonctions Utilitaires nécessaire au fonctionnement du programme: principlament les fonctions qui permettent d'afficher le texte dans le format voulu. 
  
### Comment lancer le programme :

Dans le dossier de distribution binaire `bindist` il y a un dossier à l'intérieur appelé `bin` dans lequel il y a un fichier exécutable `run.bat` qui permet de lancer le programme. 

### Comment utiliser le programme: 

Le programme utilise des commandes afin de donner des instructions. 

Voici la liste des commandes: 

- `ADD MASTER` : Ajoute un serveur en tant que maître.
- `ADD SLAVE` : Ajoute un serveur en tant qu'esclave.
- `DELETE MASTER` : Supprime un serveur maître.
- `DELETE SLAVE` : Supprime un serveur esclave.
- `QUIT` : Quitte le programme ou la connexion.

Lors de la supression d'un serveur maitre et un serveur esclave existe, le serveur esclave prend le relais et devient un serveur maitre. 

### Difficultés rencontrées au cours du projet: 

la principale difficulté rencontré au cours du projet est l'implémentation de l'architecture maitre esclave. La prise de relais a posé problème surtout comment remplacer le serveur maitre par l'esclave tout en gardant l'intégrité des données. 

La seconde difficulté recontré est de préserver l'intégrité des données au cours de l'utilisation multiple du serveur par plusieurs clients. 
Problème résolu par rendre les méthodes liées à la modification des structures de données synchrones afin de prévenir d'altérer l'intégrité des données. 
