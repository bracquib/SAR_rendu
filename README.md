Groupe de Benjamin BRACQUIER et de Lilian SOLER 
===================
- Pour lancer le test de 1 couche de Thread  ,run le fichier Test.java dans le package info5.sar.channels.tests .
- Pour lancer le test où il y a une premeière couche en Thread et la deuxième donc les queues en Threads, run le fichier Test.java dans le package info5.sar.queues.tests .
- Pour lancer le test où il y a une premeière couche en Thread et la deuxième donc les queues en évenementiel, run le fichier Test.java dans le package info5.sar.events.tests .
avec cette configuration:
 -ChannelBrokerClassName = "info5.sar.channels.CBroker"
 -QueueBrokerClassName = "info5.sar.mixed.queues.CQueueBroker"
et avoir en import :
import info5.sar.channels.Broker;
et dans QueueBroker.java:
 public String getName() {
	    return broker.getName();
	  }
et dans loadBrokerClasses():
Class params[] = new Class[1];
et dans newBrokers:
Object[] args = new Object[] { name };
- Pour lancer le test en full événementiel, run le fichier Test.java dans le package info5.sar.events.tests mais il faut avoir cette configuration:
 -ChannelBrokerClassName = "info5.sar.events.channel.CBroker"
 -QueueBrokerClassName = "info5.sar.events.queues.CQueueBroker"
et avoir en import :
import import info5.sar.events.channels.Broker;
et dans loadBrokerClasses():
Class params[] = new Class[2];
params[1] = Executor.class;
et dans newBrokers:
Object[] args = new Object[] { name, m_pump };
et dans QueueBroker.java:
 public String getName() {
        return broker1.getName();
      }
