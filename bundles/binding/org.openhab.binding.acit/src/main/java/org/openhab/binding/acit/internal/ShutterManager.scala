package org.openhab.binding.acit.internal

import akka.actor._
import org.openhab.core.events.EventPublisher
import org.openhab.binding.acit.ActiveItemsBindingProvider
import org.openhab.core.types.Command
import org.openhab.core.types.State
import org.openhab.core.library.types.UpDownType
import org.openhab.core.library.types.StopMoveType
import org.openhab.core.library.types.PercentType
import org.openhab.core.library.types.DecimalType
import java.util._

class ShutterManager {
  
}

object ShutterManager {
  def getActorForItem(itemName:String, 
      provider:ActiveItemsBindingProvider,
      eventPublisher:EventPublisher):ActorRef = {

    val itemBehind:String = provider.getItemBehindName(itemName)
    val calibrationHt = provider.getCalibration(itemName)
    val cal = convertProperties(calibrationHt)
    
    ActiveItemsActivator.actorSystem.actorOf(
        ShutterActor.props(itemName, itemBehind, cal, eventPublisher), 
        name = "Actor_" + itemName);
  }
  
  def convertProperties(prop:java.util.Hashtable[java.lang.Double,java.lang.Double])
    :scala.collection.immutable.Map[Double,Double] = {
    val bb = collection.JavaConverters.dictionaryAsScalaMapConverter(prop)
    val conv = bb.asScala.map(x => x match {
      case (key,value) => (key.asInstanceOf[Double], value.asInstanceOf[Double]) 
    })
    val cal:collection.immutable.Map[Double,Double] = collection.immutable.HashMap() ++ conv
    cal
  }
  
  def forwardItemCmd(actor:ActorRef, command:Command) = command match {
    case UpDownType.DOWN => actor ! ShutterMoveDownMsg
    case UpDownType.UP => actor ! ShutterMoveUpMsg
    case StopMoveType.STOP => actor ! ShutterStopMsg
    case StopMoveType.MOVE =>
    case pos:PercentType => actor ! ShutterMoveToMsg(pos.intValue())
  }  

  def forwardItemBehindCmd(actor:ActorRef, command:Command) = command match {
    case UpDownType.DOWN => actor ! ShutterBehindMoveDownMsg
    case UpDownType.UP => actor ! ShutterBehindMoveUpMsg
    case StopMoveType.STOP => actor ! ShutterBehindStopMsg
    case StopMoveType.MOVE =>
    case pos:PercentType => actor ! ShutterBehindMoveToMsg(pos.intValue())
  }
  
  def forwardItemBehindStatus(actor:ActorRef, newState:State) = newState match {
    case pos:DecimalType => actor ! ShutterBehindUpdatePosMsg(pos.intValue())
  }

  def forwardItemStatus(actor:ActorRef, newState:State) = newState match {
    case pos:DecimalType => // do nothing actor ! ShutterUpdatePosMsg(pos.intValue()*100/255)
  }
}