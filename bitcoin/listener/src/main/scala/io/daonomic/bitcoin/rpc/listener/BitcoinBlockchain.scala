package io.daonomic.bitcoin.rpc.listener

import java.math.BigInteger

import cats.Monad
import cats.implicits._
import io.daonomic.bitcoin.rpc.core.{Bitcoind, RestBitcoind}
import io.daonomic.bitcoin.rpc.domain
import io.daonomic.blockchain.{BalanceChange, Blockchain, Transaction}
import io.daonomic.rpc.domain.Bytes

import scala.language.higherKinds

class BitcoinBlockchain[F[_]](bitcoind: Bitcoind[F], restBitcoind: RestBitcoind[F])
                             (implicit m: Monad[F])
  extends Blockchain[F] {

  override def blockNumber: F[BigInteger] =
    bitcoind.getBlockCount

  override def getTransactionIdsByBlock(block: BigInteger): F[List[Bytes]] =
    bitcoind.getBlockHash(block)
      .flatMap(restBitcoind.getBlockSimple)
      .map(_.tx)

  override def getTransactionsByBlock(block: BigInteger): F[List[Transaction]] =
    bitcoind.getBlockHash(block)
      .flatMap(restBitcoind.getBlockFull)
      .map(block => block.tx.map(tx => new BitcoinTransaction(tx)))
}

class BitcoinTransaction(tx: domain.Transaction) extends Transaction {
  override def id: String =
    tx.txid

  override def outputs: List[BalanceChange] =
    tx.vout
        .filter(out => out.scriptPubKey != null && out.scriptPubKey.addresses != null && out.scriptPubKey.addresses.size == 1)
        .map(out => BalanceChange(out.scriptPubKey.addresses.head, BigInteger.valueOf((out.value * 100000000).toLong)))
}