package com.besterdesigns.snippet

import scala.concurrent.duration._
import dispatch._
import Defaults._
import scala.xml.Text
import scala.util.Left
import net.liftweb.common.{ Box, Empty, Full }
import net.liftweb.common.Loggable
import net.liftweb.http.DispatchSnippet
import net.liftweb.http.S
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.util.Helpers.strToSuperArrowAssoc
import net.liftmodules.ng.Angular.angular
import net.liftmodules.ng.Angular.jsObjFactory
import net.liftmodules.ng.Angular.renderIfNotAlreadyDefined
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmd
import net.liftweb.common.Failure
import net.liftweb.http.js.JsExp
import net.liftweb.http.js.JE
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import scala.util.Try
import scala.concurrent.Await
import net.liftweb.util.Props
import net.liftweb.http.ResponseShortcutException
import net.liftweb.http.NotFoundResponse
import net.liftweb.http.InternalServerErrorResponse
import net.liftweb.http.SessionVar
import net.liftweb.http.RequestVar
import net.liftweb.http.LiftRules
import org.joda.time.DateTime
import scala.util.Success
import net.liftmodules.ng.FutureConversions._
import net.liftmodules.ng.Angular.JsObjFactory

case class TestData(name:String, age:Int)

class DeviceSnippet extends DispatchSnippet with Loggable {
  implicit val liftJsonFormats = DefaultFormats
  
  private val whence = S.referer openOr "/"
  override def dispatch = {
    case "edit"     => edit
  }
  
  def getWillFail(): Box[JValue] = Failure("forced failure");
  
  def getTestData(): Box[JValue] =  {
    val data = List(TestData("a", 1), TestData("b", 2))
    val res = Full(Extraction.decompose(data))
    println(s"res $res")
    res
  }
  
  def getFutureFail() : Future[JValue] = Future.failed(new Exception("Future fail"))

  def getCachedTestData(): Future[Box[JValue]] =  {
    Future {
      val data = List(TestData("aa", 11), TestData("bb", 22))
      val res = Full(Extraction.decompose(data))
      println(s"futured res $res")
      res
    }
  }
  
  def edit = {
    val res = for {
      userId <- Full(1)
    } yield {
      def handleClick(updatedData: JValue): JsCmd = {JsCmds.Noop}
      
      val f1: JsObjFactory = jsObjFactory()
        .defAny("getData", getTestData())
        .defAny("getWillFail", getWillFail())
        .defFutureAny("getCachedData", getCachedTestData().la)
        .defFutureAny("getFutureFail", getFutureFail().la)
      
      def services() = angular.module("bc.services").factory("deviceService", f1)

      "#angularscript" #> renderIfNotAlreadyDefined(services)
    }

    res match {
      case Full(response) => response
      case Failure(err, _, _) => S.redirectTo(whence, () => {
        S.clearCurrentNotices
        S.error(err)
      })
      case _ => S.redirectTo(whence, () => {
        S.clearCurrentNotices
        S.error("Unknown please call vendor")
      })
    }
  }
}