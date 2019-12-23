import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val wireLayouts = readWireLayouts()
    val wireLayout1 = wireLayouts[0]
    val wireLayout2 = wireLayouts[1]

    val wireLayoutAsSegments1 = toSegments(wireLayout1)
    val wireLayoutAsSegments2 = toSegments(wireLayout2)
    val intersections = findIntersections(wireLayoutAsSegments1, wireLayoutAsSegments2)
    val intersectionsWithoutCenterPoint = intersections.dropWhile { it.point == Point(0, 0) }

    calculateClosestIntersectionManhattanDistance(intersectionsWithoutCenterPoint)
    calculateIntersectionWithLowestTimingNumberOfSteps(intersectionsWithoutCenterPoint)
}

fun calculateClosestIntersectionManhattanDistance(intersectionsWithoutCenterPoint: List<Intersection>) {
    val closestIntersection = intersectionsWithoutCenterPoint.minBy {
        manhattanDistanceFromCenter(it.point)
    }
    closestIntersection?.let {
        println(manhattanDistanceFromCenter(it.point))
    } ?: throw RuntimeException("No intersection found!")
}

fun calculateIntersectionWithLowestTimingNumberOfSteps(intersectionsWithoutCenterPoint: List<Intersection>) {
    val intersectionWithLowestTiming = intersectionsWithoutCenterPoint.minBy {
        wiresTiming(it)
    }
    intersectionWithLowestTiming?.let {
        println(wiresTiming(it))
    } ?: throw RuntimeException("No intersection found!")
}

fun wiresTiming(intersection: Intersection): Int {
    with (intersection) {
        val verticalSegment = if (segment1.isVertical) { segment1 } else { segment2 }
        val horizontalSegment = if (segment1.isHorizontal) { segment1 } else { segment2 }

        val additionalStepsFromStartToIntersectionForVertical = abs(horizontalSegment.start.y - verticalSegment.start.y)
        val additionalStepsFromStartToIntersectionForHorizontal = abs(horizontalSegment.start.x - verticalSegment.start.x)
        return verticalSegment.stepsForWireAtStart + horizontalSegment.stepsForWireAtStart +
                additionalStepsFromStartToIntersectionForHorizontal + additionalStepsFromStartToIntersectionForVertical
    }
}

fun manhattanDistanceFromCenter(it: Point) = abs(it.x) + abs(it.y)

fun findIntersections(wireLayoutAsSegments1: List<Segment>, wireLayoutAsSegments2: List<Segment>): List<Intersection> {
    val segmentPairs = wireLayoutAsSegments1.flatMap { wire1Segment ->
        wireLayoutAsSegments2.map { wire2Segment ->
            Pair(wire1Segment, wire2Segment)
        }
    }
    return segmentPairs
        .filter { (wire1Segment, wire2Segment) -> wire1Segment intersectsWith wire2Segment }
        .map { (wire1Segment, wire2Segment) ->
            Intersection(
                calculateIntersectionPoint(wire1Segment, wire2Segment),
                wire1Segment,
                wire2Segment)
        }
}

fun calculateIntersectionPoint(segment1: Segment, segment2: Segment): Point {
    val verticalSegment = if (segment1.isVertical) { segment1 } else { segment2 }
    val horizontalSegment = if (segment1.isHorizontal) { segment1 } else { segment2 }

    return Point(verticalSegment.start.x, horizontalSegment.start.y)
}

fun toSegments(wireLayout: List<DrawCommand>, startingPoint: Point = Point(0, 0), stepsMade: Int = 0): List<Segment> {
    if (wireLayout.isEmpty()) {
        return emptyList()
    }
    val nextPoint = applyCommand(startingPoint, wireLayout.first())
    val newSegment = Segment(startingPoint, nextPoint, stepsMade)
    return listOf(newSegment) + toSegments(wireLayout.drop(1), nextPoint, stepsMade + wireLayout.first().steps)
}

fun applyCommand(position: Point, drawCommand: DrawCommand): Point {
    return when(drawCommand.direction) {
        DrawDirection.UP -> Point(position.x, position.y + drawCommand.steps)
        DrawDirection.DOWN -> Point(position.x, position.y - drawCommand.steps)
        DrawDirection.RIGHT -> Point(position.x + drawCommand.steps, position.y)
        DrawDirection.LEFT -> Point(position.x - drawCommand.steps, position.y)
    }
}

data class Point(val x: Int,
                 val y: Int)

data class Intersection(val point: Point,
                        val segment1: Segment,
                        val segment2: Segment)

data class Segment(val start: Point,
                   val end: Point,
                   val stepsForWireAtStart: Int)

/**
 * Remark: works only for vertical or horizontal segments - it's not validated.
 */
infix fun Segment.intersectsWith(segment2: Segment): Boolean {
    val segment1 = this

    if (segment1.isVertical && segment2.isVertical || segment1.isHorizontal && segment2.isHorizontal) {
        return false
    }

    val verticalSegment = if (segment1.isVertical) { segment1 } else { segment2 }
    val horizontalSegment = if (segment1.isHorizontal) { segment1 } else { segment2 }

    val smallerX = min(horizontalSegment.start.x, horizontalSegment.end.x)
    val largerX = max(horizontalSegment.start.x, horizontalSegment.end.x)
    val smallerY = min(verticalSegment.start.y, verticalSegment.end.y)
    val largerY = max(verticalSegment.start.y, verticalSegment.end.y)

    return verticalSegment.start.x in smallerX..largerX &&
            horizontalSegment.start.y in smallerY..largerY
}

val Segment.isVertical: Boolean
    get() = start.x == end.x && start.y != end.y

val Segment.isHorizontal: Boolean
    get() = start.y == end.y && start.x != end.x

fun readWireLayouts(): List<WireLayout> {
    return readTextFileFromResourcesForDay(3)
//    return "R8,U5,L5,D3\nU7,R6,D4,L4"
//    return "R75,D30,R83,U83,L12,D49,R71,U7,L72\nU62,R66,U55,R34,D71,R55,D58,R83"//readTextFileFromResourcesForDay(3)
//    return "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51\nU98,R91,D20,R16,D67,R40,U7,R15,U6,R7"//readTextFileFromResourcesForDay(3)
        .split("\n")
        .map { line -> readWireLayout(line) }
}

fun readWireLayout(line: String): WireLayout {
    return line.split(",")
        .map { drawCommandAsString -> drawCommandAsString.parseDrawCommand() }
}

private fun String.parseDrawCommand(): DrawCommand {
    val direction = when(this[0]) {
        'U' -> DrawDirection.UP
        'D' -> DrawDirection.DOWN
        'L' -> DrawDirection.LEFT
        'R' -> DrawDirection.RIGHT
        else -> {
            throw RuntimeException("Incorrect direction letter in $this!")
        }
    }
    val steps = this.substring(1).toInt()
    return DrawCommand(direction, steps)
}

typealias WireLayout = List<DrawCommand>

data class DrawCommand(val direction: DrawDirection,
                       val steps: Int)

enum class DrawDirection {
    UP, DOWN, LEFT, RIGHT
}
