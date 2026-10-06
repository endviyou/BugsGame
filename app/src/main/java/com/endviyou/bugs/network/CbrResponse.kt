package com.endviyou.bugs.network

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element

/**
 * Корневой элемент XML от ЦБ
 */
@Root(name = "Metall", strict = false)
data class CbrResponse(
    @field:ElementList(name = "Record", inline = true, required = false)
    var records: MutableList<MetalRecord> = mutableListOf()
)

/**
 * Одна запись (один металл)
 */
@Root(name = "Record", strict = false)
data class MetalRecord(
    @field:Element(name = "Buy", required = false)
    var buy: String = "",

    @field:Element(name = "Sell", required = false)
    var sell: String = "",

    @field:Attribute(name = "Code", required = false)
    var code: String = "",

    @field:Attribute(name = "Date", required = false)
    var date: String = ""
)