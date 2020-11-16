
# Dessine moi un mouton ! (projet qui n'est plus maintained)
Ce jeu multijoueur est inspiré du célèbre jeu de société Pictionnary. En effet, un joueur dessine quelque chose choisi au hasard par le système tandis que les autres essaient de deviner le plus vite possible. Plus vite vous trouvez, plus vous avez de points. Le vainqueur est cel.ui.le qui a le plus de points à la fin du jeu.

## Features

 - Possiblité de plusieurs parties en même temps
 - Jusqu'à 30 joueurs sur le même serveur (si pas plus!)
 - Hébergé sur une machine performante
 - Optimisé pour toutes les configurtions 

Lancer le client (en alpha)

 1. Lancer le back-end Java  (bin/PicUDPClient.jar) :
 ```bash
    java -jar PicUDPClient.jar NOM_DU_JOUEUR 10001 t
```
 2. Installer Py4J et Pygame pour Python :
 ```bash
    pip install py4j pygame
 ```
 3. Lancer le programme Python (frontend/PicAcademy.py) :
 ```bash
    python PicAcademy.py
 ```
 
 Malheureusement, le serveur n'est plus en ligne. 
 Vous pouvez soit en héberger un autre soit lancer le serveur en local et ouvrir plusieurs instances du client (varier le port qui est ici 10001).
