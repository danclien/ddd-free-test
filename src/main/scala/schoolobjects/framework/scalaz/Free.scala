package schoolobjects.framework.scalaz

import scalaz._, Scalaz._

trait FreeFunctions {
  /* Runs a Free monad created using Coyoneda */
  def runFC[F[_], M[_], A](op: Free.FreeC[F, A])(interp: F ~> M)(implicit M: Monad[M]): M[A] = {
    op.foldMap[M](new (({ type l[a] = Coyoneda[F, a]})#l ~> M) {
      def apply[A](cy: Coyoneda[F, A]): M[A] =
        M.map(interp(cy.fi))(cy.k)
      }
    )
  }
}