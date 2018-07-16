package com.wms.user.domain

import java.sql.{Date, Timestamp}

import com.wms.core.database.DatabaseUtil
import slick.jdbc.MySQLProfile.api._
import io.strongtyped.active.slick._
import slick.ast.BaseTypedType
import io.strongtyped.active.slick.Lens._
import slick.jdbc.GetResult

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

import scala.language.higherKinds
import com.wix.accord.Validator
import com.wix.accord.dsl._
import reactor.core.publisher.{Flux, Mono}

  case class Log(startDate:Timestamp, endData:Timestamp, browser:String, osName:String,
                 spendMs:Long, targetClass:String, taragetMethod:String,
                 ipAddress:String, userName:String, operation:String, id:Option[Int]=None)

  class Logs(tag :Tag)  extends Table[Log](tag,"t_logs"){
    def id = column[Int]("id",O.PrimaryKey,O.AutoInc)
    def startDate = column[Timestamp]("startDate",O.Default(new Timestamp(new java.util.Date().getTime)))
    def endData = column[Timestamp]("endData",O.Default(new Timestamp(new java.util.Date().getTime)))
    def browser = column[String]("browser")
    def osName = column[String]("osName")
    def spendMs= column[Long]("spendMs")
    def targetClass = column[String]("targetClass")
    def taragetMethod = column[String]("taragetMethod")
    def ipAddress = column[String]("ipAddress")
    def userName = column[String]("userName")
    def operation = column[String]("operation")
    def * = (startDate,endData,browser,osName,spendMs,targetClass,taragetMethod,ipAddress,userName,operation,id.?)<>(Log.tupled,Log.unapply)
  }

  object Logs extends TableQuery(new Logs(_)){

  }

  class LogRepo extends EntityActions with MySQLProfileProvider{
    import jdbcProfile.api._
    val baseTypedType = implicitly[BaseTypedType[Id]]
    type Entity = Log
    type Id = Int
    type EntityTable = Logs
    val tableQuery = Logs
    def $id(table:Logs):Rep[Id] = table.id
    val idLens = lens { log: Log => log.id  }
    { (log, id) => log.copy(id = id) }
  }

  object LogRepo extends LogRepo{
    import scala.compat.java8.FutureConverters._
    import play.api.libs.json.Json._
    import play.api.libs.json._

    implicit val getLogResult = GetResult(r => Log(r.nextTimestamp(),r.nextTimestamp(),r.nextString(),r.nextString(),
      r.nextInt(),r.nextString(),r.nextString(),r.nextString(),r.nextString(),r.nextString(),r.nextIntOption()))
    def timestampToDateTime(t: Timestamp): java.util.Date = new java.util.Date(t.getTime)
    def dateTimeToTimestamp(dt: java.util.Date): Timestamp = new Timestamp(dt.getTime)
    implicit val timestampFormat = new Format[Timestamp] {
      def writes(t: Timestamp): JsValue = toJson(timestampToDateTime(t))
      def reads(json: JsValue): JsResult[Timestamp] = fromJson[java.util.Date](json).map(dateTimeToTimestamp)
    }

    implicit val logFormat = Json.format[Log]
    lazy val dbUtil = DatabaseUtil
    implicit val logValidator:Validator[Log] = validator[Log]{log =>
      log.operation is notEmpty
    }

    def findByTargetClass(targetClass:String):Flux[Log] = {
      val action = for {
        log <- tableQuery if log.targetClass === targetClass
      }yield log
      Flux.from(dbUtil.getStream(action.result))
    }

    def insertLog(log:Log):Mono[Int] = {
      val action = tableQuery returning tableQuery.map(_.id) += log
      Mono.fromFuture(dbUtil.getFuture(action).toJava.toCompletableFuture)
    }

    def createTable={
      val action = tableQuery.schema.create
      Mono.fromFuture(dbUtil.getFuture(action).toJava.toCompletableFuture)
    }
  }
