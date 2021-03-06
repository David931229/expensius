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

package com.mvcoding.expensius.feature.report

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.mvcoding.expensius.R
import com.mvcoding.expensius.extension.inflate
import com.mvcoding.expensius.extension.makeOutlineProviderOval
import com.mvcoding.expensius.model.Money
import com.mvcoding.expensius.model.Tag
import com.mvcoding.expensius.provideMoneyFormatter
import kotlinx.android.synthetic.main.item_view_tag_money.view.*

class TagMoneyItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        fun inflate(parent: ViewGroup) = parent.inflate<TagMoneyItemView>(R.layout.item_view_tag_money)
    }

    private val moneyFormatter by lazy { provideMoneyFormatter() }

    override fun onFinishInflate() {
        super.onFinishInflate()
        colorImageView.makeOutlineProviderOval()
        progressBar.max = 100
    }

    fun setTag(tag: Tag) {
        colorImageView.setColorFilter(tag.color.rgb)
        titleTextView.text = tag.title.text
        progressBar.progressTintList = ColorStateList.valueOf(tag.color.rgb)
    }

    fun setMoney(money: Money) {
        amountTextView.text = moneyFormatter.format(money)
    }

    fun setProgress(progress: Float) {
        progressBar.progress = (100 * progress).toInt()
    }
}