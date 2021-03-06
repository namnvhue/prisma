package com.prisma.deploy.connector.postgres.database

import com.prisma.deploy.connector.jdbc.database.TypeMapper
import com.prisma.gc_values.GCValue
import com.prisma.shared.models.TypeIdentifier
import com.prisma.shared.models.TypeIdentifier.TypeIdentifier
import org.jooq.DSLContext

case class PostgresTypeMapper() extends TypeMapper {
  override def rawSQLFromParts(
      name: String,
      isRequired: Boolean,
      isList: Boolean,
      typeIdentifier: TypeIdentifier,
      isAutoGeneratedByDb: Boolean = false,
      defaultValue: Option[GCValue] = None
  )(implicit dsl: DSLContext): String = {
    val n        = esc(name)
    val nullable = if (isRequired) "NOT NULL" else "NULL"
    val ty       = rawSqlTypeForScalarTypeIdentifier(isList, typeIdentifier)
    val default  = defaultValue.map(d => s"DEFAULT ${d.value}").getOrElse("")

    s"$n $ty $nullable $default"
  }

  override def rawSqlTypeForScalarTypeIdentifier(isList: Boolean, t: TypeIdentifier.TypeIdentifier): String = t match {
    case _ if isList             => "text"
    case TypeIdentifier.String   => "text"
    case TypeIdentifier.Boolean  => "boolean"
    case TypeIdentifier.Int      => "int"
    case TypeIdentifier.Float    => "Decimal(65,30)"
    case TypeIdentifier.Cuid     => "varchar (25)"
    case TypeIdentifier.Enum     => "text"
    case TypeIdentifier.Json     => "text"
    case TypeIdentifier.DateTime => "timestamp (3)"
    case TypeIdentifier.UUID     => "uuid"
    case _                       => ???
  }

}
