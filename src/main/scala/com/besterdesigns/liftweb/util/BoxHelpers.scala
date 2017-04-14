package com.besterdesigns.liftweb.util

import net.liftweb.common.Box
import net.liftweb.common.Empty
import net.liftweb.common.Failure
import net.liftweb.common.Full

object BoxHelpers extends BoxHelpers

trait BoxHelpers {
  def bdTry[T](f: => Box[T]): Box[T] = bdTry(Nil, Empty)(f)
  
  def bdTry[T](ignore: List[Class[_]], onError: Box[Throwable => Unit])(f: => Box[T]): Box[T] = {
    try {
      f
    } catch {
      case c if ignore.exists(_.isAssignableFrom(c.getClass)) => onError.foreach(_(c)); Empty
      case c if (ignore == null || ignore.isEmpty) => onError.foreach(_(c)); Failure(c.getMessage, Full(c), Empty)
    }
  }
}