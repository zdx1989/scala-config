package com.github.zdx.configs

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by zhoudunxiong on 2018/5/1.
  */
class ConfigSpec extends FunSpec with Matchers {
  import ConfigObjectReader._

  case class User(name: String, age: Int)

  describe("Config") {

    it("should get A from ConfigReader") {
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

    it("should get case class from ConfigReader") {
      val config = ConfigFactory.parseString(
        """
          |user {
          | name = zdx
          | age = 29
          |}
        """.stripMargin)
      ConfigReader[User].read(config, "user") should be (Right(User("zdx", 29)))
    }

    it("should get Map[String, A] from ConfigReader") {
      val config = ConfigFactory.parseString(
        """
          |user {
          | name = zdx
          | age = 29
          |}
        """.stripMargin)
      val actual = ConfigReader[Map[String, String]].read(config, "user")
      val expected = Right(Map("name" -> "zdx", "age" -> "29"))
      actual should be (expected)
    }

    it("shoud get Map[String, List[A]] from ConfigReader") {
      val config = ConfigFactory.parseString(
        """
          |user {
          | name = [zdx, ygy]
          | age = [29, 18]
          |}
        """.stripMargin
      )
      val actual = ConfigReader[Map[String, List[String]]].read(config, "user")
      val expected = Right(Map("name" -> List("zdx", "ygy"), "age" -> List("29", "18")))
      actual should be (expected)
    }

    it("should get List[A <: AnyVal] from ConfigListReader") {
      val config = ConfigFactory.parseString(
        """
          |names = [zdx, ygy]
        """.stripMargin)
      ConfigListReader[String].read(config, "names") should be (Right(List("zdx", "ygy")))
    }

    it("should get List[A <: AnyRef] from ConfigListReader") {
      val config = ConfigFactory.parseString(
        """
          |users = [
          | {
          |   name = zdx
          |   age = 29
          | },
          | {
          |   name = ygy
          |   age = 18
          | }
          |]
        """.stripMargin)
      val expected = List(User("zdx", 29), User("ygy", 18))
      ConfigListReader[User].read(config, "users") should be (Right(expected))
    }

    it("should get A from Config") {
      val config = ConfigFactory.parseString("foo = 42")
      config.get[Int]("foo") should be (Right(42))
    }

    it("should get List[A <: Any] from Config") {
      val config = ConfigFactory.parseString(
        """
          |names = [zdx, ygy]
        """.stripMargin)
      config.getListFrom[String]("names") should be (Right(List("zdx", "ygy")))
    }
  }
}
