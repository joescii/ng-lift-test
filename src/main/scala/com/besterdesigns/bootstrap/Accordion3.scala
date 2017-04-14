package com.besterdesigns.bootstrap

import scala.xml.Elem
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds

/**
 * Case Class defining header and body for accordion object.
 */
case class Collapsible3(header: String, body: Elem)

object Accordion3 {
  def apply(parentId: String) = new Accordion3(parentId)
}

/**
 * Javascript Bootstrap helper function to populate Accordion template.
 * Usage:
 * In template HTML file define tag with Accordion parentId (e.g., accordion1)
 *     <div class="container-fluid" data-lift="ViewJob">
 *     ...
 *                <div class="accordion" id="accordion1">
 *                  <span id="notes"></span>
 *                </div>
 *     ...
 *     </div>
 * In snippet bind [[buildCollapsible]] to the id specified in <span id="notes"></span>
 * def render = "#notes" #> Accordion("accordion1").buildCollapsible(collapsible list) &
 *
 */
class Accordion3(parentId: String) {

  private def collapseRef(count: Int) = "collapse_" + count
  /*
  private def getGroup1(data: Collapsible3, index: Int, collapsed: Boolean) = {
    val collapsed_class = collapsed match {
      case false => "panel-collapse collapse in"
      case true => "panel-collapse collapse"
    }
    
    if (data.body != "" && data.body.size > 0) {
      <div class="panel panel-default">
        <div class="panel-heading">
          <h4 class="panel-title">
            <a data-toggle="collapse" data-parent={ "#" + parentId } href={ "#" + collapseRef(index) }>
              { data.header }
            </a>
          </h4>
         </div>
        <div id={ collapseRef(index) } class={ collapsed_class }>
          <div class="panel-body">{ data.body }</div>
        </div>
      </div>
    } else {
      <div></div>
    }
  }
*/

  private def getGroup(data: Collapsible3, index: Int, collapsed: Boolean, f:(Int)=>JsCmd) = {
    val collapsed_class = collapsed match {
      case false => "panel-collapse collapse in"
      case true => "panel-collapse collapse"
    }   
    
    def aa(index: Int) = SHtml.ajaxButton(<i class="fa fa-minus-circle"></i>, () => {
//      System.out.println("In aa "+index)  
      f(index)
    }, "class"->"pull-right btn btn-default btn-xs")

    if (data.body != "" && data.body.size > 0) {
      <div class="panel panel-default">
    	<div class="panel-heading">
    	  {aa(index)}
    	  <!--
          <a href="#" class="pull-right btn btn-default btn-xs"><i class="fa fa-minus-circle"></i></a>
    	  -->
        <div data-toggle="collapse" data-parent={ "#" + parentId } data-target={ "#" + collapseRef(index) }>
          <h4 class="panel-title">
            <span class="pull-left accordion-toggle">
              { data.header }
            </span>
          </h4>
          <div class="clearfix"></div>
        </div>
        </div>
        <div id={ collapseRef(index) } class={ collapsed_class }>
          <div class="panel-body">{ data.body }</div>
        </div>
      </div>
    } else {
      <div></div>
    }
  }

  def buildCollapsible(data: List[Collapsible3], f:(Int) => JsCmd) = {
    var count = 0
    val list = for (c <- data) yield {
      count += 1; getGroup(c, count, true, f)
    }
    <div class="panel-group" id={ parentId }>
      { list }
    </div>
  }
}