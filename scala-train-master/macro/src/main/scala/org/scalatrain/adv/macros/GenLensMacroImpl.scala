package org.scalatrain.adv.macros

import scalaz.Lens


object GenLensMacroImpl {
  type Context = scala.reflect.macros.blackbox.Context

  def genLensImpl[S: c.WeakTypeTag, A: c.WeakTypeTag](c: Context)(field: c.Expr[S => A]): c.Expr[Lens[S, A]] = {
    import c.universe._

    /** Extractor for member select chains.
        e.g.: SelectChain.unapply(a.b.c) == Some("a",Seq(a.type -> "b", a.b.type -> "c")) */
    object SelectChain {
      def unapply(tree: Tree): Option[(Name, Seq[(Type, TermName)])] = tree match {
        case Select(tail@Ident(termUseName), field: TermName) =>
          Some((termUseName, Seq(tail.tpe.widen -> field)))
        case Select(tail, field: TermName) => SelectChain.unapply(tail).map(
          t => t.copy(_2 = t._2 :+ (tail.tpe.widen -> field))
        )
        case _ => None
      }
    }

    field match {
      // _.field
      case Expr(
        Function(
          List(ValDef(_, termDefName, _, EmptyTree)),
          Select(Ident(termUseName), fieldNameName)
        )
      ) if termDefName.decodedName.toString == termUseName.decodedName.toString =>
        val fieldName = fieldNameName.decodedName.toString
        mkLensImpl[S, A](c)(c.Expr[String](q"$fieldName"))

      case _ => c.abort(c.enclosingPosition, s"Illegal field reference ${show(field.tree)}")
    }
  }

  def mkLensImpl[S: c.WeakTypeTag, A: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]): c.Expr[Lens[S, A]] = {
    import c.universe._

    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val strFieldName = c.eval(c.Expr[String](resetLocalAttrs(c)(fieldName.tree.duplicate)))

    val fieldMethod = getDeclarations(c)(sTpe).collectFirst {
      case m: MethodSymbol if m.isCaseAccessor && m.name.decodedName.toString == strFieldName => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $strFieldName in $sTpe"))

    val constructor = getDeclarations(c)(sTpe).collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $sTpe"))

    val field = getParameterLists(c)(constructor).head
      .find(_.name.decodedName.toString == strFieldName)
      .getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $sTpe"))

    c.Expr[Lens[S, A]]( q"""
      import scalaz.Lens
      Lens.lensu[$sTpe, $aTpe] ((s: $sTpe, a: $aTpe) => s.copy($field = a),
      (s: $sTpe) => s.$fieldMethod)
    """)
  }

  def mkLensImpl2[S: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]) = {
    import c.universe._

    val sTpe = weakTypeOf[S]

    val q"$asdf" = fieldName.tree
    val strFieldName = asdf.toString.drop(1).dropRight(1)

    println(strFieldName)

/*    val fieldMethod = getDeclarations(c)(sTpe).collectFirst {
      case m: MethodSymbol if m.isCaseAccessor && m.name.decodedName.toString == strFieldName => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $strFieldName in $sTpe"))*/

    val constructor = getDeclarations(c)(sTpe).collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $sTpe"))

    println(strFieldName  )
    println(show(getParameterLists(c)(constructor).head.map(_.name.decodedName.toString)))

    val field = getParameterLists(c)(constructor).head
      .find(_.name.decodedName.toString == strFieldName)
      .getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $strFieldName in $sTpe"))

    val aTpe = field.typeSignature

    c.Expr( q"""
      import scalaz.Lens
      Lens.lensu[$sTpe, $aTpe] ((s: $sTpe, a: $aTpe) => s.copy($field = a),
      (s: $sTpe) => s.$field)
    """)
  }

  def getDeclarations(c: Context)(tpe: c.universe.Type): c.universe.MemberScope =
    tpe.decls

  def getParameterLists(c: Context)(method: c.universe.MethodSymbol): List[List[c.universe.Symbol]] =
    method.paramLists

  def getDeclaration(c: Context)(tpe: c.universe.Type, name: c.universe.Name): c.universe.Symbol =
    tpe.decl(name)

  def createTermName(c: Context)(name: String): c.universe.TermName =
    c.universe.TermName(name)

  def createTypeName(c: Context)(name: String): c.universe.TypeName =
    c.universe.TypeName(name)

  def resetLocalAttrs(c: Context)(tree: c.Tree): c.Tree =
    c.untypecheck(tree)

  def getTermNames(c: Context): c.universe.TermNamesApi =
    c.universe.termNames

  def companionTpe(c: Context)(tpe: c.universe.Type): c.universe.Symbol =
    tpe.typeSymbol.companion

}