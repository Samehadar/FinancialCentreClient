package com.samehadar.financialcentre

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, RunnableGraph, Sink, Source, Zip}

import scala.collection.immutable.Iterable
import scala.util.{Failure, Success, Try}

/**
  * Created by vital on 28.01.2018.
  */
object Main extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val flow = Http().superPool[NotUsed]()

  val s1 = Source.single((HttpRequest(),NotUsed))
  val s2 = Source.single((HttpRequest(),NotUsed))

  val source = Source.combine(s1,s2)(Merge(_))

  val respValidation = Flow[(Try[HttpResponse], NotUsed)]
    .map { case (maybeResp,_) => maybeResp.toEither }

}

