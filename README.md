# PLD AGILE INSA 4IF : OPTIMOD'LYON

Bienvenue dans le projet **Optimod'Lyon**, une application web conçue pour
optimiser la gestion des livraisons dans Lyon. Ce projet est divisé en deux
parties :

1. **Frontend** - développé avec **React** et **Next.js**.
2. **Backend** - développé en **Java** pour gérer les données, effectuer les
   calculs de parcours de graphe et exposer une API REST.

## **Fonctionnalités principales**

-   Visualisation des cartes et des tournées de livraison.
-   Gestion des entrepôts et des points de collecte/livraison.
-   Calculs optimisés pour les tournées des livreurs grâce à un algorithme TSP.
-   API REST pour la communication entre le frontend et le backend.

## **Installation et lancement**

### **Prérequis**

-   **Backend** :
    -   Java 11
    -   Maven 2.3
    -   Apache Tomcat 9.0.97
-   **Frontend** :
    -   Node.js 15
    -   React 19
    -   npm ou yarn
-   **Prérequis supplémentaires**
    -   Clé API Google Maps
    -   Il est fortement conseillé de build et run le backend avec NetBeans 12.6

### **Sauvegarde de la clé API**

1. Créer un fichier .env à la racine du projet et y copier le contenu de
   .env.template en y remplaçant la clé API
2. sauver le document

### **Lancement du Backend**

1. Compilez le projet `optimodlyon`
2. Compilez le projet `optimodapi`
3. Lancer `optimodapi`

### **Lancement du Frontend**

1. Rendez-vous dans le dossier `frontend` :

    ```bash
    cd frontend
    ```

2. Installez les modules Node requis :

    ```bash
    npm install
    # ou
    yarn install
    # ou
    pnpm install
    ```

3. Lancez le serveur de développement :
    ```bash
    npm run dev
    # ou
    yarn dev
    # ou
    pnpm dev
    # ou
    bun dev
    ```

Ouvrez http://localhost:3000 avec votre navigateur pour voir le résultat.

## **Contribution**

Les contributions sont les bienvenues ! Comment contribuer ?

1.  Forkez ce dépôt.
2.  Setup git flow :

    ```bash
    git flow init -d
    ```

3.  Démarrer une fonctionnalité Pour commencer à travailler sur une nouvelle
    fonctionnalité :

    ```bash
    git flow feature start nom_de_la_fonctionnalité
    ```

4.  Poussez vos modifications vers votre dépôt forké :

    ```bash
    git push origin feature/nom-fonctionnalite
    ```

5.  Ouvrez une Pull Request depuis GitHub.

Plus de détails dans le fichier [CONTRIBUTING](CONTRIBUTING.md).

## **Licence**

Ce projet est sous licence MIT. Consultez le fichier [LICENCE](LICENSE) pour
plus de détails.
