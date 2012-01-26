package scalax.io

import java.io.{Reader, BufferedReader}

/**
 * A ManagedResource for accessing and using Readers.  Class can be created using the [[scalax.io.Resource]] object.
 */
class ReaderResource[+A <: Reader] (
    opener: => A,
    closeAction:CloseAction[A],
    descName:ResourceDescName)
  extends ReadCharsResource[A]
  with ResourceOps[A, ReaderResource[A]] {

  def open():OpenedResource[A] = new CloseableOpenedResource(opener,closeAction)
  def unmanaged = new ReaderResource[A](opener, closeAction, descName) {
    private[this] val resource = opener
    override def open = new UnmanagedOpenedResource(resource, closeAction)
  }

  def prependCloseAction[B >: A](newAction: CloseAction[B]) = new ReaderResource(opener,newAction :+ closeAction,descName)
  def appendCloseAction[B >: A](newAction: CloseAction[B]) = new ReaderResource(opener,closeAction +: newAction,descName)

  override def chars : LongTraversable[Char]= ResourceTraversable.readerBased(this.open)

  override def toString: String = "ReaderResource("+descName.name+")"
}
