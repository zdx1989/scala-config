package com.github.zdx.configs

import com.github.zdx.configs.ConfigReader.Result
import com.typesafe.config.{Config, ConfigException}

/**
  * Created by zhoudunxiong on 2018/5/1.
  */
trait ConfigObjectReader[A] {

  def read(config: Config, paths: List[String]): Result[A]
}


object ConfigObjectReader {

  def apply[A](implicit cra: ConfigObjectReader[A]): ConfigObjectReader[A] = cra

  def instance[A](f: (Config, List[String]) => Result[A]): ConfigObjectReader[A] = new ConfigObjectReader[A] {
    override def read(config: Config, paths: List[String]): Result[A] = f(config, paths)
  }

  def result[A](f: (Config, List[String]) => A): (Config, List[String]) => Result[A] = (config, paths) =>
    try Right(f(config, paths))
    catch { case e: ConfigException => Left(e) }

  import shapeless.{HNil, HList, ::, Generic}

  implicit val hNilConfigReader: ConfigObjectReader[HNil] = instance(result { (config, paths) =>
      if (paths.isEmpty) HNil
      else throw new IllegalArgumentException(s"cannot be converted to HNil")
  })

  implicit def hListConfigReader[H, T <: HList](implicit
                                                hConfigReader: ConfigReader[H],
                                                tConfigReader: ConfigObjectReader[T]): ConfigObjectReader[H :: T] =
    instance { (config, paths) =>
      paths match {
        case Nil => throw new IllegalArgumentException(s"The empty paths cannot be converted to HList")
        case _ => for {
          h <- hConfigReader.read(config, paths.head).right
          t <- tConfigReader.read(config, paths.tail).right
        } yield h :: t
      }
    }

  implicit def genericConfigReader[A, R](implicit
                                         gen: Generic.Aux[A, R],
                                         crr: ConfigObjectReader[R]): ConfigReader[A] =
    new ConfigReader[A] {
      override def read(config: Config, path: String): Result[A] = {
        import scala.collection.JavaConversions._
        val obj = config.getObject(path)
        val root = obj.toConfig
        val paths = obj.unwrapped().keySet().toList
        crr.read(root, paths).right.map(gen.from)
      }
    }
}