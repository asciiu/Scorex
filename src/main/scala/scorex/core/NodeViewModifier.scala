package scorex.core

import scorex.core.serialization.BytesSerializable
import scorex.core.transaction.Transaction
import scorex.core.transaction.box.proposition.Proposition

import scala.util.Try

trait NodeViewModifier extends BytesSerializable {
  self =>

  import NodeViewModifier.{ModifierId, ModifierTypeId}

  type M >: self.type <: NodeViewModifier

  val modifierTypeId: ModifierTypeId

  //todo: check statically or dynamically output size
  def id: ModifierId

  lazy val bytes: Array[Byte] = companion.bytes(this)

  def companion: NodeViewModifierCompanion[M]
}

/**
  * It is supposed that all the modifiers (offchain transactions, blocks, blockheaders etc)
  * are of the some length fixed with the ModifierIdSize constant
  */
object NodeViewModifier {
  type ModifierTypeId = Byte
  type ModifierId = Array[Byte]

  val ModifierIdSize: Int = 32 //todo: make configurable via application.conf
}

trait NodeViewModifierCompanion[M <: NodeViewModifier] {
  def bytes(modifier: M): Array[Byte]

  def parse(bytes: Array[Byte]): Try[M]
}

trait PersistentNodeViewModifier[P <: Proposition, TX <: Transaction[P]] extends NodeViewModifier {

  // with Dotty is would be Seq[TX] | Nothing
  def transactions: Option[Seq[TX]]
}