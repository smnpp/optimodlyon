## Git Flow

Utiliser Git Flow est une excellente manière d’automatiser le workflow. Pour installer l’extension Git Flow, vous pouvez utiliser la commande suivante sur un système basé sur Linux :

```
sudo apt-get install git-flow
```

### Setup git flow

```
git flow init -d
```

### Démarrer une fonctionnalité
Pour commencer à travailler sur une nouvelle fonctionnalité :
```
git flow feature start nom_de_la_fonctionnalité
```

### Terminer une fonctionnalité
Pour finaliser une fonctionnalité :
```
git flow feature finish nom_de_la_fonctionnalité
```

Cela est beaucoup plus pratique que de devoir faire ceci pour créer une branche de fonctionnalité :
```
git checkout develop
git checkout -b nom_de_la_fonctionnalité
```

Pour plus d'informations, consultez le [guide d'Atlassian](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) sur Git Flow.

## Installation des hooks partagés

Pour garantir une cohérence dans le développement, ce projet utilise des hooks
Git partagés. Voici les étapes à suivre après avoir cloné le repository :

### **Étape 1 : Installer les hooks**

Exécutez le script suivant dans le terminal, à la racine du projet :

```bash
./setup.sh
```

Cela configure automatiquement Git pour utiliser les hooks partagés situés dans
le dossier `.githooks`.

---

### **Vérifications automatiques**

1. **Avant chaque commit** :

    - Le hook `prepare-commit-msg` s’assure que votre message suit les
      conventions des commits, comme `feat: nouvelle fonctionnalité` ou
      `fix: correction de bug`.
    - Si le message ne respecte pas ces conventions, le commit sera bloqué.

2. **Avant chaque push** :
    - Le hook `pre-push` exécute automatiquement des vérifications de code (par
      exemple, le linting). Si une erreur est détectée, le push sera annulé.

---

### **Bonnes pratiques**

-   **Ne contournez pas les hooks** : Ils sont là pour garantir la qualité et la
    cohérence du projet.
-   **Respectez les conventions des commits** : Utilisez des préfixes comme
    `feat`, `fix`, `docs`, `style`, `refactor`, etc., suivis d’une description
    concise. Se référer aux
    [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
-   **En cas de problème avec un hook** : Vérifiez vos modifications et
    assurez-vous qu’elles respectent les règles définies.
