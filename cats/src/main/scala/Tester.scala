package org.hablapps.puretest

import cats.~>

/**
 * Testers
 */
trait Tester[P[_], E] extends (P ~> Either[E, ?])

object Tester {
  def apply[P[_], E](implicit T: Tester[P, E]) = T

  /* Testing either programs */

  implicit def eitherTester[E]: Tester[Either[E, ?], E] =
    new Tester[Either[E, ?], E]{
      def apply[X](e: Either[E, X]) = e
    }

  /* Testing asynch programs */

  import scala.concurrent.{Await, Future, duration}, duration._
  import scala.util.Try
  import scala.util.{Success, Failure}

  implicit def futureTester: Tester[Future, Throwable] =
    new Tester[Future, Throwable]{
      def apply[X](f: Future[X]) =
        Try(Await.result(f, 60 second)) match { // Make this configurable
          case Success(s) => Right(s)
          case Failure(t) => Left(t)
        }
    }

  /* Validated programs */

  import cats.data.Validated

  implicit def validatedTester[E]: Tester[Validated[E, ?], E] =
    new Tester[Validated[E, ?], E] {
      def apply[A](fa: Validated[E, A]): Either[E, A] = fa.toEither
    }

  /* Lift Testing programs */

  type ExtP[P[_], A] = Either[Filter.LocationException, P[A]]

  implicit def liftTester[P[_], E](implicit T: Tester[P, E]) =
    new Tester[P, Either[E, Filter.LocationException]] {
      def apply[A](fa: P[A]): Either[Either[E, Filter.LocationException], A] =
        T(fa).fold(
          e => Left(Left(e)),
          Right(_))
    }

  implicit def liftTester2[P[_], E](implicit T: Tester[P, E]) =
    // new Tester[λ[α => Either[Filter.LocationException, P[α]]], Either[Filter.LocationException, E]] {
      // def apply[A](fa: Either[Filter.LocationException, P[A]]): Either[Either[Filter.LocationException, E], A] =
    new Tester[ExtP[P, ?], Either[Filter.LocationException, E]] {
      def apply[A](fa: ExtP[P, A]): Either[Either[Filter.LocationException, E], A] =
        fa.fold(
          le => Left(Left(le)),
          pa => T(pa).fold(
            e => Left(Right(e)),
            a => Right(a)))
    }

  /* Composing testers */

  implicit def composedTester[F[_], G[_], E](implicit
      F: Tester[F, E],
      G: Tester[G, E]): Tester[λ[α => F[G[α]]], E] =
    new Tester[λ[α => F[G[α]]], E] {
      def apply[A](fa: F[G[A]]): Either[E, A] =
        F(fa).right.flatMap(G.apply)
    }

  implicit def composedTesterEither[F[_], G[_], EF, EG](implicit // Revisit this (interpret from inner to outer using map ?)
      F: Tester[F, EF],
      G: Tester[G, EG]): Tester[λ[α => F[G[α]]], Either[EF, EG]] =
    new Tester[λ[α => F[G[α]]], Either[EF, EG]] {
      def apply[A](fa: F[G[A]]): Either[Either[EF, EG], A] =
        F(fa) match {
          case Left(ef) => Left(Left(ef))
          case Right(ga) => G(ga) match {
            case Left(eg) => Left(Right(eg))
            case Right(a) => Right(a)
          }
        }
    }

  def composedTesterOuter[F[_], G[_], EF, EG](f: EG => EF)(implicit // Revisit this
      F: Tester[F, EF],
      G: Tester[G, EG]): Tester[λ[α => F[G[α]]], EF] =
    new Tester[λ[α => F[G[α]]], EF] {
      def apply[A](fa: F[G[A]]): Either[EF, A] =
        F(fa).right flatMap { ga =>
          G(ga).fold(
            f andThen Left.apply,
            Right.apply)
        }
    }

  def composedTesterInner[F[_], G[_], EF, EG](f: EF => EG)(implicit // Revisit this
      F: Tester[F, EF],
      G: Tester[G, EG]): Tester[λ[α => F[G[α]]], EG] =
    new Tester[λ[α => F[G[α]]], EG] {
      def apply[A](fa: F[G[A]]): Either[EG, A] =
        F(fa).fold(
          f andThen Left.apply,
          G.apply)
    }

}
