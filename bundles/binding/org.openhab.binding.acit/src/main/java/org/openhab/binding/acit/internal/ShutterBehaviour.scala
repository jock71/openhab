package org.openhab.binding.acit.internal


object ShutterBehaviour {
  class Data(position:Int) {
    def getPos = position
  }
  case class IdleData(currentPos:Int) extends Data(currentPos)
  case class ResyncData(startPos:Int, resyncPos:Int, targetPos:Int) extends Data(startPos)
  // case class MovingData(startPos:Int,targetPos:Int) extends Data(startPos:Int)

  
  def fromBehind2Active(behindPos:Int, calibration:Map[Double,Double]):Int = {
    val (leftAct, leftRaw) = calibration.foldLeft((0.asInstanceOf[Double],0.asInstanceOf[Double]))({
      (currLeft, el) => if(el._2<behindPos && el._2>currLeft._2) el else currLeft
    })
    val (rightAct, rightRaw) = calibration.foldLeft((100.asInstanceOf[Double],100.asInstanceOf[Double]))({
      (currRight, el) => if(el._2>behindPos && el._2<currRight._2) el else currRight    
    })
    val actVal:Double = leftAct + (behindPos-leftRaw)*(rightAct-leftAct)/(rightRaw-leftRaw)
    actVal.asInstanceOf[Int] 
  }
  
  def fromActive2Behind(activePos:Int, calibration:Map[Double,Double]):Int = {
    val (leftAct, leftRaw) = calibration.foldLeft((0.asInstanceOf[Double],0.asInstanceOf[Double]))({
      (currLeft, el) => if(el._1<activePos && el._1>currLeft._1) el else currLeft
    })
    val (rightAct, rightRaw) = calibration.foldLeft((100.asInstanceOf[Double],100.asInstanceOf[Double]))({
      (currRight, el) => if(el._1>=activePos && el._1<currRight._1) el else currRight    
    })
    val rawVal:Double = leftRaw + (activePos-leftAct)*(rightRaw-leftRaw)/(rightAct-leftAct)
    rawVal.asInstanceOf[Int]
  }
}
