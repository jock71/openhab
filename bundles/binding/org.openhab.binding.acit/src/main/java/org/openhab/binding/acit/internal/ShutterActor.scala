package org.openhab.binding.acit.internal

import akka.actor._

import org.openhab.core.events.EventPublisher
import org.openhab.core.library.types.UpDownType
import org.openhab.core.library.types.StopMoveType
import org.openhab.core.library.types.PercentType
import scala.concurrent.duration._
import ShutterBehaviour._

class ShutterActor(
    item:String, 
    itemBehind:String,
    calibration:Map[Double,Double],
    eventPublisher:EventPublisher) extends Actor {
  
  val resynchName = "resync-4-" + itemBehind
  
  def receive = idle(IdleData(-1))
    
  def handleMoveTo(data:IdleData):PartialFunction[Any,Unit] = {
    case ShutterMoveToMsg(targetPos:Int) =>
      if(isResyncRequired(data.currentPos, targetPos)) {
        val resyncPos = getResyncPos(targetPos)
        //context.system.scheduler.scheduleOnce(30 seconds, self, message)
        //context.setReceiveTimeout(30 seconds)
        eventPublisher.postCommand(itemBehind, new PercentType(resyncPos))
        context.become(resync(ResyncData(data.currentPos, resyncPos, targetPos)), true)
      } else {
        val behindPos = fromActive2Behind(targetPos, calibration)
        eventPublisher.postCommand(itemBehind, new PercentType(behindPos))
        context.become(idle(IdleData(targetPos)), true)
      }     
  }

  /**
   * simply forward the request to the itembehind for UP, DOWN and STOP request
   */
  def baseHandling(data:IdleData):PartialFunction[Any, Unit] = {
    case ShutterMoveUpMsg => 
      eventPublisher.postCommand(itemBehind, UpDownType.UP)
      context.become(idle(IdleData(0)), true)
      
    case ShutterMoveDownMsg =>
      eventPublisher.postCommand(itemBehind, UpDownType.DOWN)
      context.become(idle(IdleData(100)), true)
      
    case ShutterStopMsg =>
      eventPublisher.postCommand(itemBehind, StopMoveType.STOP)    
      context.become(idle(data), true)
  }
  
  def actionOnItemBehindHandling(data:IdleData):PartialFunction[Any, Unit] = {
    case ShutterBehindMoveUpMsg | ShutterBehindMoveDownMsg | ShutterBehindStopMsg | ShutterBehindMoveToMsg(_:Int) =>
      context.become(idle(data), true)
    
    case ShutterBehindUpdatePosMsg(position:Int) =>
      val shutterPos = fromBehind2Active(position, calibration)
      context.become(idle(IdleData(shutterPos)), true)
      eventPublisher.postUpdate(item, new PercentType(shutterPos))
  }

  
  def idle(data:IdleData):PartialFunction[Any,Unit] = {
    handleMoveTo(data) orElse
      baseHandling(data) orElse
      actionOnItemBehindHandling(data)
  }
  
   
  def resync(data:ResyncData):PartialFunction[Any,Unit] = {
    case ShutterBehindUpdatePosMsg(pos) =>
      println("resync:ShutterBehindUpdate pos="+pos+" data=("+data.resyncPos+","+data.targetPos+")")
      if(positionReached(pos, data)) { // we reached resynch position
        println("resync:positionReached")
        val behindPos = fromActive2Behind(data.targetPos, calibration)
        eventPublisher.postCommand(itemBehind, new PercentType(behindPos))
        context.become(idle(IdleData(data.targetPos)), true)
      } else {
        // we reached an unexpected position
        println("resync:unexpected pos")
        val shutterPos = fromBehind2Active(pos, calibration)
        context.become(idle(IdleData(shutterPos)), true)
        eventPublisher.postUpdate(item, new PercentType(shutterPos))
      }
      
    case message @ (ShutterMoveUpMsg | ShutterMoveDownMsg | ShutterStopMsg | ShutterMoveToMsg(_:Int))  =>
      context.become(idle(IdleData(data.targetPos)), true)
      self ! message

    case ShutterBehindMoveUpMsg | ShutterBehindMoveDownMsg | ShutterBehindStopMsg =>
      context.become(idle(IdleData(data.targetPos)), true)
    
    case ShutterBehindMoveToMsg(pos:Int) =>
      if(pos != data.resyncPos) {
        context.become(idle(IdleData(data.resyncPos)))
      } else {
        // someone (maybe ourself) asked to go where we want to go (in the reync pos)
        // do nothing (wait for next events)
      }
  }
  
  
  private def isResyncRequired(currentPos:Int, targetPos:Int):Boolean = {
    if(targetPos<10 || targetPos>90) true else false
  }
  
  private def getResyncPos(position:Int):Int = {
    if(position<10) 0 
    else if(position>90) 100
    else position
  }
  
  private def positionReached(position:Int, data:ResyncData):Boolean = (position==data.resyncPos) 

}

object ShutterActor {
  def props(
      item:String, 
      itemBehind:String, 
      calibration:Map[Double,Double],
      eventPublisher:EventPublisher): Props = 
    Props(new ShutterActor(item, itemBehind, calibration, eventPublisher))
}
