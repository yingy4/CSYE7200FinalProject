package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.tuple
import play.api.data.Forms.text
import services.Counter

@Singleton
class ScalaPoster @Inject() (cc: ControllerComponents,
                                 counter: Counter) extends AbstractController(cc) {
  val form = Form(
    tuple(
      "scala_name" -> text,
      "scala_surname" -> text
    )
  )

  def save = Action { implicit request =>

    def values = form.bindFromRequest.data
//    def name = values("scala_name")
    def name = Form("scala_name" -> text).bindFromRequest.get

   Ok(views.html.index("You are %s, %s".format()))
  }

}