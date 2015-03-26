package org.openhab.binding.acit.internal

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;


class ActiveItemsActivator extends akka.osgi.ActorSystemActivator {
  val logger = LoggerFactory.getLogger(classOf[ActiveItemsActivator])
 
  override def start(bc:BundleContext):Unit = {
    super.start(bc)
    logger.debug("Active/Actor Items binding has been started.")
  }
  
  override def stop(bc:BundleContext):Unit = {
    super.stop(bc)
    logger.debug("Active/Actor Items binding has been stopped.")
  }
  
  def configure(bc:BundleContext, system:ActorSystem):Unit = {
    registerService(bc, system)
    ActiveItemsActivator.actorSystem = system
    //val shutterManager = system.actorOf(Props[ShutterManager], name = "manager")
  }
}

object ActiveItemsActivator {
  var actorSystem:ActorSystem = null
}