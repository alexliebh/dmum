from py4j.java_gateway import JavaGateway, GatewayParameters, Py4JError

# Indices des paramètres de la liste du back-end
GAME_ID = 0
ROUND_ID = 1
WORDS = 2
WORD = 3
TIMER = 4
STATE = 5
BOARD = 6
USERS = 7
MAIN_USER = 8
ROUND_END = 9
MESSAGES = 10
CLOSE = 11

"""
Pour envoyer un message                    --> game.sendMessage("MESSAGE")
Si personne est dessinatrice               --> game.chooseWord("MOT CHOISI")
Dessiner dans le tableau                   --> game.drawUnit(row, col)
Changer la couleur                         --> game.changeColor(index de la couleur)
Vérifie si cette personne est dessinatrice --> game.isMainUser()
"""

def update(gateway):
    # Contient toutes les valeurs liées au jeu
    game = gateway.getGame()

    # Liste d'éléments à update
    updaters = gateway.getUpdaters()

    # Si le game id a changé
    if(updaters[GAME_ID]):
        # Dire au back-end qu'on a bien reçu le changement  NE PAS OUBLIER!
        gateway.updated(GAME_ID)
        # Affiche l'ID de la partie
        print(game.getGameID())

    # Si l'ID de la manche a changé
    if(updaters[ROUND_ID]):
        # Dire au back-end qu'on a bien reçu le changement  
        gateway.updated(ROUND_ID)
        # Affiche l'ID de la manche
        print(game.getRoundID())

    # Les mots que le dessinateur peut choisir
    if(updaters[WORDS]):
        gateway.updated(WORDS)
        print(game.getWords())

    # Longeur du mot que le dessinateur a choisi
    if(updaters[WORD]):
        gateway.updated(WORD)
        print(game.getWordLength())

    # Si le timer a augmenté
    if(updaters[TIMER]):
        gateway.updated(TIMER)
        print(game.getTimer())

    # Si l'état de la partie a changé (technique, pas spécialement nécessaire d'afficher)
    if(updaters[STATE]):
        gateway.updated(STATE)
        print(game.getState())

    # Affiche la liste des éléments du terrain de jeu
    if(updaters[BOARD]):
        gateway.updated(BOARD)
        print(game.getBoard().toString())

    # Liste des utilisateurs de la partie
    if(updaters[USERS]):
        gateway.updated(USERS)
        for u in game.getUsers():
            print(u.getIdentifier())

    # Le dessinateur qui a changé
    if(updaters[MAIN_USER]):
        gateway.updated(MAIN_USER)
        print("Main user : "+game.getMainUser().getIdentifier())

    #Fin du round
    if(updaters[ROUND_END]):
        gateway.updated(ROUND_END)
        print("Round end!")

    # Si le back-end se déconnecte
    if(updaters[CLOSE]):
        exit(0)

    # Liste de messages
    if(updaters[MESSAGES]):
        gateway.updated(MESSAGES)
        for msg in game.getMessages():
            print(msg.toString())

    # Fait en sorte que cette fonction soit rappellée 200ms plus tard
    fen.after(200, lambda:self.update(gateway))

# Crée le lien entre le b-e et le f-e
gateway = JavaGateway(gateway_parameters=GatewayParameters(port=10001))
gateway = gateway.entry_point

try:
    # Teste s'il y a une erreur avec la connexion et en même temps récupère la liste de couleurs pour le jeu
    colors = gateway.getColors()
except Exception:
    print("Erreur lors de la connexion au back end.\nEst-il bien lancé ?")
    exit(0)

# Lance la boucle d'update. A appeler juste avant le mainLoop
update(gateway)