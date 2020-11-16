import pygame
import os
os.environ['SDL_VIDEO_CENTERED'] = '1'
pygame.init()

import string
from py4j.java_gateway import JavaGateway, GatewayParameters, Py4JError

from Constantes import *

WIDTH = 1380
HEIGHT = 740
FPS = 120

WHITE = 21
DARK_GRAY = 19
LIGHT_GRAY = 20
BLACK = 22
MAIN_FONT = pygame.font.SysFont("Montserrat", 25)
BOLD_FONT = pygame.font.SysFont("Montserrat", 25, bold=True)
LARGE_FONT = pygame.font.SysFont("Montserrat", 35)

fenetre = None
clock = None

# Paramètres de la zone de dessin
c_width = 800
c_height = 600
taille_pix = 4  # Nombre de pixels dans un coté de carré (valeur par défaut: 4)
rows, cols = c_height//taille_pix, c_width//taille_pix
zone_dessin = pygame.Rect(220, 60, c_width, c_height)

epaisseur = 1
couleur = BLACK
cellules_dessin = []

joueurs = []
trouveurs = []
messages = []
mots = []

mot = None
joueur_princip = None
timer = None
trouve =  False
dessinateur = False
dessin_en_cours = False
etat = ""

boutons = []
couleurs = []

nv_message = None
ecriture_en_cours = False
zone_texte = pygame.Rect(1040, 590, 320, 60)

gateway_connection = None
gateway = None

def _pix_to_units(x, y):  # Convertit les pixels à une unité
    row, col = y//taille_pix, x//taille_pix
    if row >= rows-1:  # Utilisation d'une variable déjà créée pour pas avoir à la recacluler à chaque fois
        row = rows-1
    if col >= cols-1:
        col = cols-1
    if row < 1:
        row = 0
    if col < 1:
        col = 0
    return row, col

def _dessiner(x, y):
    global mot, couleur
    if mot:
        row, col = _pix_to_units(x, y)
        for i in range(row-epaisseur, row+epaisseur-1):
                for j in range(col-epaisseur, col+epaisseur-1):
                    cellules_dessin.append((i, j, couleur))
                    gateway.getGame().drawUnit(i,j)

def _ajouter_dessin(point):
    cellules_dessin.append(point)

# Fonction utilitaire pour plus facilement afficher du texte
def _afficher_texte(texte, pos, font=MAIN_FONT, color=(0,0,0)):
    fenetre.blit(font.render(texte, True, color), pos)

def _ajouter_bouton(texte, pos, callback):
    rect = pygame.Rect(pos[0], pos[1], 160, 40)
    boutons.append((texte, rect, callback, DARK_GRAY))

def _ajouter_petit_bouton(coul, pos, callback):
    rect = pygame.Rect(int(pos[0]), int(pos[1]), 20, 40)
    boutons.append(("", rect, callback, coul))

def _ajouter_boutons_dessin():
    if dessinateur and mot:
        _ajouter_bouton("Moins épais", (400, 680), lambda txt : _changer_epaisseur(-1))
        _ajouter_bouton("Clear", (570, 680), _vider_tableau)
        _ajouter_bouton("Plus épais", (740, 680), lambda txt : _changer_epaisseur(+1))
        half = len(couleurs)/2
        for i, coul in enumerate(couleurs):
            if i < half:
                _ajouter_petit_bouton(i, (50+30*i, 680), lambda c: _changer_couleur(c))
            else:
                _ajouter_petit_bouton(i, (950+30*(i-half), 680), lambda c: _changer_couleur(c))

def _ajouter_boutons_mots():
    if dessinateur and mots:
        for i, mot in enumerate(mots):
            _ajouter_bouton(mot, (400+160*i, 10), lambda mot : _choisir_mot(mot))

def _vider_tableau(mot):
    global cellules_dessin
    cellules_dessin.clear()
    if dessinateur:
        gateway.getGame().clearBoard()

def _changer_epaisseur(offset):
    global epaisseur
    epaisseur += offset

def _choisir_mot(m):
    global mot, mots
    gateway.getGame().chooseWord(m)
    mot = m
    mots = []
    boutons.clear()
    _ajouter_boutons_dessin()

def _changer_couleur(coul):
    global couleur
    couleur = coul
    if dessinateur:
        gateway.getGame().changeColor(couleur)

def _fin_manche():
    global boutons, couleur, mot, mots, dessinateur, timer, joueur_princip, trouve, messages, trouveurs
    boutons.clear()
    trouveurs.clear()
    _vider_tableau("")
    couleur = BLACK
    mot = None
    messages = []
    mots = []
    dessinateur = False
    joueur_princip = None
    timer = 0
    trouve = False

def _update_joueurs(jous):
    global joueurs
    joueurs.clear()
    for j in jous:
        joueurs.append(j)

def _update_tableau(new_tableau):
    for unit in new_tableau:
        _ajouter_dessin((unit.getX(), unit.getY(), unit.getColorID()))
    gateway.getGame().clearNewUnits()

def afficher_boutons():
    for bouton in boutons:
        texte, rect, callback, coul = bouton
        pygame.draw.rect(fenetre, couleurs[coul], rect)
        pygame.draw.rect(fenetre, couleurs[BLACK], rect, 1)

        _afficher_texte(texte, (rect.x+3, rect.y), color=couleurs[WHITE])

def afficher_joueurs():
    pygame.draw.rect(fenetre, couleurs[LIGHT_GRAY], (10, 60, 200, 600), 0)
    for i, joueur in enumerate(joueurs):

        police = MAIN_FONT
        color = BLACK
        if joueur_princip and joueur_princip.getID() == joueur.getID():
            police = BOLD_FONT
        elif joueur.getID() in trouveurs:
            color = 16

        _afficher_texte(joueur.getUsername()+" ["+str(joueur.getScore())+"]", (20, 70+70*i), font=police, color=couleurs[color])

def afficher_chat():
    global nv_message, messages
    pygame.draw.rect(fenetre, couleurs[LIGHT_GRAY], (1030, 60, 340, 600), 0)

    for i, message in enumerate(messages):
        auteur, msg, score = message.toString().split('µ')
        color = couleurs[BLACK]
        font = MAIN_FONT
        if message.isSuccessful():
            if trouve or dessinateur:
                color = couleurs[15]
                font = BOLD_FONT
                _afficher_texte(auteur+" : "+msg, (1035, 70+35*i), color=color, font=font)
        else:
            _afficher_texte(auteur+" : "+msg, (1035, 70+35*i), color=color, font=font)

    largeur_cadre = 1    
    if ecriture_en_cours:
        largeur_cadre = 2

    pygame.draw.rect(fenetre, couleurs[WHITE], zone_texte)
    pygame.draw.rect(fenetre, couleurs[BLACK], zone_texte, largeur_cadre)

    _afficher_texte(nv_message, (1042, 600))

def afficher_barres():
    global mot, trouve
    pygame.draw.rect(fenetre, couleurs[DARK_GRAY], (10, 10, 1360, 40), 0)  # haut
    if timer:
        _afficher_texte(str(timer), (100, 10), font=LARGE_FONT)
    
    if mot:
        if dessinateur or trouve:
            _afficher_texte(mot, (550, 13), color=couleurs[WHITE])
        else:
            _afficher_texte(' _'*len(mot), (545, 13), color=couleurs[WHITE])
    
    if etat:
        _afficher_texte(etat, (980, 13), color=couleurs[WHITE])

    pygame.draw.rect(fenetre, couleurs[DARK_GRAY], (10, 670, 1360, 60), 0)  # bas

def afficher_dessin():
    pygame.draw.rect(fenetre, couleurs[WHITE], zone_dessin, 0)

    for point in cellules_dessin:
        x, y = point[1]*taille_pix + zone_dessin.x, point[0]*taille_pix+zone_dessin.y
        pygame.draw.rect(fenetre, couleurs[point[2]], (x, y, taille_pix, taille_pix), 0)

def afficher_elements():
    fenetre.fill((0, 0, 18))
    afficher_joueurs()
    afficher_chat()
    afficher_barres()
    afficher_dessin()
    afficher_boutons()

    # Done after drawing everything to the screen
    pygame.display.flip()

def _click(event):
    global ecriture_en_cours, zone_dessin, dessin_en_cours, mot
    if event.button == 1:
        if zone_dessin.collidepoint(event.pos):
            if dessinateur:
                dessin_en_cours = True
        elif zone_texte.collidepoint(event.pos):
            if not dessinateur:
                ecriture_en_cours = True
        else:
            for bout in boutons:
                if bout[1].collidepoint(event.pos):
                    if bout[0]:
                        bout[2](bout[0])
                    else:
                        bout[2](bout[3])

def _clavier(event):
    global ecriture_en_cours, nv_message, trouve
    if ecriture_en_cours:
        if event.key == pygame.K_BACKSPACE:
            if nv_message:
                nv_message = nv_message[:-1]
        elif event.key == pygame.K_RETURN or event.key == pygame.K_KP_ENTER:
            if nv_message:
                ecriture_en_cours = False
                if gateway.getGame().sendMessage(nv_message):
                    trouve = True
                nv_message = None
        elif event.unicode in string.printable:
            if not nv_message:
                nv_message = event.unicode
            else:
                nv_message += event.unicode

def gestion_evenements():
    global dessin_en_cours
    # Récupère tous les évènements
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            fermer_gateway()
            return False
        if event.type == pygame.MOUSEBUTTONDOWN:
            _click(event)
        if event.type == pygame.MOUSEBUTTONUP and dessin_en_cours:
            if event.button == 1:
                dessin_en_cours = False
        if event.type == pygame.MOUSEMOTION and dessin_en_cours:
            mouse_x, mouse_y = event.pos
            x = mouse_x - zone_dessin.x
            y = mouse_y - zone_dessin.y
            _dessiner(x, y)
        if event.type == pygame.KEYDOWN:
            _clavier(event)
    return True

def lancer_gateway():
    global gateway_connection, gateway, couleurs
    # Crée le lien entre le backend et le frontend
    gateway_connection = JavaGateway(gateway_parameters=GatewayParameters(port=10001))
    gateway = gateway_connection.entry_point

    try:
        # Teste s'il y a une erreur avec la connexion
        gateway.getColors()
    except Exception:
        print("Erreur lors de la connexion au back end.\nEst-il bien lancé ?")
        exit(0)

    for c in gateway.getColors():
        col = (c[0], c[1], c[2])
        couleurs.append(col)

def fermer_gateway():
    gateway_connection.close()
    gateway_connection.shutdown()

def update_gateway():
    global gateway, dessinateur, mots, mot, timer, messages, joueur_princip, etat, trouveurs
    # Contient toutes les valeurs liées au jeu
    game = gateway.getGame()
    # Liste d'éléments à update
    updaters = gateway.getUpdaters()

    # Si le game id a changé
    if(updaters[GAME_ID]):
        # Dire au back-end qu'on a bien reçu le changement  NE PAS OUBLIER!
        gateway.updated(GAME_ID)
        # Affiche l'ID de la partie
        print("Game ID: " + str(game.getGameID()))
        _update_joueurs(game.getUsers())

    # Si l'ID de la manche a changé
    if(updaters[ROUND_ID]):
        gateway.updated(ROUND_ID)
        # Affiche l'ID de la manche
        print("Round ID: " + str(game.getRoundID()))

    # Les mots que le dessinateur peut choisir
    if(updaters[WORDS]):
        gateway.updated(WORDS)
        mots = game.getWords()
        _ajouter_boutons_mots()

    # Mot choisi par le dessinateur
    if(updaters[WORD]):
        gateway.updated(WORD)
        mot = game.getWord()

    # Si le timer a augmenté
    if(updaters[TIMER]):
        gateway.updated(TIMER)
        timer = game.getTimer()

    # Si l'état de la partie a changé (technique, pas spécialement nécessaire d'afficher)
    if(updaters[STATE]):
        gateway.updated(STATE)
        etat = game.getState().getState()

    # Affiche la liste des éléments du terrain de jeu
    if(updaters[BOARD]):
        gateway.updated(BOARD)
        _update_tableau(game.getNewUnits())
    # Liste des utilisateurs de la partie
    if(updaters[USERS]):
        gateway.updated(USERS)
        _update_joueurs(game.getUsers())

    # Le dessinateur qui a changé
    if(updaters[MAIN_USER]):
        gateway.updated(MAIN_USER)

        joueur_princip = gateway.getGame().getMainUser()
        if game.isMainUser():
            dessinateur = True
        else:
            dessinateur = False

        _ajouter_boutons_mots()

    #Fin du round
    if(updaters[ROUND_END]):
        gateway.updated(ROUND_END)
        _fin_manche()
        print("Round end!")

    # Si le back-end se déconnecte
    if(updaters[CLOSE]):
        exit(0)

    if(updaters[CLEAR]):
        _vider_tableau("")

    # Liste de messages
    if(updaters[MESSAGES]):
        gateway.updated(MESSAGES)
        messages.clear()
        trouveurs.clear()
        for msg in game.getMessages():
            if msg.isSuccessful():
                trouveurs.append(msg.getSenderID())
        for msg in game.getMessages()[-14:]:
            messages.append(msg)


def lancer_fenetre():
    global fenetre, clock
    # Création de la fenetre.
    fenetre = pygame.display.set_mode((WIDTH, HEIGHT))
    pygame.display.set_caption("PicAcademy - Alexandre & Wladimir")
    clock = pygame.time.Clock()

if __name__ == "__main__":
    mot = None
    lancer_gateway()
    lancer_fenetre()
    # Game loop
    continuer = True
    while continuer:
        clock.tick(FPS)
        update_gateway()
        continuer = gestion_evenements()
        afficher_elements()

    pygame.quit()
