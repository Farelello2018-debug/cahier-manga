# Matchs TV

Application pour **Android TV / Google TV** (et Fire TV) qui lit des chaînes à partir d'une playlist **M3U**.

- Colle l'adresse de ta playlist M3U (celle de ton opérateur ou de ton abonnement), puis **Charger**.
- Ou choisis un pays africain : l'app charge la liste publique des chaînes **gratuites** de ce pays (projet [iptv-org](https://github.com/iptv-org/iptv)).
- Cherche une chaîne, sélectionne-la avec la télécommande, puis **OK**.
- Pendant la lecture : **▲ / ▼** (ou CH+ / CH−) pour changer de chaîne, **OK** pour afficher le nom, **Retour** pour revenir à la liste.

Les chaînes payantes (Canal+, beIN Sports, SuperSport…) ne sont pas incluses : il faut leur abonnement officiel.
Les flux gratuits changent souvent : certaines chaînes peuvent être coupées à certains moments.

## Installer sur la télé

L'APK est compilé automatiquement par GitHub Actions à chaque modification du dossier `tv-matchs/`.
Lien direct : `https://github.com/farelello2018-debug/cahier-manga/releases/download/matchs-tv-latest/MatchsTV.apk`

1. Sur la télé, installe l'application **Downloader** (by AFTVnews) depuis le Play Store.
2. Dans **Paramètres → Applications → Sécurité** (le nom varie selon la télé), autorise Downloader à installer des applications inconnues.
3. Ouvre Downloader, tape le lien ci-dessus, puis installe.
4. L'app **Matchs TV** apparaît dans la liste des applications de la télé.

## Compiler soi-même

Avec Android Studio, ou en ligne de commande (SDK Android requis) :

```bash
cd tv-matchs && ./gradlew assembleRelease
```
