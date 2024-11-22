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
    concise.
-   **En cas de problème avec un hook** : Vérifiez vos modifications et
    assurez-vous qu’elles respectent les règles définies.
