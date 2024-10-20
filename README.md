# Application Android de Gestion des Étudiants

Cette application Android est conçue pour gérer les informations des étudiants. Elle permet aux utilisateurs d'ajouter de nouveaux étudiants, de visualiser une liste d'étudiants et d'effectuer diverses opérations telles que la mise à jour et la suppression des dossiers d'étudiants.

## Fonctionnalités

1. Ajouter de nouveaux étudiants : Les utilisateurs peuvent ajouter de nouveaux étudiants avec des détails tels que le nom, le prénom, la ville, le sexe et la photo de profil.
2. Voir la liste des étudiants : L'application affiche une liste de tous les étudiants avec leurs informations de base.
3. Fonctionnalité de recherche : Les utilisateurs peuvent rechercher des étudiants par nom ou filtrer par sexe.
4. Modifier les informations des étudiants : L'application permet de mettre à jour les détails des étudiants.
5. Supprimer des étudiants : Les utilisateurs peuvent supprimer des dossiers d'étudiants du système.
6. Tiroir de navigation : Permet une navigation facile entre les différentes sections de l'application.

## Détails techniques

- Communication backend : L'application utilise la bibliothèque Volley pour communiquer avec un backend Spring Boot.
- Gestion des images : CircleImageView est utilisé pour afficher les photos de profil, et l'application peut sélectionner des images depuis la galerie de l'appareil.
- Persistance des données : Les données des étudiants sont stockées et récupérées depuis un serveur via des appels API REST.
- Composants UI : L'application utilise divers composants UI Android tels que ListView, SearchView, AlertDialog et DrawerLayout.

## Activités

1. MainActivity : Gère l'ajout de nouveaux étudiants.
2. ListEtudiantActivity : Affiche la liste des étudiants et gère les opérations comme la recherche, la modification et la suppression.

## Configuration

1. Assurez-vous d'avoir Android Studio installé.
2. Clonez le dépôt ou téléchargez le code source.
3. Ouvrez le projet dans Android Studio.
4. Mettez à jour les URLs du serveur dans MainActivity.java et ListEtudiantActivity.java pour pointer vers votre serveur backend.
5. Exécutez l'application sur un émulateur ou un appareil physique.

## Dépendances

- Volley : Pour les requêtes réseau
- Gson : Pour l'analyse JSON
- CircleImageView : Pour les images de profil circulaires

## Note

Cette application suppose qu'un serveur backend est configuré pour gérer les opérations CRUD pour les données des étudiants. Assurez-vous que votre serveur est correctement configuré et accessible avant d'exécuter l'application.
## Screen Video

https://github.com/user-attachments/assets/bb8ecc96-2cfd-4aad-b412-3b478b74d266

