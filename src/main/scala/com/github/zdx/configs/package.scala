package com.github.zdx

import com.github.zdx.configs.ConfigReader.Result
import com.typesafe.config.Config

/**
  * Created by zhoudunxiong on 2018/5/3.
  */
package object configs {

  implicit class RichConfig(self: Config) {

    def get[A](path: String)(implicit cra: ConfigReader[A]): Result[A] =
      cra.read(self, path)

    def getListFrom[A](path: String)(implicit clra: ConfigListReader[A]): Result[List[A]] =
      clra.read(self, path)
  }
}
