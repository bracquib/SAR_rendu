# DESIGN FULL EVENT

Dans broker y'a une map pour les accepts et une pour les connects

## Bind
Tout le code de bind est dans le runnable
Dans le cas où il n'y a pas de connexion au préalable
- Ajoute à la map le listener (donné par le listener) au port
- Post un event bind
- Return true si le port est libre
Dans le cas où il y a un connectListener en attente -> map<Integer, Queue<ConnectListnener>>
- post le runnable


## Unbind

- S'il y est, retire de la liste des accepts le port et pour chaque connectlistner dans la map<Integer, Queue<MessageQueue>> faire close à chaque messagequeue (de notre côté et en remote)
- Return true si find, false sinon

## Connect

- Cherche le broker dans le BM
- Post un event connect -> dans le code: 
    - Check si dans la map des accepts y'a le port
    - Si tu trouves le port, tu crées les channels, la messagequeue etc et tu postes les events accepted et connecteed
    - Sinon, tu ajoutes ton listener dans la map des connectListener en attente
- Return true si find, false sinon


#################

## setListner

- Affecte le listener

## send
- post ecrire
- Post event l2.received avec le message
