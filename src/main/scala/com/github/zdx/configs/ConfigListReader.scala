package com.github.zdx.configs

import java.time.Duration

import com.github.zdx.configs.ConfigReader.Result
import com.typesafe.config.{Config, ConfigException}
import shapeless.Generic

/**
  * Created by zhoudunxiong on 2018/5/3.
  */
trait ConfigListReader[A] {

  def read(config: Config, path: String): Result[List[A]]
}

object ConfigListReader {
  import scala.collection.JavaConversions._

  def apply[A](implicit cra: ConfigListReader[A]): ConfigListReader[A] = cra
  
  def instance[A](f: (Config, String) => Result[List[A]]): ConfigListReader[A] = new ConfigListReader[A] {
    override def read(config: Config, path: String): Result[List[A]] = f(config, path)
  }
  
  def result[A](f: (Config, String) => List[A]): (Config, String) => Result[List[A]] = (config, path) =>
    try Right(f(config, path))
    catch { case e: ConfigException => Left(e) }
  
  implicit val intConfigReader: ConfigListReader[Int] = instance(result { (config, path) =>
    config.getIntList(path).toList.map(_.toInt)
  })

  implicit val longConfigReader: ConfigListReader[Long] = instance(result { (config, path) =>
    config.getLongList(path).toList.map(_.toLong)
  })

  implicit val doubleConfigReader: ConfigListReader[Double] = instance(result { (config, path) =>
    config.getDoubleList(path).toList.map(_.toDouble)
  })

  implicit val stringConfigReader: ConfigListReader[String] = instance(result { (config, path) =>
    config.getStringList(path).toList
  })

  implicit val booleanConfigReader: ConfigListReader[Boolean] = instance(result { (config, path) =>
    config.getBooleanList(path).toList.map(_.booleanValue())
  })

  implicit val durationConfigReader: ConfigListReader[Duration] = instance(result { (config, path) =>
    config.getDurationList(path).toList
  })

  implicit def objectConfigReader[A, R](implicit
                                        gen: Generic.Aux[A, R],
                                        crr: ConfigObjectReader[R]): ConfigListReader[A] =
  instance(result { (config, path) =>
    config.getObjectList(path).toList.map { obj =>
      val root = obj.toConfig
      val paths = obj.unwrapped().keySet().toList
      crr.read(root, paths).right.map(gen.from).fold(e => throw e, identity)
    }
  })
}
