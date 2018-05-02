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

}
