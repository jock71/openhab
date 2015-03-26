package org.openhab.binding.acit.internal

class ShutterMessage {}
class SmartShutterMessage extends ShutterMessage { }
class ShutterBehindMessage extends ShutterMessage { }
/**
 * Smart Shutter MoveUp command received
 */
case object ShutterMoveUpMsg extends ShutterMessage
/**
 * 
 */
case object ShutterMoveDownMsg extends SmartShutterMessage
case object ShutterStopMsg extends SmartShutterMessage
case class ShutterMoveToMsg(position:Int) extends SmartShutterMessage

/**
 * Shutter behind MoveUp command received
 */
case object ShutterBehindMoveUpMsg extends ShutterBehindMessage
/**
 * 
 */
case object ShutterBehindMoveDownMsg extends ShutterBehindMessage
case object ShutterBehindStopMsg extends ShutterBehindMessage
case class ShutterBehindMoveToMsg(position:Int) extends ShutterBehindMessage
case class ShutterBehindUpdatePosMsg(position:Int) extends ShutterBehindMessage