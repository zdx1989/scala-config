package com.github.zdx.configs

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by zhoudunxiong on 2018/5/1.
  */
class ConfigSpec extends FunSpec with Matchers {

  describe("Config") {

    it("should get A from Config") {
      val config = ConfigFactory.parseString("foo = 42")
      ConfigReader[Int].read(config, "foo") should be (Right(42))
      ConfigReader[Long].read(config, "foo") should be (Right(42L))
      ConfigReader[Double].read(config, "foo") should be (Right(42.0))
      ConfigReader[String].read(config, "foo") should be (Right("42"))

      an [ConfigException] should be thrownBy ConfigReader[Boolean].read(config, "foo")
          .fold(e => throw e, identity)
      val config1 = ConfigFactory.parseString("foo = true")
      ConfigReader[Boolean].read(config1, "foo") should be (Right(true))

      ConfigReader[Option[Int]].read(config, "zdx") should be (Right(None))
      ConfigReader[Option[Int]].read(config, "foo") should be (Right(Some(42)))
    }

    it("should get case class from config") {
      import ConfigObjectReader._
      case class User(name: String, age: Int)
      val config = ConfigFactory.parseString(
        """
          |user {
          | name = zdx
          | age = 29
          |}
        """.stripMargin)
      ConfigReader[User].read(config, "user") should be (Right(User("zdx", 29)))
    }
  }
}
