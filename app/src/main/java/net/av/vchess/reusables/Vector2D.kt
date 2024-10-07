package net.av.vchess.reusables

import android.nfc.FormatException
import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class Vector2D(val x: Int, val y: Int) {
    companion object{
        fun fromString(str:String):Vector2D{
            try {
                val x = str.substring(1, str.indexOf(",")).toInt()
                val y = str.substring(str.indexOf(",") + 2, str.indexOf("}")).toInt()
                return Vector2D(x, y)
            }
            catch (e:Exception){
                throw IllegalArgumentException("Vector is not formatted correctly.")
            }
        }
    }
    override fun equals(other: Any?): Boolean {
        if (other is Vector2D) {
            return x == other.x && y == other.y
        }
        return super.equals(other)
    }

    operator fun plus(another: Vector2D): Vector2D {
        return Vector2D(x + another.x, y + another.y)
    }

    operator fun minus(another: Vector2D): Vector2D {
        return Vector2D(x - another.x, y - another.y)
    }

    operator fun times(number: Int): Vector2D {
        return Vector2D(x * number, y * number)
    }

    fun sameDirection(other: Vector2D): Boolean {
        val distance = Vector2D(other.x - x, other.y - y)
        val distanceTan: Double = distance.y / distance.x.toDouble()
        val selfTan: Double = y / x.toDouble()

        //This if statement check if the other vector is pointing in the same direction OR in the opposite direction:
        if (distanceTan == selfTan) {
            //This check if they are either both negative or both positive:
            if (distance.x / abs(distance.x) == x / abs(x)) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "{$x, $y}"
    }
}