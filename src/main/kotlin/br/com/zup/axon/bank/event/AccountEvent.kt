package br.com.zup.axon.bank.event

import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.AccountName
import br.com.zup.axon.bank.aggregate.Gender
import br.com.zup.axon.bank.aggregate.Money
import br.com.zup.axon.bank.aggregate.Tenant
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision
import org.springframework.web.bind.annotation.ResponseBody

data class CreateAccountCommand(@field:TargetAggregateIdentifier val id: AccountId, val name: AccountName, val gender: Gender, val money: Money)
@Revision("3.0")
data class AccountCreatedEvent @JsonCreator constructor(
        @JsonProperty("id") val id: AccountId,
        @JsonProperty("name") val name: AccountName,
        @JsonProperty("gender") val gender: Gender,    // added v 3.0
        @JsonProperty("balance") val balance: Money,
        @JsonProperty("tenant") val tenant: Tenant     // added v 2.0
        )




data class DepositMoneyCommand(@field:TargetAggregateIdentifier val accountId: AccountId,
                               val transactionId: String,
                               val money: Money)
@Revision("2.0")
data class MoneyDepositedEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money,
        @JsonProperty("tenant") val tenant: Tenant)
@Revision("1.0")
data class MoneyDepositRejectEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("money") val money: Money)



data class WithdrawMoneyCommand(@field:TargetAggregateIdentifier val accountId: AccountId, val transactionId: String, val money: Money)
@Revision("1.0")
data class MoneyWithdrawnEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money)

@Revision("1.0")
data class MoneyWithdrawRejectedEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money)


data class CloseAccountCommand(@field:TargetAggregateIdentifier val id: AccountId)
@Revision("1.0")
data class AccountClosedEvent @JsonCreator constructor(@JsonProperty("accountId") val accountId: AccountId)

@Revision("1.0")
data class AccountCloseRejectEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("balance") val balance: Money)