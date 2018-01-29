package com.samehadar.financialcentre

import java.nio.charset.Charset
import akka.http.scaladsl.model.{HttpMethod, HttpMethods}
import akka.stream.javadsl.Sink
import akka.stream.scaladsl.{GraphDSL, RunnableGraph}
import akka.util.ByteString
import io.circe.Decoder
import io.circe.jawn.JawnParser
import cats._
import cats.data.EitherT
import cats.implicits._
import cats.instances._

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by vital on 28.01.2018.
  */
object Test extends App {
  import akka.NotUsed
  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl.{Flow, Merge, Source}

  import scala.concurrent.Await
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  import scala.util.Try

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  type In = (Try[HttpResponse], NotUsed)
  type Result[A] = Either[Err,A]

  val flow = Http().superPool[NotUsed]()

  val uri1 = "https://google.com"
  val uri2 = "https://yandex.ru"

  val source = Source.single[(HttpRequest,NotUsed)]((HttpRequest(uri = uri1),NotUsed))

  val respToBytes: In => Result[Future[ByteString]] = {
    case (Success(s),_) => Right(s.entity.dataBytes.runFold(ByteString.empty)(_ ++ _))
    case (Failure(t),_) => Left(NetworkError(t))
  }


  val parser = new JawnParser

  def deserialize[T](implicit d: Decoder[T]): ByteString => Result[T] =
    byteStr => {
      val str = byteStr.decodeString(Charset.forName("UTF-8"))
      val r = for {
        json <- parser.parse(str)
        decoded <- d.decodeJson(json)
      } yield decoded

      r.leftMap(DecodingError)
    }



  case class Query1()
  case class Query2()

  import io.circe.generic.semiauto._
  implicit val dQ1: Decoder[Query1] = deriveDecoder

  val secondRequest: Flow[Future[Result[Query1]], EitherT[Future,Err,HttpRequest], NotUsed] =
    Flow[Future[Result[Query1]]]
      .map(EitherT(_).map { query =>
        //TODO build second request
        HttpRequest(uri = uri2, method = HttpMethods.POST)
      }
      )

  def deserializeFlow[T](implicit d: Decoder[T]) =
    Flow[(Try[HttpResponse],NotUsed)]
      .map(respToBytes)
      .map(_.traverse(_.map(deserialize[Query1])))
      .map(_.map(_.joinRight))


  def f[L,R,F[_],Mat](source: Source[EitherT[F,L,R], Mat], lSink: Sink[L, Mat], rSink: Sink[R, Mat]) = {
    RunnableGraph.fromGraph(GraphDSL.create(source, lSink, rSink)((_,_,_)) { implicit b => (s, l, r) =>
      ???
    })
  }

  val value = source
    .via(flow)
    .via(deserializeFlow[Query1])
    .via(secondRequest)
  //.via()
  //.via(deserializeFlow[Query2])



  //val res = Await.ready(???, 3.second)
}
