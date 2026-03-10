package com.raywenderlich.airhockey.util

import android.location.Location.distanceBetween
import java.util.Vector
import kotlin.math.sqrt

class Geometry {

    data class Point(
        val x: Float,
        val y: Float,
        val z: Float
    ) {
        fun translateY(distance: Float): Point {
            return Point(x, y + distance, z)
        }

        fun translate(vector: Vector): Point {
            return Point(
                x + vector.x,
                y + vector.y,
                z + vector.z
            )
        }
    }

    data class Circle(
        val center: Point,
        val radius: Float
    ) {
        fun scale(scale: Float): Circle {
            return Circle(center, radius * scale)
        }
    }

    data class Cylinder(
        val center: Point,
        val radius: Float,
        val height: Float
    )

    data class Ray(
        val point: Point,
        val vector: Vector
    )

    data class Vector(
        val x: Float,
        val y: Float,
        val z: Float
    ) {
        fun length(): Float {
            return sqrt(x * x + y * y + z * z)
        }

        fun crossProduct(other: Vector): Vector {
            return Vector(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x)
            )
        }

        fun dotProduct(other: Vector): Float {
            return x * other.x + y * other.y + z * other.z
        }

        fun scale(f: Float): Vector {
            return Vector(
                x * f,
                y * f,
                z * f
            )
        }
    }

    data class Sphere(
        val center: Point,
        val radius: Float
    )

    data class Plane(
        val point: Point,
        val normal: Vector
    )

    companion object {
        fun vectorBetween(from: Point, to: Point): Vector {
            return Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z
            )
        }

        fun intersects(sphere: Sphere, ray: Ray): Boolean {
            return distanceBetween(sphere.center, ray) < sphere.radius
        }

        fun distanceBetween(point: Point, ray: Ray): Float {
            val p1ToPoint = vectorBetween(ray.point, point)
            val p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point)

            // The length of the cross product gives the area of a parallelogram
            val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length()
            val lengthOfBase = ray.vector.length()

            // Height of the triangle is the distance from the point to the ray
            val distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase
            return distanceFromPointToRay
        }

        fun intersectionPoint(ray: Ray, plane: Plane): Point {
            val rayToPlaneVector = vectorBetween(ray.point, plane.point)
            val scaleFactor = rayToPlaneVector.dotProduct(plane.normal) /
                    ray.vector.dotProduct(plane.normal)
            return ray.point.translate(ray.vector.scale(scaleFactor))
        }
    }
}