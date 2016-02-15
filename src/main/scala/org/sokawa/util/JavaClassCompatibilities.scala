/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.util

import java.lang.{ ClassLoader => JavaClassLoader, Thread => JavaThread}
import java.lang.reflect.{ Constructor, Modifier, Method }
import java.io.{ File => JavaFile }
import java.net.URL
import java.net.{ URLClassLoader => JavaURLClassLoader }
import scala.reflect.runtime.ReflectionUtils.unwrapHandler
import scala.util.control.Exception.{ catching }
import scala.language.implicitConversions
import scala.reflect.{ ClassTag, classTag }


trait JavaClassCompatibilities {
  implicit class ScalaEquivalentOfJavaClassLoader(classLoader: JavaClassLoader) {
    import ScalaEquivalentOfJavaClassLoader._
    // PLACEHOLDER.
  }

  object ScalaEquivalentOfJavaClassLoader {
    implicit def apply(classLoader: JavaClassLoader): ScalaClassLoader = classLoader match {
      case urlClassLoader: JavaURLClassLoader => new JavaURLClassLoader(urlClassLoader.getURLs, urlClassLoader.getParent)
      case _                                  => classLoader
    }
  }
}
