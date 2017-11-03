package org.hablapps.puretest


import cats.Monad
import cats.syntax.all._

trait PureMatchersTC[P[_], E] {

  /* Evidences */

  implicit val M: Monad[P]
  val HE: HandleError[P, E]
  val RE: RaiseError[P, PuretestError[E]]

  /* Matchers */

  def shouldFail[A](pa: P[A])(p: E => Boolean)(
      errorIfSuccess: A => PuretestError[E],
      errorIfFailure: E => PuretestError[E]): P[Unit] =
    HE.handleError(pa >>= (errorIfSuccess andThen RE.raiseError[Unit])) { e =>
      if (p(e)) M.pure(())
      else RE.raiseError(errorIfFailure(e))
    }

  def shouldSucceed[A](pa: P[A])(p: A => Boolean)(
      errorIfSuccess: A => PuretestError[E],
      errorIfFailure: E => PuretestError[E]): P[A] =
    HE.handleError(pa)(errorIfFailure andThen RE.raiseError) >>= { a =>
      if (p(a)) M.pure(a)
      else RE.raiseError(errorIfSuccess(a))
    }

}

object PureMatchersTC {
  def apply[P[_], E](implicit PM: PureMatchersTC[P, E]) = PM

  implicit def pureMatchersFromEvidences[P[_], E](implicit
      M2: Monad[P],
      HE2: HandleError[P, E],
      RE2: RaiseError[P, PuretestError[E]]) =
    new PureMatchersTC[P, E] {
      val M = M2
      val HE = HE2
      val RE = RE2
    }

  object syntax extends Syntax
  trait Syntax {
    implicit class FilterOps[P[_], A, E](self: P[A])(implicit PM: PureMatchersTC[P, E]) {

      def filter(f: A => Boolean)(implicit
          F: sourcecode.File, L: sourcecode.Line): P[A] =
        self shouldMatch f

      def withFilter(f: A => Boolean)(implicit
          F: sourcecode.File, L: sourcecode.Line): P[A] =
        filter(f)

    }

    implicit class PureMatchersOps[P[_], A](self: P[A]) {
      // Failure

      def shouldMatchFailure[E](p: E => Boolean)(implicit PM: PureMatchersTC[P, E]): P[Unit] =
        PM.shouldFail(self)(p)(NotFailed(_), NotMatchedFailure(_))

      def shouldFail[E](implicit PM: PureMatchersTC[P, E]): P[Unit] =
        PM.shouldFail(self)(_ => true)(NotFailed(_), _ => ShouldNotHappen())

      def shouldFailWith[E](e: E)(implicit PM: PureMatchersTC[P, E]): P[Unit] =
        PM.shouldFail(self)(_ == e)(NotError(_, e), OtherError(_, e))

      // Success

      def shouldMatch[E](p: A => Boolean)(implicit PM: PureMatchersTC[P, E]): P[A] =
        PM.shouldSucceed(self)(p)(NotMatched(_), NotSucceeded(_))

      def shouldBe[E](a: A)(implicit PM: PureMatchersTC[P, E]): P[A] =
        PM.shouldSucceed(self)(_ == a)(NotEqualTo(_, a), NotValue(_, a))

      def shouldSucceed[E](implicit PM: PureMatchersTC[P, E]): P[A] =
        PM.shouldSucceed(self)(_ => true)(_ => ShouldNotHappen(), NotSucceeded(_))

    }
  }
}
