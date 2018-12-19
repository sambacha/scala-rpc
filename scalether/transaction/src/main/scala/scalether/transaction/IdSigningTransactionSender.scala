package scalether.transaction

import java.math.BigInteger

import cats.Id
import io.daonomic.cats.implicits._
import io.daonomic.rpc.domain.Word
import scalether.core.IdEthereum
import scalether.domain.request.Transaction
import scalether.sync.IdSynchronizer

class IdSigningTransactionSender(ethereum: IdEthereum, nonceProvider: IdNonceProvider, synchronizer: IdSynchronizer, privateKey: BigInteger, gas: BigInteger, gasPrice: IdGasPriceProvider)
  extends SigningTransactionSender[Id](ethereum, nonceProvider, synchronizer, privateKey, gas, gasPrice) with IdTransactionSender {

  override def sendTransaction(transaction: Transaction): Word =
    super.sendTransaction(transaction)
}
