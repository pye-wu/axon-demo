package br.com.zup.axon.bank.saga

import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.Money
import br.com.zup.axon.bank.event.CompleteMoneyTransferCommand
import br.com.zup.axon.bank.event.DepositMoneyCommand
import br.com.zup.axon.bank.event.FailMoneyTransferCommand
import br.com.zup.axon.bank.event.MoneyDepositRejectEvent
import br.com.zup.axon.bank.event.MoneyDepositedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawRejectedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawnEvent
import br.com.zup.axon.bank.event.TransferMoneyRequestedEvent
import br.com.zup.axon.bank.event.WithdrawMoneyCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
final class BankTransferSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    lateinit var transactionId: String
        private set

    lateinit var sourceId: AccountId
        private set

    lateinit var destinationId: AccountId
        private set

    var amount: Money? = null
        private set

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: TransferMoneyRequestedEvent) {
        this.transactionId = event.transactionId
        this.sourceId = event.sourceId
        this.destinationId = event.destinationId
        this.amount = event.amount

        commandGateway.send(WithdrawMoneyCommand(event.sourceId, event.transactionId, event.amount),
                            LoggingCallback.INSTANCE)

    }

    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyWithdrawnEvent) {
        commandGateway.send(DepositMoneyCommand(this.destinationId, event.transactionId, event.money),
                            LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyWithdrawRejectedEvent) {
        commandGateway.send(FailMoneyTransferCommand(event.transactionId),
                            LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyDepositRejectEvent) {
        commandGateway.send(DepositMoneyCommand(this.sourceId, event.transactionId, event.money),
                            LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyDepositedEvent) {
        if (event.accountId == this.destinationId) {
            commandGateway.send(CompleteMoneyTransferCommand(event.transactionId),
                                LoggingCallback.INSTANCE)
        } else {
            commandGateway.send(FailMoneyTransferCommand(event.transactionId),
                                LoggingCallback.INSTANCE)
        }
    }

}