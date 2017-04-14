package bootstrap.liftweb

import java.util.Date
import java.util.TimeZone
import scala.xml.NodeSeq
import scala.xml.Text
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import net.liftmodules.FoBo
import net.liftmodules.widgets.autocomplete.AutoComplete
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import net.liftweb.util
import net.liftweb.util.DefaultDateTimeConverter
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers.intToTimeSpanBuilder
import net.liftweb.util.Mailer
import net.liftweb.util.NamedPF
import net.liftweb.util.Props
import scala.sys.SystemProperties
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends BiochargerLogMenu with Loggable {
  def boot {
       
    // where to search snippet
    LiftRules.addToPackages("com.besterdesigns")
    LiftRules.resourceNames = "MessageResource" :: Nil

    //		val server = new MongoClient("127.0.0.1", 27017)

    //		MongoDB.defineDb(util.DefaultConnectionIdentifier, server, "bc")

    MyCss.init //add css to html

    //		FoBo.InitParam.JQuery = FoBo.JQuery1102
    //FoBo.InitParam.JQuery = FoBo.JQuery211
    //FoBo.InitParam.ToolKit = FoBo.Bootstrap320
    //FoBo.InitParam.ToolKit = FoBo.FontAwesome403
    
    FoBo.InitParam.JQuery = FoBo.JQuery214
    FoBo.InitParam.ToolKit = FoBo.Bootstrap336
    FoBo.InitParam.ToolKit = FoBo.FontAwesome430
    FoBo.init()

    net.liftmodules.ng.Angular.init(
      futures = true,
      appSelector = "[ng-app]",
      includeJsScript = true)

    //Add CSS resources
    ResourceServer.allow {
      case "css" :: "bdtoggle.css" :: Nil => true
      case "css" :: "mystyles.css" :: Nil => true
      case "css" :: "styles.css" :: Nil => true
      case "css" :: "basestyles.css" :: Nil => true
      case "js" :: "bootstrap-fileinput" :: "css" :: "fileinput.min.css" :: Nil => true
      case "js" :: "jquery-ui" :: _ => true
      case "js" :: "jquery-migrate-1.2.1.min.js" :: _ => true
      case "js" :: "bootstrap-fileinput" :: "js" :: "fileinput.min.js" :: Nil => true
      case "fonts" :: _ => true
      case "images" :: _ => true
    }

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))
    LiftRules.setSiteMap(sitemap());

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => {
      notices match {
        case NoticeType.Notice  => Full((5.seconds, 5.seconds))
        case NoticeType.Error   => Full((5.seconds, 5.seconds))
        case NoticeType.Warning => Full((5.seconds, 5.seconds))
        case _                  => Empty
      }
    })
    
    LiftRules.responseTransformers.append {
      case CustomisedPages(resp) => resp
      case resp => resp
    }
    
    object CustomisedPages {
      val definedPages = 500 :: Nil
      
      def unapply(resp: LiftResponse): Option[LiftResponse] = 
        definedPages.find(_ == resp.toResponse.code).flatMap(toResponse)
        
        def toResponse(status: Int): Box[LiftResponse] = 
          for {
            session <- S.session
            req <- S.request
            template = Templates(status.toString :: Nil)
            response <- session.processTemplate(template, req, req.path, status)
          } yield response
    }
    
    val sessionTimeout = Props.getInt("session.timeout", 30)
    val ajaxTimeout = Props.getInt("ajax.timeout", 5000);
    val ajaxRetry = Props.getInt("ajax.retry", 5);
    
    println("sessionInactivityTimeout can be set in props file using session.timeout (minutes) key")
    println("ajaxRetryCount can be set in props file using ajax.retry (ms) key")
    println("retry count "+ajaxRetry)

    LiftRules.sessionInactivityTimeout.default.set(Full(60000L * sessionTimeout))
    LiftRules.ajaxPostTimeout = ajaxTimeout
    LiftRules.ajaxRetryCount = Full(ajaxRetry)    
  }

  def configMailer(host: String, user: String, password: String) {
    logger.info(s"Configuring for $host, $user")
    // Enable TLS support
    System.setProperty("mail.smtp.starttls.enable", "true");
    // Set the host name
    System.setProperty("mail.smtp.host", host) // Enable authentication
    System.setProperty("mail.smtp.auth", "true") // Provide a means for authentication. Pass it a Can, which can either be Full or Empty
    Mailer.authenticator = Full(new Authenticator {
      override def getPasswordAuthentication = new PasswordAuthentication(user, password)
    })
  }

  object MyCss {

    def init(): Unit = {     
      def addMyCss(s: LiftSession, r: Req) = {

        val paths = List(s.contextPath + "/classpath/css/styles.css", 
                         s.contextPath + "/classpath/css/mystyles.css",
                         s.contextPath + "/classpath/css/bdtoggle.css")
                         
        val elemData = paths.view.zipWithIndex.map{ case (element, index) => {
            <link id="custom{index}" data-lift="with_resource_id" rel="stylesheet" href={element} type="text/css"/>
          }
        }
        elemData.foreach { S.putInHead(_) }
        
        val jsClass = Props.get("demo.mode", "true").toBoolean match {
          case false => "production"
          case true => "demo"
        }
        
        logger.info("Setting class "+jsClass);
       
        val jsSetClass = JE.JsRaw(s"""$$(document).ready(function(){
          $$("#headerMain").addClass("${jsClass}")
          })""").cmd
        S.appendJs(jsSetClass)
       
      }

      LiftSession.onBeginServicing = addMyCss _ :: LiftSession.onBeginServicing
    }
  }
}

trait BiochargerLogMenu {  
  def sitemap() =
    SiteMap(Menu("Home") / "index")
}