package org.openhab.binding.acit.internal

import org.openhab.core.events.EventPublisher
import org.openhab.core.library.types.PercentType
import akka.actor._


case class ShutterResyncActor(resyncPos:Int, targetPosition:Int, 
    itemBehind:String, eventPublisher:EventPublisher) extends Actor {
    
  def receive = waitResyncPosReached
  
  val waitResyncPosReached:PartialFunction[Any,Unit] = {
    case ShutterBehindUpdatePosMsg(position:Int) =>
      if(positionReached(position)) {
        eventPublisher.postCommand(itemBehind, new PercentType(position))
      }
      context stop self
  }
  
  def positionReached(position:Int):Boolean = (position==this.resyncPos) 
   
}

object ShutterResyncActor {
  def props(resyncPos:Int, targetPosition:Int, itemBehind:String, 
      eventPublisher:EventPublisher): Props = 
    Props(new ShutterResyncActor(resyncPos, targetPosition, itemBehind, eventPublisher))
}