/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.monad.arm


trait ResourceOperations[+A] extends Resource[A] { self =>
  override def apply[B](fetch: A => B): B = tryToAcquire(fetch).fold(list => throw list.head, x => x)

  override def acquire[B](fetch: A => B): B = apply(fetch)

  override def map[B](f: A => B): NullableResource[B] = new LazyNullableResource(self, f)

  override def flatMap[B](f: A => Resource[B]): Resource[B] = new ResourceOperations[B] {
    override def tryToAcquire[C](fetch: B => C): Either[List[Throwable], C] = self.tryToAcquire(e => f(e).tryToAcquire(fetch)).fold(e => Left(e), e => e)
  }

  override def foreach(f: A => Unit): Unit = acquire(f)

  override def product[B](that: Resource[B]): Resource[(A, B)] = cartesian(self, that)
}
