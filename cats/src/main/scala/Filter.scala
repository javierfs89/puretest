package org.hablapps.puretest

import cats.MonadError
import cats.syntax.applicative._

trait Filter[F[_]]{
  def filter[A](fa: F[A])(f: A => Boolean)(implicit
    F: sourcecode.File,
    L: sourcecode.Line): F[A]
}

object Filter {

  def apply[F[_]](implicit S: Filter[F]) = S

  // Use in for-comprehensions
  trait Syntax {
    implicit class FilterOps[F[_], A](fa: F[A])(implicit SF: Filter[F]){
      def filter(f: A => Boolean)(implicit F: sourcecode.File, L: sourcecode.Line): F[A] =
        SF.filter(fa)(f)
      def withFilter(f: A => Boolean)(implicit F: sourcecode.File, L: sourcecode.Line): F[A] =
        filter(f)
    }
  }

  object syntax extends Syntax

  type Location = (sourcecode.File, sourcecode.Line)

  case class LocationException(obtained: String, location: Location) extends Throwable {
    override def toString =
      s"returned value $obtained does not match pattern at ${location.toString}"
    override def getMessage = toString
  }

  def FilterForMonadError[F[_], E](error: (String, Location) => E)(implicit
      merror: MonadError[F, E]) =
    new Filter[F] {
      def filter[A](fa: F[A])(f: A => Boolean)(implicit
          F: sourcecode.File, L: sourcecode.Line): F[A] =
        merror.flatMap(fa) { a =>
          if (f(a)) a.pure[F]
          else merror.raiseError(error(a.toString, (F, L)))
        }
    }

  implicit def FilterForLocation[F[_]](
      implicit merror: MonadError[F, Location]) =
    FilterForMonadError((_, loc) => loc)

  implicit def FilterForThrowable[F[_]](
      implicit merror: MonadError[F, Throwable]) =
    FilterForMonadError[F, Throwable](LocationException(_, _))

  // TODO: Make an automatic lift from:
  //   * P that is MonadError[P, E]
  // to:
  //   * P' that is MonadError[P, Either[E, LocationException]]
  def FilterForTester[P[_], E](implicit
      T: Tester[P, E],
      ME: MonadError[P, Either[E, LocationException]]) =
    new Filter[P] {
      def filter[A](fa: P[A])(f: A => Boolean)(implicit
          F: sourcecode.File,
          L: sourcecode.Line): P[A] =
        T(fa).fold(
          e => ME.raiseError(Left(e)),
          a =>  if (f(a)) ME.pure(a)
                else ME.raiseError(Right(LocationException(a.toString, (F, L)))))
    }
}
