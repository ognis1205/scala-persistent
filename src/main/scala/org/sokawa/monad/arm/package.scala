/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.monad

import java.lang.{ VirtualMachineError => JavaVirtualMachineError }
import java.nio.file.LinkOption
import java.nio.file.StandardCopyOption
import scala.reflect.runtime.universe.TypeTag
import scala.util.control.ControlThrowable


package object arm {
  type Closeable = {
    def close(): Unit
  }

  type Disposable = {
    def dispose(): Unit
  }

  private[arm] trait ResourceHandler[A] {
    def open(resource: A): Unit = ()

    def close(resource: A): Unit

    def closeAfter(resource: A, throwable: Throwable): Unit = close(resource)

    def isFatalException(throwable: Throwable): Boolean = throwable match {
      case _: JavaVirtualMachineError => true
      case _                          => false
    }

    def isRethrownException(throwable: Throwable): Boolean = throwable match {
      case _: ControlThrowable     => true
      case _: InterruptedException => true
      case _                       => false
    }
  }

  private[arm] sealed trait ImplicationsOfResourceHandler {
    implicit def closeableResourceHandler[A <: Closeable] = new ResourceHandler[A] {
      override def close(resource: A) = resource.close
    }

    implicit def disposableResourceHandler[A <: Disposable] = new ResourceHandler[A] {
      override def close(resource: A) = resource.dispose
    }
  }

  private[arm] object ResourceHandler extends ImplicationsOfResourceHandler

  object IOMonad {
    def apply[A: ResourceHandler: TypeTag](fetch: => A): Resource[A] = new DefaultResource(fetch)
  }

  private[arm] def cartesian[A, B](left: Resource[A], right: Resource[B]): Resource[(A, B)] =
    left flatMap {
      leftElement => right map {
        rightElement => (leftElement, rightElement)
      }
    }
}
