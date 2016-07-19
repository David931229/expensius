/*
 * Copyright (C) 2016 Mantas Varnagiris.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.mvcoding.expensius.feature

import com.mvcoding.expensius.model.Transaction
import com.mvcoding.expensius.model.TransactionState
import com.mvcoding.expensius.model.TransactionType
import org.joda.time.Interval

data class FilterData(
        val transactionType: TransactionType? = null,
        val transactionState: TransactionState? = null,
        val interval: Interval? = null) {

    fun withTransactionType(transactionType: TransactionType?) = copy(transactionType = transactionType)
    fun withTransactionState(transactionState: TransactionState?) = copy(transactionState = transactionState)
    fun withInterval(interval: Interval?) = copy(interval = interval)

    fun filter(transactions: List<Transaction>): List<Transaction> = transactions.filter { transaction ->
        transactionType?.let { transaction.transactionType == it } ?: true
                && transactionState?.let { transaction.transactionState == it } ?: true
                && interval?.let { it.contains(transaction.timestamp) } ?: true
    }
}