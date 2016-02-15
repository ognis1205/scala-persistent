/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.monad.arm

import java.lang.{ VirtualMachineError => JavaVirtualMachineError }
import scala.reflect.runtime.universe.TypeTag
import scala.util.control.Exception
import scala.util.control.Exception.Catch
import scala.util.control.Exception.mkThrowableCatcher
import scala.util.control.ControlThrowable


abstract class AbstractResource[A] extends Resource[A] with ResourceOperations[A] {
  protected def open: A

  protected def close(resource: A, error: Option[Throwable]): Unit

  protected def isRethrown(throwable: Throwable): Boolean = throwable match {
    case _: ControlThrowable     => true
    case _: InterruptedException => true
    case _                       => false
  }

  protected def isFatal(throwable: Throwable): Boolean = throwable match {
    case _: JavaVirtualMachineError => true
    case _                          => false
  }

  private final val catchIfNotFatal: Catch[Nothing] = new Catch(mkThrowableCatcher(e => !isFatal(e), throw _), None, _ => false) withDesc "<not fatal>"

  override def tryToAcquire[B](fetch: A => B): Either[List[Throwable], B] = {
    import Exception._
    val resource = open
    val acquired = catchIfNotFatal either fetch(resource)
    val closed   = catchIfNotFatal either close(resource, acquired.left.toOption)
      (acquired, closed) match {
      case (Left(throwable1), _               ) if isRethrown(throwable1) => throw throwable1
      case (Left(throwable1), Left(throwable2))                           => Left(throwable1 :: throwable2 :: Nil)
      case (Left(throwable1), _               )                           => Left(throwable1 :: Nil)
      case (_,                Left(throwable2))                           => Left(throwable2 :: Nil)
      case (Right(right),     _               )                           => Right(right)
    }
  }
}

private[arm] class LazyNullableResource[+A, B](val resource: Resource[B], val translate: B => A) extends NullableResource[A] with ResourceOperations[A] { self =>
  override def tryToAcquire[C](fetch: A => C): Either[List[Throwable], C] = resource tryToAcquire (translate andThen fetch)

  override def either: Either[Seq[Throwable], A] = resource tryToAcquire translate

  override def opt: Option[A] = either.right.toOption
}

final class DefaultResource[A: ResourceHandler: TypeTag](source: => A) extends AbstractResource[A] { self =>
  protected val typeTrait = implicitly[ResourceHandler[A]]

  override protected def open: A = {
    val resource = source
    typeTrait.open(resource)
    resource
  }

  override protected def close(resource: A, error: Option[Throwable]): Unit = error match {
    case None            => typeTrait.close(resource)
    case Some(throwable) => typeTrait.closeAfter(resource, throwable)
  }

  override protected def isFatal(throwable: Throwable): Boolean = typeTrait isFatalException throwable

  override protected def isRethrown(throwable: Throwable): Boolean = typeTrait isRethrownException throwable
}
