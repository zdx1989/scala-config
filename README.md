# scala-config

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

## 通过Config获取配置信息

通过Config获取基础配置信息
```scala
scala> val config = ConfigFactory.parseString("foo = 42")
config: com.typesafe.config.Config = Config(SimpleConfigObject({"foo":42}))

scala> config.get[Int]("foo")
res7: com.github.zdx.configs.ConfigReader.Result[Int] = Right(42)

scala> config.get[Double]("foo")
res10: com.github.zdx.configs.ConfigReader.Result[Double] = Right(42.0)
```

通过Config获取case class配置信息
```scala
scala> val config = ConfigFactory.parseString(
     |         """
     |           |user {
     |           | name = zdx
     |           | age = 29
     |           |}
     |         """.stripMargin)
config: com.typesafe.config.Config = Config(SimpleConfigObject({"user":{"age":29,"name":"zdx"}}))

scala> config.get[User]("user")
res11: com.github.zdx.configs.ConfigReader.Result[User] = Right(User(zdx,29))
```

通过Config获取List[A <: AnyVal]配置
```scala
scala> config.getListFrom[Int]("postcode")
res0: com.github.zdx.configs.ConfigReader.Result[List[Int]] = Right(List(423042, 423000))
```

通过Config获取List[A <: AnyRef]配置
```scala
scala> val config = ConfigFactory.parseString(
     |         """
     |           |users = [
     |           | {
     |           |   name = zdx
     |           |   age = 29
     |           | },
     |           | {
     |           |   name = ygy
     |           |   age = 18
     |           | }
     |           |]
     |         """.stripMargin)
config: com.typesafe.config.Config = Config(SimpleConfigObject({"users":[{"age":29,"name":"zdx"},{"age":18,"name":"ygy"}]}))

scala> case class User(name: String, age: Int)
defined class User

scala> config.getListFrom[User]("users")
res1: com.github.zdx.configs.ConfigReader.Result[List[User]] = Right(List(User(zdx,29), User(ygy,18)))

```

