package com.github.zdx.configs

import java.time.Duration

import com.github.zdx.configs.ConfigReader.Result
import com.typesafe.config.{Config, ConfigException}



/**
  * Created by zhoudunxiong on 2018/5/1.
  */
trait ConfigReader[A] {

  def read(config: Config, path: String): Result[A]
}

object ConfigReader {

  type Result[A] = Either[ConfigException, A]

  def apply[A](implicit cra: ConfigReader[A]): ConfigReader[A] = cra

  def instance[A](f: (Config, String) => Result[A]): ConfigReader[A] = new ConfigReader[A] {
    override def read(config: Config, path: String): Result[A] = f(config, path)
  }

  def result[A](f: (Config, String) => A): (Config, String) => Result[A] = (config, path) =>
    try Right(f(config, path))
    catch { case e: ConfigException => Left(e) }

  implicit val intConfigReader: ConfigReader[Int] = instance(result(_.getInt(_)))

  implicit val longConfigReader: ConfigReader[Long] = instance(result(_.getLong(_)))

  implicit val doubleConfigReader: ConfigReader[Double] = instance(result(_.getDouble(_)))

  implicit val stringConfigReader: ConfigReader[String] = instance(result(_.getString(_)))

  implicit val booleanConfigReader: ConfigReader[Boolean] = instance(result(_.getBoolean(_)))

  implicit val durationConfigReader: ConfigReader[Duration] = instance(result(_.getDuration(_)))

  implicit def optionConfigReader[A](implicit cra: ConfigReader[A]): ConfigReader[Option[A]] =
    instance(result { (config, path) =>
      if (!config.hasPath(path)) None
      else Some(cra.read(config, path).fold(e => throw e, identity))
    })

  implicit def mapConfigReader[A](implicit
                                  mc: ConfigReader[A]): ConfigReader[Map[String, A]] =
    instance { (config, path) =>
      val obj = config.getObject(path)
      val root = obj.toConfig
      import scala.collection.JavaConversions._
      val paths = obj.unwrapped().keySet().toList
      val res = paths.map { key =>
        mc.read(root, key).right.map(key -> _)
      }
      sequence(res).right.map(_.toMap)
    }

  implicit def mapListConfigReader[A](implicit
                                      mlc: ConfigListReader[A]): ConfigReader[Map[String, List[A]]] =
    instance { (config, path) =>
      val obj = config.getObject(path)
      val root = obj.toConfig
      import scala.collection.JavaConversions._
      val paths = obj.unwrapped().keySet().toList
      val res = paths.map { key =>
        mlc.read(root, key).right.map(key -> _)
      }
      sequence(res).right.map(_.toMap)
    }

  def sequence[A](ra: List[Result[(String, A)]]): Result[List[(String, A)]] = {
    def loop(n: Int, res: Result[List[(String, A)]]): Result[List[(String, A)]] = n match {
      case m if m < 0 => res
      case _ => ra(n) match {
        case Left(e) => Left(e)
        case Right(a) => loop(n - 1, res.right.map(a :: _))
      }
    }
    loop(ra.length - 1, Right(Nil))
  }

}
