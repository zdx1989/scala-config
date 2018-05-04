# scala-jdbc

scala + typesafe config

## 通过ConfigReader获取配置信息

通过ConfigReader获取基础配置信息
```scala
scala> import com.typesafe.config.{ConfigException, ConfigFactory}
import com.typesafe.config.{ConfigException, ConfigFactory}
scala> import com.github.zdx.configs._
import com.github.zdx.configs._
scala> ConfigReader[Int].read(config, "foo")
res1: com.github.zdx.configs.ConfigReader.Result[Int] = Right(42)
scala> ConfigReader[Double].read(config, "foo")
res3: com.github.zdx.configs.ConfigReader.Result[Double] = Right(42.0)
```

通过ConfigReader获取case class配置信息
```scala
case class User(name: String, age: Int)
scala> val config = ConfigFactory.parseString(
|         """
|           |user {
|           | name = zdx
|           | age = 29
|           |}
|         """.stripMargin)
config: com.typesafe.config.Config = Config(SimpleConfigObject({"user":{"age":29,"name":"zdx"}}))
scala> import ConfigObjectReader._
import ConfigObjectReader._
scala> ConfigReader[User].read(config, "user")
res5: com.github.zdx.configs.ConfigReader.Result[User] = Right(User(zdx,29))
```


