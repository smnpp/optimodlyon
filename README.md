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

-   **Frontend** :

    -   Node.js 15
    -   React 19

-   **Backend** :
    -   Java 11
    -   Maven pour gérer les dépendances.

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

### **Lancement du Backend**

1. Rendez-vous dans le dossier backend :

    ```bash
    cd backend
    ```

2. Compilez le projet avec Maven :

    ```bash
    mvn clean install
    ```

## **Contribution**

Les contributions sont les bienvenues ! Comment contribuer ?

1.  Forkez ce dépôt.
2.  Setup git flow : <<<<<<< HEAD

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
