package org.hablapps.puretest

import cats.MonadError

trait MonadErrorUtils {

  trait RaiseError[P[_],E]{
    def raiseError[A](e: E): P[A]
  }

  object RaiseError{
    def apply[P[_], E](implicit RE: RaiseError[P,E]) = RE

    implicit def raiseErrorEither[E] =
      new RaiseError[Either[E, ?], E] {
        def raiseError[A](e: E): Either[E, A] = Left(e)
      }

    implicit def fromMonadError[P[_], E](implicit ME: MonadError[P, E]) =
      new RaiseError[P, E]{
        def raiseError[A](e: E) = ME.raiseError(e)
      }
  }

  trait HandleError[P[_],E]{
    def handleError[A](p: P[A])(f: E => P[A]): P[A]
  }

  object HandleError {
    def apply[P[_],E](implicit HE: HandleError[P,E]) = HE

    implicit def handleErrorEither[E]: HandleError[Either[E, ?], E] =
      new HandleError[Either[E, ?], E] {
        def handleError[A](p: Either[E, A])(f: E => Either[E, A]): Either[E, A] =
          p.fold(f, Right(_))
      }

    implicit def fromMonadError[P[_],E](implicit ME: MonadError[P,E]) =
      new HandleError[P,E] {
        def handleError[A](p: P[A])(f: E => P[A]) = ME.handleErrorWith(p)(f)
      }
  }

}
