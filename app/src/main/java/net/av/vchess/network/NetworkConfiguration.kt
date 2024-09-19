package net.av.vchess.network

object NetworkConfiguration {
    private const val base = 56005
    val GameInformerPorts: List<Int> = listOf(base, base + 1, base + 2, base + 3)
    val GamePorts: List<Int> = listOf(base + 10, base + 11, base + 12, base + 13)
}