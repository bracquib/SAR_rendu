accept -> let a listener acceptrdv -> notifier et remplir avec la channel

connect -> rdv + let listener -> notifier et remplir avec channel

read -> space ? post write and block space : let listenner -> return in read

write same as read

disconnect -> post (destroy all writer and reader listner from other guy)

event :
read, write, giveChanel, disconnect

listener :
accept/connect (same), read, write

broker :
acceptList

channel :
readList, writeList

task:
channel








