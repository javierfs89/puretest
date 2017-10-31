package org.hablapps.puretest

import scalaz.{Monad, StateT, ~>}

trait Implicits {

  implicit def toPuretestError[E](e: E): PuretestError[E] = ApplicationError(e)
  implicit def liftEither[E1 <% E2, E2] =
    λ[Either[E1, ?] ~> Either[E2, ?]] { either =>
      either.fold(
        e => Left(e),
        a => Right(a))
    }
  implicit def stateLiftEffect[F[_]: Monad, G[_]: Monad, S](implicit
      nat: F ~> G): StateT[F, S, ?] ~> StateT[G, S, ?] =
    λ[StateT[F, S, ?] ~> StateT[G, S, ?]] { state =>
      StateT { s =>
        nat(state.run(s))
      }
    }

  implicit class LiftInstanceOps[F[_], A](fa: F[A]) {
    def liftTo[G[_]](implicit nat: F ~> G) = nat(fa)
  }

}
