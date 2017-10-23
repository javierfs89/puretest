package org.hablapps.puretest

import cats.MonadError
import cats.data.StateT
import cats.syntax.all._

trait StateTMonadError extends LowerPriorityImplicits {

  implicit def stateTMonadError[S, F[_], E](implicit F: MonadError[F, E]) =
    new MonadError[StateT[F, S, ?], E]{

      def pure[A](a: A): StateT[F, S, A] = {
        StateT(s => F.pure(s, a))
      }

      def flatMap[A, B](fa: StateT[F, S, A])(f: A => StateT[F, S, B]): StateT[F, S, B] = fa.flatMap(f)

      def tailRecM[A, B](a: A)(f: A => StateT[F, S, Either[A, B]]): StateT[F, S, B] = ???

      def raiseError[A](e: E): StateT[F, S, A] =
        StateT(_ => F.raiseError(e))

      def handleErrorWith[A](sfa: StateT[F, S, A])(
          f: E => StateT[F, S, A]): StateT[F, S, A] =
        StateT[F, S, A] { s =>
          val fa = sfa.run(s)
          fa handleErrorWith { e =>
            f(e).run(s)
          }
        }
    }
}

trait LowerPriorityImplicits {

  implicit def toMonadError[P[_], E](implicit
      toE: PureTestError[E] => Option[E],
      fromE: E => PureTestError[E],
      ME: MonadError[P, PureTestError[E]]) =
    new MonadError[P, E] {
      def pure[A](a: A) = ME.pure(a)
      def flatMap[A,B](p: P[A])(f: A => P[B]) = ME.flatMap(p)(f)
      def tailRecM[A, B](a: A)(f: A => P[Either[A, B]]): P[B] = ME.tailRecM(a)(f)
      def raiseError[A](e: E) = ME.raiseError(fromE(e))
      def handleErrorWith[A](p: P[A])(f: E => P[A]) =
        p handleErrorWith { e2 =>
          toE(e2) match {
            case Some(e1) => f(e1)
            case None => ME.raiseError(e2)
          }
        }
    }

}
